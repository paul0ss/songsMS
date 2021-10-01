package htwb.ai.SongsSonglistsService.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import htwb.ai.SongsSonglistsService.exception.BadRequestException;
import htwb.ai.SongsSonglistsService.exception.NotFoundException;
import htwb.ai.SongsSonglistsService.exception.UnathorizedException;
import htwb.ai.SongsSonglistsService.model.Song;
import htwb.ai.SongsSonglistsService.model.Songlist;
import htwb.ai.SongsSonglistsService.repository.SongRepository;

@Service
public class SongsService {
	
	/**
	 * Song repository manages persistence related operation.s 
	 */
	private final SongRepository repository;
	
	/**
	 * Synchronous Http-Client for making HTTP requests.
	 * Used to check User-Authentication, by sending a GET-Request to the
	 * Authentication Service(../auth/{token})
	 */
	private final RestTemplate restTemplate;
	
	List<Song> songlist;
	
	public SongsService(SongRepository repository) {
		this.repository = repository;
		restTemplate = new RestTemplate();
	}
	
	/**
	 * Get all songs from the DB
	 * @return the songlist
	 * @throws UnathorizedException 
	 * @throws NotFoundException
	 */
	public List<Song> getSongs(String token) throws UnathorizedException, NotFoundException {
		
		//make request to /auth/{token} to check for authorization
		boolean authorized = this.checkToken(token);
		
		//If user not authorized
		if(authorized == false) {
			throw new UnathorizedException("Unauthorized");
		}

    	//Gets alls songs from the DB
    	try {
    		Iterable<Song> iterable = repository.findAll();
    		songlist = new ArrayList();
    		iterable.forEach(songlist::add);
    	}catch(NullPointerException npe) {
    		throw new NotFoundException("Declined: No songs yet!");
    	}
    	
    	return songlist;
        }
	
	public Song getSong(String token, Integer id) throws UnathorizedException, NotFoundException {
  	  
	  	//If the User is authorized
	    if(!this.checkToken(token)) {
	  	  throw new UnathorizedException("Unathorized");
	  	}
		
		Song song = null;
		
	    //Getting requested song from the DB
  	  try {
  		  Optional<Song> result = repository.findById(id); //Exception handling needed!!!	  
  		  song = result.get();
  	  }catch(NoSuchElementException nsee) {
  		  throw new NotFoundException("Song with id " + Integer.toString(id) + " does not exist");
  	  }
  	  
  	  return song;
	}
	
	public Integer saveSong(String token, Song song) throws UnathorizedException, BadRequestException {
		
		//If not authorized
		if(!this.checkToken(token)) {
			throw new UnathorizedException("Unauthorized");
		}
		//If the request-body is not a valid data
		if(song == null) {
			throw new BadRequestException("Song was not created, RequestBody was not valid");
		}
	
    	//Save song to the DB
    	Song savedSong = repository.save(song);
    	
    	//if the song was successfully created
    	Integer id = savedSong.getId();
    	return id;

	}
	
	public boolean updateSong(String token, Song song, Integer id) throws UnathorizedException, BadRequestException, NotFoundException {
		
    	//If not authorized
        if(!this.checkToken(token)) {
      	  throw new UnathorizedException("Unauthorized");
      	}
        
        //If song exists and IDs match
        if(song == null || id != song.getId()) {
        	throw new BadRequestException("Song was not updated, RequestBody was null or ID did not match");
        }
        	
    	//Check if the requested song exists in DB
    	if(repository.findById(id).isEmpty()) {
    		throw new NotFoundException("404 Error. Song does not exist!");
    	}
    	
    	//the number of rows that were updated in the DB
        int rows = repository.updateSong(id, song.getTitle(), song.getArtist(), song.getLabel(), song.getReleased()); //ERROR HERE!!!
        System.out.println("----------------------------------------------------------");
        System.out.println("Number of rows has to be 1:" + rows);
        System.out.println("----------------------------------------------------------");
        
        //If exectly one row was updated than the operation was successful
        if(rows != 1) {
        	return false;
        }
        
        return true;
	}
	
	public void deleteSong(String token, Integer id) throws UnathorizedException, NotFoundException {
		
    	//If not authorized
        if(!this.checkToken(token)) {
      	  throw new UnathorizedException("Unauthorized");
      	}
        //Get the song from the DB
        Optional<Song> op = repository.findById(id);
        
        //If the song doesnt exist
        if(op.isEmpty()) {
        	throw new NotFoundException("Song doesnt exist");
        }
        
        Song song = op.get();
        
        //Delete the song
        repository.delete(song);
	}
	
	public boolean songExists(Song s) {
		return repository.existsById(s.getId());
	}
	
	
	public String songlistToXMLString(List<Song> songlist) {
		String outputXML = "<songs>" + System.lineSeparator();
        for (Song song : songlist) {
            outputXML += song.toStringXML();
        }
        outputXML += "</songs>";
        return outputXML;
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
