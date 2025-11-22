package glting.server.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import glting.server.exception.ServerException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static glting.server.common.util.CommonUtil.*;
import static glting.server.exception.code.ExceptionCodeMapper.*;
import static glting.server.exception.code.ExceptionCodeMapper.getCode;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class CommonService {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpiration;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3client;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 지정된 만료 시간과 사용자 정보를 기반으로 JWT 토큰을 발급합니다.
     *
     * @param userSeq 사용자 고유 식별자(PK)
     * @param type    토큰 유형 (예: "ACCESS", "REFRESH")
     * @param social  소셜 로그인 타입 (예: "Kakao", "Google", "Naver")
     * @return 서명된 JWT 토큰 문자열
     */
    public String issueToken(Long userSeq, String type, String social) {
        long now = System.currentTimeMillis();
        Date exp = null;
        if (type.equalsIgnoreCase("ACCESS")) exp = new Date(now + accessTokenExpiration);
        if (type.equalsIgnoreCase("REFRESH")) exp = new Date(now + refreshTokenExpiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userSeq", userSeq);
        claims.put("social", social);
        claims.put("type", type.toUpperCase());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userSeq))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(now))
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }


    /**
     * JWT 토큰을 Redis에 저장합니다.
     *
     * @param userSeq Redis 키로 사용할 사용자 식별자
     * @param type    저장 유형 ("WHITE" 또는 "BLACK") — 현재 구현은 키 프리픽스 구분에 사용됩니다
     * @param token   저장할 JWT 토큰 문자열
     */
    public void saveToken(Long userSeq, String type, String token) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            Date exp = body.getExpiration();
            long now = System.currentTimeMillis();
            long TTL = exp.getTime() - now;
            
            if (TTL <= 0) {
                throw new ServerException(
                        HttpStatus.BAD_REQUEST.value(),
                        "Token expiration time is invalid or already expired",
                        getCode("Token expiration time is invalid or already expired", ExceptionType.SERVER)
                );
            }
            
            String key = "";

            if (type.equalsIgnoreCase("WHITE")) key = String.format(WHITE_KEY_FMT, userSeq);
            if (type.equalsIgnoreCase("BLACK")) key = String.format(BLACK_KEY_FMT, userSeq);

            if (key.isEmpty()) {
                throw new ServerException(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid token type: " + type,
                        getCode("Invalid token type", ExceptionType.SERVER)
                );
            }

            redisTemplate.opsForValue().set(
                    key,
                    token,
                    TTL,
                    TimeUnit.MILLISECONDS
            );
            
            // 저장 확인
            String savedValue = redisTemplate.opsForValue().get(key);
            if (savedValue == null || !savedValue.equals(token)) {
                throw new ServerException(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Failed to save token to Redis",
                        getCode("Failed to save token to Redis", ExceptionType.SERVER)
                );
            }
        } catch (ServerException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionType.SERVER)
            );
        }
    }

    /**
     * Redis에서 토큰을 삭제합니다.
     *
     * @param userSeq 사용자 고유 식별자(PK)
     * @param type    삭제할 리스트 타입 ("WHITE" 또는 "BLACK")
     *                - "WHITE": auth:whitelist:{userSeq} 키 삭제
     *                - "BLACK": auth:blacklist:{userSeq} 키 삭제
     */
    public void deleteToken(Long userSeq, String type) {
        String key = "";
        if (type.equalsIgnoreCase("WHITE")) key = String.format(WHITE_KEY_FMT, userSeq);
        if (type.equalsIgnoreCase("BLACK")) key = String.format(BLACK_KEY_FMT, userSeq);

        redisTemplate.delete(key);
    }

    /**
     * JWT 토큰이 WHITE 리스트에 있는지 확인합니다.
     *
     * @param userSeq 사용자 고유 식별자(PK)
     * @param token   확인할 JWT 토큰 문자열
     * @return WHITE 리스트에 토큰이 존재하면 true, 아니면 false
     */
    public boolean isTokenInWhiteList(Long userSeq, String token) {
        String key = String.format(WHITE_KEY_FMT, userSeq);
        String storedToken = redisTemplate.opsForValue().get(key);
        return storedToken != null && storedToken.equals(token);
    }

    /**
     * JWT 토큰이 BLACK 리스트에 있는지 확인합니다.
     *
     * @param userSeq 사용자 고유 식별자(PK)
     * @param token   확인할 JWT 토큰 문자열
     * @return BLACK 리스트에 토큰이 존재하면 true, 아니면 false
     */
    public boolean isTokenInBlackList(Long userSeq, String token) {
        String key = String.format(BLACK_KEY_FMT, userSeq);
        String storedToken = redisTemplate.opsForValue().get(key);
        return storedToken != null && storedToken.equals(token);
    }

    /**
     * BLACK 리스트에서 토큰을 가져옵니다.
     *
     * @param userSeq 사용자 고유 식별자(PK)
     * @return BLACK 리스트에 저장된 토큰, 없으면 null
     */
    public String getTokenFromBlackList(Long userSeq) {
        String key = String.format(BLACK_KEY_FMT, userSeq);
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * JWT 토큰에서 Claims를 추출합니다.
     *
     * @param token JWT 토큰 문자열
     * @return Claims 객체
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 단일 이미지 파일을 S3에 업로드합니다.
     *
     * @param multipartFile 업로드할 이미지 파일
     * @return S3에 업로드된 파일의 URL
     */
    public String uploadJPGFile(MultipartFile multipartFile) {
        try {
            String fileName = randomUUID().toString() + ".jpg";
            InputStream inputStream = multipartFile.getInputStream();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            s3client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
            return s3client.getUrl(bucketName, fileName).toString();
        } catch (IOException e) {
            throw new ServerException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionType.SERVER)
            );
        }
    }

    /**
     * 여러 이미지 파일을 S3에 일괄 업로드합니다.
     *
     * @param multipartFiles 업로드할 이미지 파일 목록
     * @return S3에 업로드된 파일들의 URL 목록
     */
    public List<String> uploadJPGFileList(List<MultipartFile> multipartFiles) {
        try {

            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile multipartFile : multipartFiles) {
                String fileName = randomUUID().toString() + ".jpg";

                InputStream inputStream = multipartFile.getInputStream();

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(multipartFile.getSize());
                metadata.setContentType(multipartFile.getContentType());

                s3client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));

                imageUrls.add(s3client.getUrl(bucketName, fileName).toString());
            }

            return imageUrls;
        } catch (IOException e) {
            throw new ServerException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionType.SERVER)
            );
        }
    }
}
