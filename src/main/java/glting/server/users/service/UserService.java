package glting.server.users.service;

import glting.server.common.service.CommonService;
import glting.server.exception.ConflictException;
import glting.server.exception.NotFoundException;
import glting.server.exception.ServerException;
import glting.server.exception.UnauthorizedException;
import glting.server.users.entity.UserEntity;
import glting.server.users.repository.UserImageRepository;
import glting.server.users.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static glting.server.exception.code.ExceptionCodeMapper.*;
import static glting.server.exception.code.ExceptionCodeMapper.getCode;
import static glting.server.users.controller.request.UserRequest.*;
import static glting.server.users.controller.response.UserResponse.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;

    private final CommonService commonService;

    /**
     * 비회원(소셜 로그인 미가입자) 회원가입을 처리합니다.
     *
     * @param request 회원가입 요청 데이터 (소셜 타입, 이름, 성별 등)
     * @param images  프로필 이미지 MultipartFile 리스트
     */
    @Transactional
    public void register(NoAccountRequest request, List<MultipartFile> images) {
        userRepository.findBySocialId(request.id(), request.type().toUpperCase())
                .ifPresent(userEntity -> {
                    throw new ConflictException(
                            HttpStatus.CONFLICT.value(),
                            "이미 회원가입된 사용자입니다.",
                            getCode("이미 회원가입된 사용자입니다.", ExceptionType.CONFLICT)
                    );
                });

        try {
            UserEntity userEntity = UserEntity.builder()
                    .name(request.name())
                    .birth(request.birth())
                    .gender(request.gender())
                    .sexualType(request.sexualType())
                    .relationship(request.relationship())
                    .build();
            userEntity.updateSocialId(request.type().toUpperCase(), request.id());
            UserEntity savedUserEntity = userRepository.saveUserEntity(userEntity);

            List<String> imageUrls = commonService.uploadJPGFileList(images);
            userImageRepository.saveAllUserImageUrls(savedUserEntity.getUserSeq(), imageUrls);

        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    HttpStatus.CONFLICT.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionType.CONFLICT)
            );
        } catch (Exception e) {
            throw new ServerException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionType.SERVER)
            );
        }
    }

    /**
     * 로그아웃 처리: WHITE 리스트에서 삭제 후 BLACK 리스트에 추가
     *
     * @param userSeq      사용자 고유 식별자(PK)
     * @param accessToken  AccessToken
     * @param refreshToken RefreshToken
     */
    @Transactional
    public void logout(Long userSeq, String accessToken, String refreshToken) {
        commonService.deleteToken(userSeq, "WHITE");
        commonService.saveToken(userSeq, "BLACK", accessToken);
        commonService.saveToken(userSeq, "BLACK", refreshToken);
    }

    /**
     * 사용자 프로필 정보를 업데이트합니다.
     *
     * @param userSeq   사용자 고유 식별자(PK)
     * @param request   업데이트할 사용자 정보 (bio, height, job, company, school, city, smoking, drinking, religion, open)
     * @param newImages 새로운 프로필 이미지 파일 목록
     */
    @Transactional
    public void update(Long userSeq, UpdateUserRequest request, List<MultipartFile> newImages) {
        UserEntity userEntity = userRepository.findByUserSeq(userSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        try {
            userImageRepository.deleteImages(request.removeImages());
            List<String> imageUrls = commonService.uploadJPGFileList(newImages);
            userImageRepository.saveAllUserImageUrls(userEntity.getUserSeq(), imageUrls);

            userEntity.updateUser(
                    request.bio(), request.height(), request.weight(),
                    request.job(), request.company(), request.school(), request.city(),
                    request.smoking(), request.drinking(), request.religion(), request.open()
            );
            userRepository.saveUserEntity(userEntity);
        } catch (OptimisticLockException e) {
            throw new ConflictException(
                    HttpStatus.CONFLICT.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionType.CONFLICT)
            );
        } catch (Exception e) {
            throw new ServerException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionType.SERVER)
            );
        }
    }

    /**
     * 사용자 프로필 정보를 조회합니다.
     *
     * @param userSeq 사용자 고유 식별자(PK)
     * @return 사용자 프로필 정보 (이름, 생년월일, 성별, 성향, 관계 상태, 자기소개, 키, 직업, 회사, 학교, 도시, 흡연, 음주, 종교, 공개 여부)
     */
    @Transactional(readOnly = true)
    public GetUserResponse get(Long userSeq) {
        UserEntity userEntity = userRepository.findByUserSeq(userSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        return new GetUserResponse(
                userEntity.getName(), userEntity.getBirth(), userEntity.getGender(),
                userEntity.getSexualType(), userEntity.getRelationship(), userEntity.getBio(), userEntity.getHeight(),
                userEntity.getJob(), userEntity.getCompany(), userEntity.getSchool(), userEntity.getCity(),
                userEntity.getSmoking(), userEntity.getDrinking(), userEntity.getReligion(), userEntity.getOpen()
        );
    }

    /**
     * 호스트 사용자와 조회 대상 사용자를 검증한 후 사용자 프로필 정보를 조회합니다.
     *
     * @param hostSeq 요청한 사용자 고유 식별자(PK) - 검증용
     * @param userSeq 조회할 사용자 고유 식별자(PK)
     * @return 사용자 프로필 정보 (이름, 생년월일, 성별, 성향, 관계 상태, 자기소개, 키, 직업, 회사, 학교, 도시, 흡연, 음주, 종교, 공개 여부)
     */
    @Transactional(readOnly = true)
    public GetUserResponse get(Long hostSeq, Long userSeq) {
        userRepository.findByUserSeq(hostSeq)
                .orElseThrow(() -> new UnauthorizedException(
                        HttpStatus.UNAUTHORIZED.value(),
                        "존재하지 않는 요청자 SEQ입니다.",
                        getCode("존재하지 않는 요청자 SEQ입니다.", ExceptionType.UNAUTHORIZED)
                ));

        return get(userSeq);
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 재발급합니다.
     * Refresh Token Rotation 패턴을 사용하여 기존 Refresh Token은 BLACK 리스트에 추가됩니다.
     *
     * @param request Refresh Token이 포함된 요청
     * @return 새로 발급된 Access Token과 Refresh Token
     */
    @Transactional
    public ReIssueTokenResponse reissueToken(ReIssueTokenRequest request) {
        var claims = commonService.parseToken(request.refreshToken());
        String type = claims.get("type", String.class);
        Long userSeq = ((Number) claims.get("userSeq")).longValue();
        String social = claims.get("social", String.class);

        if (!"REFRESH".equalsIgnoreCase(type)) {
            throw new UnauthorizedException(
                    HttpStatus.UNAUTHORIZED.value(),
                    "REFRESH 토큰만 사용할 수 있습니다.",
                    getCode("REFRESH 토큰만 사용할 수 있습니다.", ExceptionType.UNAUTHORIZED)
            );
        }

        userRepository.findByUserSeq(userSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        if (commonService.isTokenInWhiteList(userSeq, request.refreshToken())) {
            throw new UnauthorizedException(
                    HttpStatus.UNAUTHORIZED.value(),
                    "유효하지 않은 Refresh Token입니다.",
                    getCode("유효하지 않은 Refresh Token입니다.", ExceptionType.UNAUTHORIZED)
            );
        }

        if (commonService.isTokenInBlackList(userSeq, request.refreshToken())) {
            throw new UnauthorizedException(
                    HttpStatus.UNAUTHORIZED.value(),
                    "이미 사용된 Refresh Token입니다.",
                    getCode("이미 사용된 Refresh Token입니다.", ExceptionType.UNAUTHORIZED)
            );
        }

        commonService.deleteToken(userSeq, "WHITE");
        commonService.saveToken(userSeq, "BLACK", request.refreshToken());

        String accessToken = commonService.issueToken(userSeq, "ACCESS", social);
        String refreshToken = commonService.issueToken(userSeq, "REFRESH", social);

        commonService.saveToken(userSeq, "WHITE", accessToken);

        return new ReIssueTokenResponse(accessToken, refreshToken);
    }
}
