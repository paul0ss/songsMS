package htwb.ai.LyricsService.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import htwb.ai.LyricsService.exception.BadRequestException;
import htwb.ai.LyricsService.exception.NotFoundException;
import htwb.ai.LyricsService.exception.UnathorizedException;
import htwb.ai.LyricsService.model.Lyric;
import htwb.ai.LyricsService.repository.LyricsRepository;
import htwb.ai.LyricsService.service.LyricsService;

@RestController
@RequestMapping(value="/lyrics")
public class LyricsController {
	
	private final LyricsService service;
	
    public LyricsController (LyricsRepository repository, LyricsService service) {
        this.service = service;
    }
	
    /**
     * Retrieves the lyric of the song in XML or JSON format
     * Requirements:
     * - url should contain the artist 
     * - url should contain the title
     * 
     * URL: http://localhost:8080/lyrics/{artist}/{title}
     * @param title of the song
     * @param artist of the song
     * @param acceptType
     * @return
     */
	@GetMapping(value="/{artist}/{title}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getLyric(
    		@PathVariable (value="title") String title,
    		@PathVariable (value="artist") String artist,
    		@RequestHeader("Accept") String acceptType){
		
		Lyric lyric;
		
		try {
			lyric = service.getLyric(artist, title);
		}catch(NotFoundException nfe) {
			return new ResponseEntity<String>(nfe.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		MultiValueMap mvm = new HttpHeaders();
			
		//If Accept-Header: application:xml, than return xml data
        if(acceptType.equals(MediaType.APPLICATION_XML_VALUE)) {
            mvm.add("Content-Type", "application/xml");
            return new ResponseEntity<String>(lyric.toXMLString(), mvm, HttpStatus.OK);
            
        //If Accept-Header: application:json or empty, than return json data
        }else{
            mvm.add("Content-Type", "application/json");
        	return new ResponseEntity<String>(lyric.toJSONString(), mvm, HttpStatus.OK);
        	
        }
	}
	
	/**
	 * Retrieves all lyrics of the specified artist in XML or JSON format
	 * Requirements:
	 * - artist has to be specified in the url
	 * 
	 * URL: http://localhost:8080/lyrics/{artist}
	 * @param artist
	 * @param acceptType
	 * @return
	 */
	@GetMapping(value="/{artist}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getLyrics(
    		@PathVariable (value="artist") String artist,
    		@RequestHeader("Accept") String acceptType){
		
		System.out.println("accept type: " + acceptType);
		
		List<Lyric> lyricList;
		
		try {
			lyricList = service.getLyrics(artist, acceptType);
		} catch (NotFoundException nfe) {
			return new ResponseEntity<String>(nfe.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		MultiValueMap mvm = new HttpHeaders();
		String response;
		
		if(acceptType.equals(MediaType.APPLICATION_XML_VALUE)) {
			mvm.add("Content-Type", "application/xml");
			response = service.lyricsListJSON(lyricList);
            return new ResponseEntity<String>(response, mvm, HttpStatus.OK);
			
		}else{
			mvm.add("Content-Type", "application/json");
			response = service.lyricsListJSON(lyricList);
			return new ResponseEntity<String>(response, mvm, HttpStatus.OK);
		}
	}
	
	
	/**
	 * Uploads the lyric recieved in JSON format from an authorized user to the filesystem.
	 * Requirements:
	 * - Lyric has to be in JSON format
	 * - Authoriaztion-Header with valid token
	 * 
	 * URL: http://localhost:8080/lyrics
	 * @param lyric
	 * @param token
	 * @return
	 */
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> postLyric(@RequestBody Lyric lyric,
			@RequestHeader(value="Authorization", required=true) String token){
		
		try {
			boolean result = service.uploadLyric(lyric, token);
			
			if(result == false) {
				return new ResponseEntity<String>("Server side error occured!", HttpStatus.INTERNAL_SERVER_ERROR);
			}else {
				return new ResponseEntity<String>("Lyric was posted successfully", HttpStatus.CREATED);
			}
			
		} catch (UnathorizedException ue) {
			return new ResponseEntity<String>(ue.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (BadRequestException bre) {
			return new ResponseEntity<String>(bre.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	
	/**
	 * Deletes the lyric if the user is authorized. Every user that is authorized can delete every lyric.
	 * Requirements:
	 * - artist and title have to be contained in the url to identify the lyric
	 * - Authorization-Header has to contain a valid token
	 * 
	 * URL: http://localhost:8080/lyrics/{artist}/{title}
	 * @param title
	 * @param artist
	 * @param token
	 * @return
	 */
	@DeleteMapping(value="/{artist}/{title}")
	public ResponseEntity<String> deleteLyric(@PathVariable (value="title") String title,
			@PathVariable (value="artist") String artist,
			@RequestHeader(value="Authorization") String token){
		
        try {
        	
			if(service.deleteLyric(artist, title, token)) {
				return new ResponseEntity<String>("Song was deleted", HttpStatus.NO_CONTENT);
			}else {
				return new ResponseEntity<String>("Song was not found", HttpStatus.NOT_FOUND);
			}
			
		} catch (UnathorizedException ue) {
			return new ResponseEntity<String>(ue.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (BadRequestException bre) {
			return new ResponseEntity<String>(bre.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
