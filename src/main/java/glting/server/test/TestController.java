package glting.server.test;

import glting.server.base.BaseResponse;
import glting.server.chat.entity.ChatRoomEntity;
import glting.server.chat.repository.ChatRoomJpaRepository;
import glting.server.common.service.CommonService;
import glting.server.users.entity.UserEntity;
import glting.server.users.repository.UserJpaRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Tag(name = "테스트", description = "담당자(박종훈)")
public class TestController {
    private final CommonService commonService;
    private final UserJpaRepository userJpaRepository;
    private final ChatRoomJpaRepository chatRoomJpaRepository;

    @GetMapping("/login")
    public ResponseEntity<BaseResponse<String>> login(@RequestParam("userSeq") Long userSeq) {
        String accessToken = commonService.issueToken(userSeq, "ACCESS", "TEST");
        commonService.saveToken(userSeq, "WHITE", accessToken);

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), accessToken));
    }

    @GetMapping("/chat-room")
    public ResponseEntity<BaseResponse<String>> chat(@RequestParam("userSeq1") Long userSeq1, @RequestParam("userSeq2") Long userSeq2) {
        UserEntity userA = userJpaRepository.findByUserSeq(userSeq1);
        UserEntity userB = userJpaRepository.findByUserSeq(userSeq2);
        ChatRoomEntity save = chatRoomJpaRepository.save(ChatRoomEntity.builder()
                .userA(userA)
                .userB(userB)
                .deleted(false)
                .build());

        return ResponseEntity.ok().body(BaseResponse.ofSuccess(HttpStatus.OK.value(), save.getChatRoomSeq()));
    }

}
