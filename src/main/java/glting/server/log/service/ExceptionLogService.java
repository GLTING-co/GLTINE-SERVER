package glting.server.log.service;

import glting.server.log.domain.ExceptionLogEntity;
import glting.server.log.dto.ExceptionLogDto;
import glting.server.log.repository.ExceptionLogEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExceptionLogService {
    private final ExceptionLogEntityRepository exceptionLogEntityRepository;

    /**
     * 예외 로그를 저장합니다.
     *
     * @param status HTTP 상태 코드
     * @param code 예외 코드
     * @param message 예외 메시지
     * @param uri 요청 URI
     * @param methodName 예외가 발생한 메서드명
     * @param logDetail 요청 본문 상세 정보
     */
    public void saveExceptionLogEntity(int status, String code, String message, String uri, String methodName, String logDetail) {
        ExceptionLogEntity exceptionLogEntity = ExceptionLogEntity.builder()
                .httpStatus(status)
                .errorCode(code)
                .message(message)
                .requestUri(uri)
                .methodName(methodName)
                .requestBody(logDetail)
                .build();

        exceptionLogEntityRepository.saveExceptionLogEntity(exceptionLogEntity);
    }

    /**
     * 예외 로그 목록을 페이징하여 조회합니다.
     *
     * @param start 시작 인덱스 (0부터 시작)
     * @param end 종료 인덱스 (포함하지 않음)
     * @return 예외 로그 DTO 목록 (생성일시 내림차순 정렬)
     */
    public List<ExceptionLogDto> getAllExceptionLogEntity(int start, int end) {
        return exceptionLogEntityRepository.findAll(start, end).stream()
                .map(log -> new ExceptionLogDto(
                        log.getCreatedAt(),
                        log.getHttpStatus(),
                        log.getErrorCode(),
                        log.getMessage(),
                        log.getRequestUri(),
                        log.getRequestBody(),
                        log.getMethodName()
                ))
                .collect(Collectors.toList());
    }
}
