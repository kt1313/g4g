package pl.com.k1313.g4g.domain.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByClubname(String clubname);
    AppUser findByTimeStampAppUser(String timeStampAppUser);
    AppUser findByClubId(long clubId);
    AppUser findByAppUserId(long appUserId);
    Optional<AppUser> findByAppUserName(String appusername);


}
