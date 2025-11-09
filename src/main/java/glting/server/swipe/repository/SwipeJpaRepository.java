package glting.server.swipe.repository;

import glting.server.swipe.entity.SwipeEntity;
import glting.server.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SwipeJpaRepository extends JpaRepository<SwipeEntity, Long> {

    Optional<SwipeEntity> findByFromUserSeq(UserEntity userEntity);
}
