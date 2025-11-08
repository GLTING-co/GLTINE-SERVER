package glting.server.exception.code;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExceptionCodeMapper {
    private static final Map<String, String> BAD_REQUEST_MAP = new ConcurrentHashMap<>();
    private static final Map<String, String> CONFLICT_MAP = new ConcurrentHashMap<>();
    private static final Map<String, String> NOT_FOUND_MAP = new ConcurrentHashMap<>();
    private static final Map<String, String> SERVER_MAP = new ConcurrentHashMap<>();
    private static final Map<String, String> UNAUTHORIZED_MAP = new ConcurrentHashMap<>();

    static {
        // BadRequestException
        BAD_REQUEST_MAP.put("요청 데이터 오류입니다.", "BAD_REQUEST_EXCEPTION_001");
        BAD_REQUEST_MAP.put("이미 회원가입된 사용자입니다.", "BAD_REQUEST_EXCEPTION_002");
        BAD_REQUEST_MAP.put("type 종류가 잘못됐습니다.", "BAD_REQUEST_EXCEPTION_003");

        // ConflictException
        CONFLICT_MAP.put("이미 회원가입된 사용자입니다.", "CONFLICT_EXCEPTION_001");
        CONFLICT_MAP.put("동일한 소셜 ID로 이미 가입된 사용자가 존재합니다.", "CONFLICT_EXCEPTION_002");
        CONFLICT_MAP.put("다른 요청이 먼저 수정했습니다. 다시 시도해주세요.", "CONFLICT_EXCEPTION_003");

        // NotFoundException
        NOT_FOUND_MAP.put("존재하지 않는 회원입니다.", "NOT_FOUND_EXCEPTION_001");
        NOT_FOUND_MAP.put("존재하지 않는 채팅방입니다.", "NOT_FOUND_EXCEPTION_002");
        NOT_FOUND_MAP.put("존재하지 않는 요청자 SEQ입니다.", "NOT_FOUND_EXCEPTION_002");

        // ServerException
        SERVER_MAP.put("카카오 로그인 요청 시 토큰 정보 수집 오류가 발생했습니다.", "SERVER_EXCEPTION_001");
        SERVER_MAP.put("카카오 로그인 요청 시 사용자 정보 수집 오류가 발생했습니다.", "SERVER_EXCEPTION_002");
        SERVER_MAP.put("카카오 로그아웃 호출 중 오류가 발생했습니다.", "SERVER_EXCEPTION_003");
        SERVER_MAP.put("네이버 로그인 요청 시 토큰 정보 수집 오류가 발생했습니다.", "SERVER_EXCEPTION_004");
        SERVER_MAP.put("네이버 로그인 요청 시 사용자 정보 수집 오류가 발생했습니다.", "SERVER_EXCEPTION_005");
        SERVER_MAP.put("네이버 로그아웃 호출 중 오류가 발생했습니다.", "SERVER_EXCEPTION_006");

        // UnauthorizedException
        UNAUTHORIZED_MAP.put("만료된 JWT 입니다.", "UNAUTHORIZED_EXCEPTION_002");
        UNAUTHORIZED_MAP.put("잘못된 JWT 입니다.", "UNAUTHORIZED_EXCEPTION_003");
        UNAUTHORIZED_MAP.put("ACCESS 토큰만 사용할 수 있습니다.", "UNAUTHORIZED_EXCEPTION_004");
        UNAUTHORIZED_MAP.put("REFRESH 토큰만 사용할 수 있습니다.", "UNAUTHORIZED_EXCEPTION_005");
        UNAUTHORIZED_MAP.put("로그아웃된 토큰입니다.", "UNAUTHORIZED_EXCEPTION_006");
        UNAUTHORIZED_MAP.put("이미 사용된 Refresh Token입니다.", "UNAUTHORIZED_EXCEPTION_007");
        UNAUTHORIZED_MAP.put("유효하지 않은 토큰입니다.", "UNAUTHORIZED_EXCEPTION_008");
        UNAUTHORIZED_MAP.put("유효하지 않은 Refresh Token입니다.", "UNAUTHORIZED_EXCEPTION_009");
        UNAUTHORIZED_MAP.put("JWT 토큰 처리 중 오류가 발생했습니다.", "UNAUTHORIZED_EXCEPTION_010");
        UNAUTHORIZED_MAP.put("존재하지 않는 요청자 SEQ입니다.", "UNAUTHORIZED_EXCEPTION_011");
    }

    public static String getCode(String message, ExceptionType type) {
        return switch (type) {
            case BAD_REQUEST -> BAD_REQUEST_MAP.getOrDefault(message, "BAD_REQUEST_EXCEPTION_예외코드 설정하세요.");
            case CONFLICT -> CONFLICT_MAP.getOrDefault(message, "CONFLICT_EXCEPTION_예외코드 설정하세요.");
            case NOT_FOUND -> NOT_FOUND_MAP.getOrDefault(message, "NOT_FOUND_EXCEPTION_예외코드 설정하세요.");
            case SERVER -> SERVER_MAP.getOrDefault(message, "SERVER_EXCEPTION_예외코드 설정하세요.");
            case UNAUTHORIZED -> UNAUTHORIZED_MAP.getOrDefault(message, "UNAUTHORIZED_EXCEPTION_예외코드 설정하세요.");
        };
    }

    public enum ExceptionType {
        BAD_REQUEST,
        CONFLICT,
        NOT_FOUND,
        SERVER,
        UNAUTHORIZED
    }
}