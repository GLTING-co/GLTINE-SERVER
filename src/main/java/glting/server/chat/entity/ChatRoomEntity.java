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
@Table(name = "CHAT_ROOM")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
@SQLDelete(sql = "UPDATE CHAT_ROOM SET deleted = true WHERE chat_room_seq = ?")
@Where(clause = "deleted = false")
public class ChatRoomEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "chat_room_seq", columnDefinition = "VARCHAR(36)")
    private String chatRoomSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_a_seq", nullable = false, unique = false)
    private UserEntity userA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_b_seq", nullable = false, unique = false)
    private UserEntity userB;

    @Builder.Default
    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = false;
}
