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
