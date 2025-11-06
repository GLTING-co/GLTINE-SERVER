package glting.server.recommendation.controller;

import glting.server.exception.handler.GlobalExceptionHandler;
import glting.server.recommendation.controller.vo.request.RecommendationFilterRequest;
import glting.server.recommendation.service.RecommendationService;
import glting.server.users.controller.vo.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Tag(name = "추천 목록", description = "담당자(송인준)")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    @Parameters({
            @Parameter(
                    name = "minAge",
                    description = "최소 나이 조건",
                    example = "20"),
            @Parameter(
                    name = "maxAge",
                    description = "최대 나이 조건",
                    example = "30"),
            @Parameter(
                    name = "sexualType",
                    description = "성적 타입 조건",
                    example = "1"),
            @Parameter(
                    name = "relationship",
                    description = "관계 조건",
                    example = "SINGLE"),
            @Parameter(
                    name = "user",
                    description = "Member Sequence",
                    example = "1"),
    })
    @Operation(summary = "추천 목록 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "NOT_FOUND_EXCEPTION_002", description = "존재하지 않은 회원입니다.", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
    })
    public List<UserProfileResponse> getRecommendations(RecommendationFilterRequest filter) {

        return recommendationService.getRecommendations(filter);
    }
}
