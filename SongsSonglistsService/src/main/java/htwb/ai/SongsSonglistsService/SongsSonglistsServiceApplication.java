package htwb.ai.SongsSonglistsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;


/**
 * Hello world!
 *
 */
@SpringBootApplication
//@ImportResource("classpath:songsSongslistsService-servlet.xml")
public class SongsSonglistsServiceApplication 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(SongsSonglistsServiceApplication.class, args);
    }
}
