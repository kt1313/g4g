package pl.com.k1313.g4g.domain.league;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.k1313.g4g.domain.club.Club;
import pl.com.k1313.g4g.domain.match.Game;

import java.util.HashMap;
import java.util.List;

@Repository
public interface LeagueRepository extends JpaRepository<League,Long> {

    League findAllById(long id);
    List<League> findAnyByLeagueNumberNotNull();

}
