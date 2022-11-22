package pl.com.k1313.g4g.domain.league;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueRepository extends JpaRepository<League,Long> {

    League findById(long id);
    List<League> findAnyByLeagueNumberNotNull();
}
