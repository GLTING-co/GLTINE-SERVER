package glting.server.recommendation.controller;

import glting.server.exception.handler.GlobalExceptionHandler;
import glting.server.recommendation.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static glting.server.recommendation.controller.request.RecommendationRequest.*;
import static glting.server.recommendation.controller.response.RecommendationResponse.*;

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
    public List<UserProfileResponse> getRecommendations(@ParameterObject RecommendationFilterRequest filter) {

        return recommendationService.getRecommendations(filter);
    }
}
