package glting.server.match.entity;

import glting.server.users.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_match")
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
    @JoinColumn(name = "userA", nullable = false)
    private UserEntity userA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userB", nullable = false)
    private UserEntity userB;

    @Column(name = "matched_at", nullable = false)
    private LocalDateTime matchedAt;
}