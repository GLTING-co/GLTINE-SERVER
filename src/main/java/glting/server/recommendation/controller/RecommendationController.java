package glting.server.recommendation.controller;

import glting.server.base.BaseResponse;
import glting.server.exception.handler.GlobalExceptionHandler;
import glting.server.recommendation.service.RecommendationService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static glting.server.recommendation.controller.request.RecommendationRequest.*;
import static glting.server.users.controller.response.UserResponse.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Tag(name = "추천 목록", description = "담당자(송인준)")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping
    @Operation(summary = "추천 목록 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_002", description = "존재하지 않은 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public ResponseEntity<BaseResponse<List<UserProfileResponse>>> getRecommendations(
                                                        @RequestParam(required = false) @Schema(description = "최소 나이", example = "20") Integer minAge,
                                                        @RequestParam(required = false) @Schema(description = "최대 나이", example = "30") Integer maxAge,
                                                        @RequestParam(required = false) @Schema(description = "성적 타입", example = "1") String sexualType,
                                                        @RequestParam(required = false) @Schema(description = "관계", example = "SINGLE") String relationship,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size,
                                                        HttpServletRequest httpServletRequest) {

        RecommendationFilterRequest filter  = new RecommendationFilterRequest(minAge,maxAge,sexualType,relationship,(Long) httpServletRequest.getAttribute("userSeq"),page, size);

        List<UserProfileResponse> response = recommendationService.getRecommendations(filter);

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), response));

    }
}
