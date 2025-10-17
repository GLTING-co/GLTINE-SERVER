package glting.server.log.repository;

import glting.server.log.domain.ExceptionLogEntity;

import java.util.List;

public interface ExceptionLogEntityRepository {
    ExceptionLogEntity saveExceptionLogEntity(ExceptionLogEntity exceptionLogEntity);

    List<ExceptionLogEntity> findAll(int start, int end);
}
