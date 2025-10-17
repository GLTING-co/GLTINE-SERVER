package glting.server.log.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import glting.server.log.domain.ExceptionLogEntity;
import glting.server.log.domain.QExceptionLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExceptionLogEntityRepositoryImpl implements ExceptionLogEntityRepository {
    private final ExceptionLogEntityJpaRepository repository;
    private final JPAQueryFactory queryFactory;

    @Override
    public ExceptionLogEntity saveExceptionLogEntity(ExceptionLogEntity exceptionLogEntity) {
        return repository.save(exceptionLogEntity);
    }

    @Override
    public List<ExceptionLogEntity> findAll(int start, int end) {
        QExceptionLogEntity qExceptionLogEntity = QExceptionLogEntity.exceptionLogEntity;

        return queryFactory
                .selectFrom(qExceptionLogEntity)
                .orderBy(qExceptionLogEntity.createdAt.desc())
                .offset(start)
                .limit(end - start)
                .fetch();
    }
}
