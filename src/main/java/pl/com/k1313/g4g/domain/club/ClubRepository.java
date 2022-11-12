package pl.com.k1313.g4g.domain.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {


    Optional<Club>findById(long clubId);
    Optional<Club> findByClubName(String clubname);
}
