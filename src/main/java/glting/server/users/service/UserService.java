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
                    "동일한 소셜 ID로 이미 가입된 사용자가 존재합니다.",
                    getCode("동일한 소셜 ID로 이미 가입된 사용자가 존재합니다.", ExceptionType.CONFLICT)
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

    @Transactional
    public void update(Long userSeq, UpdateUserRequest request, List<MultipartFile> images) {
        UserEntity userEntity = userRepository.findByUserSeq(userSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        try {
            userImageRepository.deleteAllByUserSeq(userSeq);
            List<String> imageUrls = commonService.uploadJPGFileList(images);
            userImageRepository.saveAllUserImageUrls(userEntity.getUserSeq(), imageUrls);

            userEntity.updateUser(
                    request.bio(),
                    request.height(),
                    request.job(),
                    request.company(),
                    request.school(),
                    request.city(),
                    request.smoking(),
                    request.drinking(),
                    request.religion()
            );
            userRepository.saveUserEntity(userEntity);
        } catch (OptimisticLockException e) {
            throw new ConflictException(
                    HttpStatus.CONFLICT.value(),
                    "다른 요청이 먼저 수정했습니다. 다시 시도해주세요.",
                    getCode("다른 요청이 먼저 수정했습니다. 다시 시도해주세요.", ExceptionType.CONFLICT)
            );
        } catch (Exception e) {
            throw new ServerException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    getCode(e.getMessage(), ExceptionType.SERVER)
            );
        }
    }

    @Transactional(readOnly = true)
    public GetUserResponse get(Long userSeq) {
        UserEntity userEntity = userRepository.findByUserSeq(userSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        return new GetUserResponse(
                userEntity.getName(),
                userEntity.getBirth(),
                userEntity.getGender(),
                userEntity.getSexualType(),
                userEntity.getRelationship(),
                userEntity.getBio(),
                userEntity.getHeight(),
                userEntity.getJob(),
                userEntity.getCompany(),
                userEntity.getSchool(),
                userEntity.getCity(),
                userEntity.getSmoking(),
                userEntity.getDrinking(),
                userEntity.getReligion()
        );
    }

    @Transactional
    public void delete(Long userSeq) {
        UserEntity userEntity = userRepository.findByUserSeq(userSeq)
                .orElseThrow(() -> new NotFoundException(
                        HttpStatus.NOT_FOUND.value(),
                        "존재하지 않는 회원입니다.",
                        getCode("존재하지 않는 회원입니다.", ExceptionType.NOT_FOUND)
                ));

        userRepository.deleteUserEntity(userEntity);
    }

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

        if (!commonService.isTokenInWhiteList(userSeq, request.refreshToken())) {
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
        commonService.saveToken(userSeq, "WHITE", refreshToken);

        return new ReIssueTokenResponse(accessToken, refreshToken);
    }
}
