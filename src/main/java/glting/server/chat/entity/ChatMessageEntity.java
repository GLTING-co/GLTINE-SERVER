package glting.server.chat.entity;

import glting.server.base.BaseTimeEntity;
import glting.server.users.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "CHAT_MESSAGE")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
@SQLDelete(sql = "UPDATE CHAT_MESSAGE SET deleted = true WHERE chat_message_seq = ?")
@Where(clause = "deleted = false")
public class ChatMessageEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "chat_message_seq", columnDefinition = "VARCHAR(36)")
    private String chatMessageSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_seq", nullable = false, unique = false)
    private ChatRoomEntity chatRoomEntity;

    @Column(name = "message", nullable = false, unique = false, columnDefinition = "LONGTEXT")
    private String message;

    @Column(name = "is_read", nullable = false, unique = false)
    private Boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_seq", nullable = false, unique = false)
    private UserEntity senderEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reveiver_seq", nullable = false, unique = false)
    private UserEntity receiverEntity;

    @Builder.Default
    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = false;
}
