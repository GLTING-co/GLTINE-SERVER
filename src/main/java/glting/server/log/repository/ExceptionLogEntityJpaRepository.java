package glting.server.log.repository;

import glting.server.log.domain.ExceptionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExceptionLogEntityJpaRepository extends JpaRepository<ExceptionLogEntity, Long> {
}
