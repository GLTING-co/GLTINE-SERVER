package glting.server.users.controller;

import glting.server.base.BaseResponse;
import glting.server.exception.BadRequestException;
import glting.server.exception.handler.GlobalExceptionHandler;
import glting.server.social.kakao.service.KakaoService;
import glting.server.social.naver.service.NaverService;
import glting.server.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

import static glting.server.exception.code.ExceptionCodeMapper.*;
import static glting.server.exception.code.ExceptionCodeMapper.getCode;
import static glting.server.users.controller.request.UserRequest.*;
import static glting.server.users.controller.response.UserResponse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-service")
@Tag(name = "소셜", description = "담당자(박종훈)")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;
    private final NaverService naverService;

    @GetMapping("/DESCRPTION")
    @Operation(
            operationId = "1",
            summary = "소셜 로그인/회원가입 API Description - 필독!",
            description = """
                    소셜 로그인 시, 회원가입 여부에 따라 반환값이 다릅니다.
                    <br><br>
                    ### 회원가입이 되어 있는 사용자의 경우
                    ```
                    {
                        "accessToken": "string",
                        "refreshToken": "string"
                    }
                    ```
                    ### 회원가입이 되어 있지 않은 경우
                    ```
                    {
                        "type": "string",
                        "id": "Long"
                    }
                    ```
                    <br>
                    회원가입이 되어 있지 않은 경우, 어플 회원 정보 (이름, 성별 .. etc)를 입력 받은 후 해당 정보들과 함께 type(Google, Kakao, Naver), id 데이터를 함께 보내주어야합니다.
                    """
    )
    public ResponseEntity<BaseResponse<String>> description(HttpServletRequest httpServletRequest) {
        Long userSeq = (Long) httpServletRequest.getAttribute("userSeq");
        String type = (String) httpServletRequest.getAttribute("type");
        String accessToken = (String) httpServletRequest.getAttribute("accessToken");

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), userSeq + type + accessToken));
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "소셜로그인 비회원 회원가입 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "BAD_REQUEST_EXCEPTION_001", description = "요청 데이터 오류입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "BAD_REQUEST_EXCEPTION_002", description = "이미 회원가입된 사용자입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "CONFLICT_EXCEPTION_002", description = "동일한 소셜 ID로 이미 가입된 사용자가 존재합니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "SERVER_EXCEPTION_예외코드 설정하세요.", description = "서버 내부 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<String>> register(
            @RequestPart(value = "request", required = false) NoAccountRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        if (request == null) {
            throw new BadRequestException(
                    HttpStatus.BAD_REQUEST.value(),
                    "요청 데이터 오류입니다.",
                    getCode("요청 데이터 오류입니다.", ExceptionType.BAD_REQUEST)
            );
        }

        if (images.isEmpty()) {
            throw new BadRequestException(
                    HttpStatus.BAD_REQUEST.value(),
                    "요청 데이터 오류입니다.",
                    getCode("요청 데이터 오류입니다.", ExceptionType.BAD_REQUEST)
            );
        }

        if (!(request.type().equalsIgnoreCase("KAKAO") || request.type().equalsIgnoreCase("NAVER") || request.type().equalsIgnoreCase("GOOGLE"))) {
            throw new BadRequestException(
                    HttpStatus.BAD_REQUEST.value(),
                    "요청 데이터 오류입니다.",
                    getCode("요청 데이터 오류입니다.", ExceptionType.BAD_REQUEST)
            );
        }

        userService.register(request, images);
        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), "SUCCESS"));
    }

    @PostMapping("/login/kakao")
    @Operation(summary = "카카오 로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "SERVER_EXCEPTION_001", description = "카카오 로그인 요청 시 토큰 정보 수집 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "SERVER_EXCEPTION_002", description = "카카오 로그인 요청 시 사용자 정보 수집 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public Mono<BaseResponse<?>> loginKakao(@RequestBody SocialLoginRequest request) {
        return kakaoService.loginKakao(request.accessToken())
                .map(response -> BaseResponse.ofSuccess(HttpStatus.OK.value(), response));
    }

    @PostMapping("/login/naver")
    @Operation(summary = "네이버 로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "SERVER_EXCEPTION_004", description = "네이버 로그인 요청 시 토큰 정보 수집 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "SERVER_EXCEPTION_005", description = "네이버 로그인 요청 시 사용자 정보 수집 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public Mono<BaseResponse<?>> loginNaver(@RequestBody SocialLoginRequest request) {
        return naverService.loginNaver(request.accessToken())
                .map(response -> BaseResponse.ofSuccess(HttpStatus.OK.value(), response));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "SERVER_EXCEPTION_예외코드 설정하세요.", description = "서버 내부 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<String>> logout(HttpServletRequest httpServletRequest, @RequestBody LogoutUserRequest request) {
        Long userSeq = (Long) httpServletRequest.getAttribute("userSeq");
        String accessToken = (String) httpServletRequest.getAttribute("accessToken");

        userService.logout(userSeq, accessToken, request.refreshToken());

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), "SUCCESS"));
    }

    @PutMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "회원 정보 수정 API",
            description = """
                    ### 요청 파라미터
                    - bio: 자기소개
                    - height: 키
                    - job: 직업
                    - company: 회사
                    - school: 학교
                    - city: 도시
                    - smoking: 흡연 여부
                      * 허용 값: NONE (흡연 안함), OCCASIONAL (가끔), REGULAR (자주)
                    - drinking: 음주 여부
                      * 허용 값: NONE (음주 안함), OCCASIONAL (가끔), REGULAR (자주)
                    - religion: 종교
                      * 허용 값: NONE (무교), CHRISTIAN (기독교), CATHOLIC (천주교), BUDDHIST (불교), ISLAM (이슬람), HINDU (힌두교), OTHER (기타)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "BAD_REQUEST_EXCEPTION_001", description = "요청 데이터 오류입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_001", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "CONFLICT_EXCEPTION_003", description = "다른 요청이 먼저 수정했습니다. 다시 시도해주세요.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<String>> update(
            HttpServletRequest httpServletRequest,
            @RequestPart(value = "request", required = false) UpdateUserRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        if (request == null) {
            throw new BadRequestException(
                    HttpStatus.BAD_REQUEST.value(),
                    "요청 데이터 오류입니다.",
                    getCode("요청 데이터 오류입니다.", ExceptionType.BAD_REQUEST)
            );
        }

        if (images.isEmpty()) {
            throw new BadRequestException(
                    HttpStatus.BAD_REQUEST.value(),
                    "요청 데이터 오류입니다.",
                    getCode("요청 데이터 오류입니다.", ExceptionType.BAD_REQUEST)
            );
        }

        Long userSeq = (Long) httpServletRequest.getAttribute("userSeq");
        userService.update(userSeq, request, images);

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), "SUCCESS"));
    }

    @GetMapping()
    @Operation(summary = "회원 정보 개별 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_001", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<GetUserResponse>> get(HttpServletRequest httpServletRequest) {
        Long userSeq = (Long) httpServletRequest.getAttribute("userSeq");
        GetUserResponse response = userService.get(userSeq);

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), response));
    }

    @DeleteMapping
    @Operation(summary = "회원 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_001", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<String>> delete(HttpServletRequest httpServletRequest) {
        Long userSeq = (Long) httpServletRequest.getAttribute("userSeq");
        userService.delete(userSeq);

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), "SUCCESS"));
    }

    @PostMapping("/reissue-token")
    @Operation(summary = "토큰 재발급 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "UNAUTHORIZED_EXCEPTION_005", description = "REFRESH 토큰만 사용할 수 있습니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "UNAUTHORIZED_EXCEPTION_007", description = "이미 사용된 Refresh Token입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "UNAUTHORIZED_EXCEPTION_008", description = "유효하지 않은 Refresh Token입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_001", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<ReIssueTokenResponse>> reissueToken(@RequestBody ReIssueTokenRequest request) {
        ReIssueTokenResponse response = userService.reissueToken(request);

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), response));
    }
}
