package pl.com.k1313.g4g.domain.league;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeagueRepository extends JpaRepository<League,Long> {

    League findById(long id);
}
