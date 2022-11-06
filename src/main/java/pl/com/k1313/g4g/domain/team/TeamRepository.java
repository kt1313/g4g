package pl.com.k1313.g4g.domain.team;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.com.k1313.g4g.domain.player.Player;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
