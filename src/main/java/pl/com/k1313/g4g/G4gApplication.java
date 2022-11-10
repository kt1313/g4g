package pl.com.k1313.g4g;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class G4gApplication {

	public static void main(String[] args) {
		SpringApplication.run(G4gApplication.class, args);
	}

}
