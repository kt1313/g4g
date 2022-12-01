package pl.com.k1313.g4g.domain.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.k1313.g4g.domain.club.Club;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findFirstByInProgress(boolean inProgress);

    Optional<Game> findFirstByGameClubsInAndInProgress(List<Club> clubs, boolean inProgress);

}
