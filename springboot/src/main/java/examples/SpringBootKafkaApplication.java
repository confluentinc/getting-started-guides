package examples.springboot.kafka;
  
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootKafkaApplication {

  public static void main(final String[] args) {
    SpringApplication.run(SpringBootKafkaApplication.class, args);
  }

}
