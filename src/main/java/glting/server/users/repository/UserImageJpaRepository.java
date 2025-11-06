package glting.server.users.repository;

import glting.server.users.entity.UserImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImageJpaRepository extends JpaRepository<UserImageEntity, Integer> {

    /**
     * userSeq에 해당하는 모든 UserImage를 삭제합니다. (Soft Delete)
     * deleted = true로 업데이트하여 논리적 삭제를 수행합니다.
     *
     * @param userSeq 사용자 고유 식별자(PK)
     */
    @Modifying
    @Query("""
            UPDATE UserImageEntity ui
            SET ui.deleted = true
            WHERE ui.userEntity.userSeq = :userSeq AND ui.deleted = false
            """)
    void deleteAllByUserSeq(@Param("userSeq") Long userSeq);

    @Query("""
            SELECT ui
            FROM UserImageEntity ui
            WHERE ui.userEntity.userSeq = :userSeq AND ui.deleted = false
            ORDER BY ui.userImageSeq DESC
            LIMIT 1
            """)
    UserImageEntity findRepresentImageByUserSeq(@Param("userSeq") Long userSeq);
}
