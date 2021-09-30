package htwb.ai.AuthenticationService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@ComponentScan
@EnableJpaRepositories
public class AuthenticationServiceApplication 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(AuthenticationServiceApplication.class, args);
    }
    
    
}
