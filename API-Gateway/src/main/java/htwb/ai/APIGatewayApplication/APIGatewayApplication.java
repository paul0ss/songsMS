package htwb.ai.APIGatewayApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableEurekaClient
public class APIGatewayApplication 
{
    public static void main( String[] args )
    {
        SpringApplication.run(APIGatewayApplication.class, args);
    }
}
