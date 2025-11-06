package glting.server.users.repository;

import glting.server.users.entity.UserImageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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
                INSERT INTO user_image (user_seq, image, deleted)
                VALUES (?, ?, false)
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

    public void deleteAllByUserSeq(Long userSeq) {
        userImageJpaRepository.deleteAllByUserSeq(userSeq);
    }
}
