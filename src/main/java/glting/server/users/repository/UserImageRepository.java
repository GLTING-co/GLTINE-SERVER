package glting.server.users.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserImageRepository {
    private final UserImageJpaRepository userImageJpaRepository;
    private final JdbcTemplate jdbcTemplate;

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
}
