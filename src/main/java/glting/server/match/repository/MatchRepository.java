package glting.server.match.repository;

import glting.server.match.entity.MatchEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MatchRepository {

    private final MatchJpaRepository matchJpaRepository;

    public void saveMatch(MatchEntity matchEntity) {
        matchJpaRepository.save(matchEntity);
    }
}
