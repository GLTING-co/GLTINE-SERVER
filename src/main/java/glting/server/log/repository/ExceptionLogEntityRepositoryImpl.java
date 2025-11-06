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

    /**
     * 예외 로그 엔티티를 저장합니다.
     *
     * @param exceptionLogEntity 저장할 예외 로그 엔티티
     * @return 저장된 예외 로그 엔티티
     */
    @Override
    public ExceptionLogEntity saveExceptionLogEntity(ExceptionLogEntity exceptionLogEntity) {
        return repository.save(exceptionLogEntity);
    }

    /**
     * 예외 로그 목록을 페이징하여 조회합니다.
     * QueryDSL을 사용하여 생성일시 내림차순으로 정렬합니다.
     *
     * @param start 시작 인덱스 (0부터 시작)
     * @param end 종료 인덱스 (포함하지 않음)
     * @return 예외 로그 엔티티 목록 (생성일시 내림차순 정렬)
     */
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
