package glting.server.log.repository;

import glting.server.log.domain.ExceptionLogEntity;

import java.util.List;

public interface ExceptionLogEntityRepository {
    /**
     * 예외 로그 엔티티를 저장합니다.
     *
     * @param exceptionLogEntity 저장할 예외 로그 엔티티
     * @return 저장된 예외 로그 엔티티
     */
    ExceptionLogEntity saveExceptionLogEntity(ExceptionLogEntity exceptionLogEntity);

    /**
     * 예외 로그 목록을 페이징하여 조회합니다.
     *
     * @param start 시작 인덱스 (0부터 시작)
     * @param end 종료 인덱스 (포함하지 않음)
     * @return 예외 로그 엔티티 목록 (생성일시 내림차순 정렬)
     */
    List<ExceptionLogEntity> findAll(int start, int end);
}
