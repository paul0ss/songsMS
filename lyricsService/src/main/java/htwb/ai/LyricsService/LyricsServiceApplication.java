package htwb.ai.LyricsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import htwb.ai.LyricsService.repository.LyricsRepositoryImpl;


/**
 * Hello world!
 *
 */
@SpringBootApplication
public class LyricsServiceApplication 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(LyricsServiceApplication.class, args);
//    	LyricsRepositoryImpl repository = new LyricsRepositoryImpl();
//    	repository.lyricsByAuthor("Blabla");
    }
}
