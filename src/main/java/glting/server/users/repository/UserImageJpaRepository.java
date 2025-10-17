package glting.server.users.repository;

import glting.server.users.entity.UserImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImageJpaRepository extends JpaRepository<UserImageEntity, Integer> {

}
