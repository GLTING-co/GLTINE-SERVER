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
import java.util.*;
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
            String key = "";

            if (type.equalsIgnoreCase("WHITE")) key = String.format(WHITE_KEY_FMT, userSeq);
            if (type.equalsIgnoreCase("BLACK")) key = String.format(BLACK_KEY_FMT, userSeq);

            redisTemplate.opsForValue().set(
                    key,
                    token,
                    TTL,
                    TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            throw new ServerException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionType.SERVER)
            );
        }
    }

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

    public List<String> uploadJPGFileList(List<MultipartFile> multipartFiles) {
        try {
            String fileName = randomUUID().toString() + ".jpg";

            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile multipartFile : multipartFiles) {
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
