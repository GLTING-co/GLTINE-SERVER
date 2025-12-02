package glting.server.users.repository;

import glting.server.users.entity.UserImageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserImageRepository {
    private final UserImageJpaRepository userImageJpaRepository;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 사용자 이미지 URL 목록을 일괄 저장합니다.
     *
     * @param userSeq   사용자 식별자
     * @param imageUrls S3 등에 업로드된 이미지 URL 목록
     */
    public void saveAllUserImageUrls(Long userSeq, List<String> imageUrls) {
        final String sql = """
                INSERT INTO user_image (user_seq, image, deleted, created_at, updated_at)
                VALUES (?, ?, false, NOW(), NOW())
                """;

        jdbcTemplate.batchUpdate(
                sql,
                imageUrls,
                imageUrls.size(),
                (ps, url) -> {
                    ps.setLong(1, userSeq);
                    ps.setString(2, url);
                }
        );
    }

    /**
     * 이미지 URL 목록에 해당하는 사용자 이미지를 삭제합니다.
     *
     * @param images 삭제할 이미지 URL 목록
     */
    public void deleteImages(List<String> images) {
        userImageJpaRepository.deleteImages(images);
    }

    /**
     * 사용자 고유 식별자로 대표 이미지를 조회합니다.
     * userImageSeq가 가장 작은 이미지를 대표 이미지로 반환합니다.
     *
     * @param userSeq 사용자 고유 식별자(PK)
     * @return 대표 이미지 엔티티 (존재하지 않으면 null)
     */
    public Optional<UserImageEntity> findRepresentImageByUserSeq(Long userSeq) {
        return userImageJpaRepository.findRepresentImageByUserSeq(userSeq);
    }

    /**
     * 사용자의 모든 이미지 URL 목록을 조회합니다.
     *
     * @param userSeq 사용자 고유 식별자(PK)
     * @return 이미지 URL 목록
     */
    public List<String> findAllImagesByUserSeq(Long userSeq) {
        return userImageJpaRepository.findAllImagesByUserSeq(userSeq);
    }
}
