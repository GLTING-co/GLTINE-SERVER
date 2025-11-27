package glting.server.swipe.entity;

import glting.server.users.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "SWIPE",
        uniqueConstraints = @UniqueConstraint(columnNames = {"from_user_seq", "to_user_seq"})
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "swipe_seq")
    private Long swipeSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_seq", nullable = false)
    private UserEntity fromUserSeq; // 스와이프한 사람 (나)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_seq", nullable = false)
    private UserEntity toUserSeq;   // 스와이프 당한 사람

    @Column(name = "liked", nullable = false)
    private Boolean liked;    // true: 좋아요, false: 싫어요
}
