package pl.com.k1313.g4g.domain.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>{

    //przyklad ..jakich komand? nwm..sprawdz
//    Optional<Guest> findTop1ByCustomerIdAndFirstNameAndLastNameAndBirthDate(
//            String customerId,
//            String firstName,
//            String lastName,
//            LocalDate birthDate
//    );
}

