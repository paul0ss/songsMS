package htwb.ai.AuthenticationService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@ImportResource("classpath:authentication-servlet.xml")
public class AuthenticationServiceApplication 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(AuthenticationServiceApplication.class, args);
    }
    
    
}
