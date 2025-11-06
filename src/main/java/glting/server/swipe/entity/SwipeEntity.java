package glting.server.swipe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SWIPE")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "swipe_seq")
    private Long swipeSeq;

    @Column(name = "from_user_seq", nullable = false)
    private Long fromUserSeq; // 스와이프한 사람 (나)

    @Column(name = "to_user_seq", nullable = false)
    private Long toUserSeq;   // 스와이프 당한 사람

    @Column(name = "liked", nullable = false)
    private Boolean liked;    // true: 좋아요, false: 싫어요
}
