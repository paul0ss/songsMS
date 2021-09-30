package htwb.ai.LyricsService.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import htwb.ai.LyricsService.exception.BadRequestException;
import htwb.ai.LyricsService.exception.NotFoundException;
import htwb.ai.LyricsService.exception.UnathorizedException;
import htwb.ai.LyricsService.model.Lyric;
import htwb.ai.LyricsService.repository.LyricsRepository;

@Service
public class LyricsService {
	
	/**
	 * Lyrics repository manages persistence related operations
	 */
	private final LyricsRepository repository;
	
	/**
	 * Synchronous Http-Client for making HTTP requests.
	 * Used to check User-Authentication, by sending a GET-Request to the
	 * Authentication Service(../auth/{token})
	 */
	private final RestTemplate restTemplate;
	
	private String serviceToken = "fuiwei72r723if";
	
	public LyricsService(LyricsRepository repository) {
		this.repository = repository;
		restTemplate = new RestTemplate();
	}
	
	public Lyric getLyric(String artist, String title) throws NotFoundException {
		
		//Retrieves lyric from the filesystem
		Lyric lyric = repository.getLyric(artist, title);
	
		if(lyric == null ) {
			throw new NotFoundException("Lyric was not found");
		}
		
		return lyric;
	}
	
	public List<Lyric> getLyrics(String artist, String acceptType) throws NotFoundException {
		
		//Get list of Lyrics
		List<Lyric> lyricList = repository.lyricsByAuthor(artist);
		if(lyricList.isEmpty()) {
			throw new NotFoundException("We cant find the lyrics of this artist...");
		}
		return lyricList;
	}
	
	public boolean uploadLyric(Lyric lyric, String token) throws UnathorizedException, BadRequestException{
		
		//Authorization check
    	boolean authorized = this.checkToken(token);
    	
    	//If not authorized
        if(authorized == false) {
      	  throw new UnathorizedException("Unauthorized");
      	}
		
        //If the body was parsed correctly
		if(lyric == null) {
			throw new BadRequestException("Lyric was not posted, RequestBody was not valid");
		}
		
		return repository.save(lyric);
	}
	
	public boolean deleteLyric(String artist, String title, String token) throws UnathorizedException, BadRequestException {
		
		//Authorization check
    	boolean authorized = this.checkToken(token);
    	
    	//If not authorized
        if(authorized == false) {
      	  throw new UnathorizedException("Unathorized");
      	}
        
        if(artist == null || title == null) {
        	throw new BadRequestException("Artist or Title is empty");
        }
        
        return repository.delete(artist, title);
	}
	
	/**
	 * Outputs the list of lyrics in XML format
	 * @param lyricList
	 * @return XML string
	 */
	public String lyricsListXML(List<Lyric> lyricList) {
		String outputXML = "<lyriclist>" + System.lineSeparator();
        for (Lyric l : lyricList) {
            outputXML += l.toXMLString();
        }
        outputXML += "</lyriclist>";
        return outputXML;
	}
	
	
	/**
	 * Outputs the list of lyrics ion JSON format
	 * @param lyricList
	 * @return JSON string
	 */
	public String lyricsListJSON(List<Lyric> lyricList) {
		String outputJSON = "{" + System.lineSeparator();
        for(int i = 0; i < lyricList.size(); i++) {
        	Lyric l = lyricList.get(i);
        	if(i != lyricList.size() - 1) {
        		outputJSON += l.toJSONString() + "," + System.lineSeparator();
        	}else {
        		outputJSON += l.toJSONString() + System.lineSeparator();
        	}
        }
        outputJSON += System.lineSeparator() + "}";
        return outputJSON;
	}
	
    private boolean checkToken(String token) {
    	String url = "http://localhost:8080/auth/" + token;
    	ResponseEntity<String> response = null;
    	try {
    		response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
    	}catch(Exception ex) {
    		System.out.println("Something happend"); //NEED HANDLING! EXCEPTION CUZ OF 401-Code!
    	}
    	boolean authorized = false;
    	if(response == null) { //null when 4XX or 5XX Status-Code
    		return false;
    	}
    	if(response.getStatusCode() == HttpStatus.OK) {
    		authorized = true;
    	}
    	System.out.println("---------------------------------------------------------------");
    	System.out.println(response.getStatusCode().toString());
    	return authorized;
    }
	

}
