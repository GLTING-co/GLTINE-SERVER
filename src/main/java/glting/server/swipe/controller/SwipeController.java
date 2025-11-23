package glting.server.swipe.controller;

import glting.server.base.BaseResponse;
import glting.server.exception.handler.GlobalExceptionHandler;
import glting.server.swipe.service.SwipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static glting.server.swipe.controller.request.SwipeRequest.*;
import static glting.server.swipe.controller.response.SwipeResponse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/swipe")
@Tag(name = "스와이프", description = "담당자(송인준)")
public class SwipeController {

    private final SwipeService swipeService;


    @PostMapping("/dislike")
    @Operation(summary = "싫어요 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_002", description = "존재하지 않은 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<String>> dislikeUser(HttpServletRequest httpServletRequest,
                                                            @RequestBody MatchRequest matchRequest) {

        Long fromUserSeq = (Long) httpServletRequest.getAttribute("userSeq");

        swipeService.dislike(fromUserSeq, matchRequest.toUserSeq());

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), "SUCCESS"));
    }


    @PostMapping("/like")
    @Operation(summary = "좋아요 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_002", description = "존재하지 않은 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<MatchedResponse>> likeUser(HttpServletRequest httpServletRequest,
                                                                  @RequestBody MatchRequest matchRequest) {

        Long fromUserSeq = (Long) httpServletRequest.getAttribute("userSeq");

        MatchedResponse response = swipeService.like(fromUserSeq, matchRequest.toUserSeq());

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), response));
    }
}
