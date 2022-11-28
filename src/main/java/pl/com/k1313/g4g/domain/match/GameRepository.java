package pl.com.k1313.g4g.domain.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.k1313.g4g.domain.club.Club;

@Repository

public interface GameRepository extends JpaRepository< Game, Long>{

Game findFirstlByInProgress();

}
