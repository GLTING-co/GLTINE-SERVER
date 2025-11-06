package glting.server.users.service;

import glting.server.common.service.CommonService;
import glting.server.exception.ConflictException;
import glting.server.exception.NotFoundException;
import glting.server.exception.ServerException;
import glting.server.users.entity.UserEntity;
import glting.server.users.repository.UserImageRepository;
import glting.server.users.repository.UserRepository;
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
                            getCode("이미 회원가입된 사용자입니다.", ExceptionType.BAD_REQUEST)
                    );
                });

        try {
            UserEntity userEntity = UserEntity.builder()
                    .name(request.name())
                    .birth(request.birth())
                    .gender(request.gender())
                    .sexualType(request.sexualType())
                    .relationship(request.relationship())
                    .deleted(false)
                    .build();
            userEntity.updateSocialId(request.type().toUpperCase(), request.id());
            UserEntity savedUserEntity = userRepository.saveUserEntity(userEntity);

            List<String> imageUrls = commonService.uploadJPGFileList(images);
            userImageRepository.saveAllUserImageUrls(savedUserEntity.getUserSeq(), imageUrls);

        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    HttpStatus.CONFLICT.value(),
                    "동일한 소셜 ID로 이미 가입된 사용자가 존재합니다.",
                    getCode("동일한 소셜 ID로 이미 가입된 사용자가 존재합니다.", ExceptionType.BAD_REQUEST)
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
    }
}
