package glting.server.users.repository;

import glting.server.users.entity.UserImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserImageJpaRepository extends JpaRepository<UserImageEntity, Integer> {

    @Modifying
    @Query("""
            UPDATE UserImageEntity ui
            SET ui.deleted = true
            WHERE ui.image IN :images
            AND ui.deleted = false
            """)
    void deleteImages(@Param("images") List<String> images);

    @Query("""
            SELECT ui
            FROM UserImageEntity ui
            WHERE ui.userEntity.userSeq = :userSeq AND ui.deleted = false
            ORDER BY ui.userImageSeq ASC
            LIMIT 1
            """)
    Optional<UserImageEntity> findRepresentImageByUserSeq(@Param("userSeq") Long userSeq);

    @Query("""
            SELECT ui.image
            FROM UserImageEntity ui
            WHERE ui.userEntity.userSeq = :userSeq AND ui.deleted = false
            ORDER BY ui.userImageSeq ASC
            """)
    List<String> findAllImagesByUserSeq(@Param("userSeq") Long userSeq);

    @Query("""
            SELECT ui
            FROM UserImageEntity ui
            WHERE ui.userEntity.userSeq IN :userSeqs
            AND ui.deleted = false
            AND ui.userImageSeq = (
                SELECT MIN(ui2.userImageSeq)
                FROM UserImageEntity ui2
                WHERE ui2.userEntity.userSeq = ui.userEntity.userSeq
                AND ui2.deleted = false
            )
            """)
    List<UserImageEntity> findRepresentImagesByUserSeqs(@Param("userSeqs") List<Long> userSeqs);
}
