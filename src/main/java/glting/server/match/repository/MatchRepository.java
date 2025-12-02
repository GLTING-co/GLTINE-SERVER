package glting.server.match.repository;

import glting.server.match.entity.MatchEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MatchRepository {

    private final MatchJpaRepository matchJpaRepository;

    /**
     * 매칭 엔티티를 저장합니다.
     *
     * @param matchEntity 저장할 매칭 엔티티
     */
    public void saveMatch(MatchEntity matchEntity) {
        matchJpaRepository.save(matchEntity);
    }
}
