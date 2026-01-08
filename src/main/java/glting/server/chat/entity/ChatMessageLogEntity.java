package glting.server.chat.entity;

import glting.server.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "CHAT_MESSAGE_LOG")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageLogEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "chat_message_log_seq", columnDefinition = "VARCHAR(100)")
    private String chatMessageLogSeq;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_seq", nullable = false)
    private ChatRoomEntity chatRoomEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_a_last_read_message_seq", nullable = true)
    private ChatMessageEntity userALastReadMessage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_b_last_read_message_seq", nullable = true)
    private ChatMessageEntity userBLastReadMessage;

    @Version
    @Builder.Default
    @Column(name = "version", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer version = 0;

    /**
     * 사용자의 마지막 읽은 메시지를 업데이트합니다.
     *
     * @param isUserA     사용자가 userA인지 여부
     * @param readMessage 읽은 메시지 엔티티
     */
    public void updateLastReadMessage(boolean isUserA, ChatMessageEntity readMessage) {
        if (isUserA) this.userALastReadMessage = readMessage;
        else this.userBLastReadMessage = readMessage;
    }
}