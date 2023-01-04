package pl.com.k1313.g4g.domain.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.k1313.g4g.domain.club.Club;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findFirstByPlayerClubAndFirstSquadPlayerTrueAndPlayerPosition(Club club, PlayerPosition playerPosition);

    List<Player> findAllByPlayerClubAndFirstSquadPlayer(Club club, boolean first11Player);

    List<Player> findAllByPlayerClub(Club club);
    Player findById(long id);

    List<Player> findAllByFirstSquadPlayer(boolean first11Player);
    //przyklad ..jakich komand? nwm..sprawdz
//    Optional<Guest> findTop1ByCustomerIdAndFirstNameAndLastNameAndBirthDate(
//            String customerId,
//            String firstName,
//            String lastName,
//            LocalDate birthDate
//    );
}

