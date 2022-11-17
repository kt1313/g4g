package pl.com.k1313.g4g.domain.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.k1313.g4g.domain.league.League;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    Club findByClubId(long clubId);
    List<Club> findByClubLeague(League league);

    Optional<Club> findById(long clubId);

    Optional<Club> findByClubName(String clubname);

}
