package glting.server.match.entity;

import glting.server.users.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "MATCH_",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_a_seq", "user_b_seq"})
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_seq")
    private Long matchSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_a_seq", nullable = false)
    private UserEntity userA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_b_seq", nullable = false)
    private UserEntity userB;

    @Column(name = "matched_at", nullable = false)
    private LocalDateTime matchedAt;
}