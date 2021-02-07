package risk.game.grp.twenty;


import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot application starter and Configuration for Beans Management.
 *
 * @author Ali MOLLA MOHAMMADI
 */

@SpringBootApplication
@Configuration
public class GameRunner {

  private final static Logger LOGGER = LoggerFactory.getLogger(GameRunner.class);

  public static void main(String[] args) {
    LOGGER.info("Game Started: {} ", new Date());
    SpringApplication.run(GameRunner.class, args);
  }
}