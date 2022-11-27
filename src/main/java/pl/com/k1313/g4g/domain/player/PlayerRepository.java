package pl.com.k1313.g4g.domain.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.k1313.g4g.domain.club.Club;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>{

    List<Player> findAllByPlayerClubAndFirstSquadPlayer(Club club);
    List<Player> findAllByPlayerClub(Club club);
    List<Player> findAllByFirstSquadPlayer(boolean first11Player);
    //przyklad ..jakich komand? nwm..sprawdz
//    Optional<Guest> findTop1ByCustomerIdAndFirstNameAndLastNameAndBirthDate(
//            String customerId,
//            String firstName,
//            String lastName,
//            LocalDate birthDate
//    );
}

