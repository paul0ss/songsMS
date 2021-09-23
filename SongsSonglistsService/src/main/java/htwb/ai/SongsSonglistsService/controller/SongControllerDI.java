package htwb.ai.SongsSonglistsService.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import htwb.ai.SongsSonglistsService.model.*;
import htwb.ai.SongsSonglistsService.repository.SongRepository;


/**
 * Controller of the Song service
 * @author lyubar
 *
 */
@RestController
@RequestMapping("/songs")
public class SongControllerDI {
	
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
    
    public SongControllerDI (SongRepository repository) {
        this.repository = repository;
        restTemplate = new RestTemplate();
    }
    
    /**
     * Handling GET-Request
     * Requirements:
     * - Accept-Header: application/xml or application/json
     * - Authorization-Header with a valid token
     * URL: http://localhost:8080/songs
     * @param acceptType
     * @param token
     * @return
     * @throws IOException
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getSongs (@RequestHeader("Accept") String acceptType,
    		@RequestHeader(value="Authorization") String token) throws IOException {
        //songlist = songDAO.findAllSongs(); //throws NullPOinter
    	
    	//Gets alls songs from the DB
    	try {
    		Iterable<Song> iterable = repository.findAll();
    		songlist = new ArrayList();
    		iterable.forEach(songlist::add);
    	}catch(NullPointerException npe) {
    		return new ResponseEntity<String>("Declined: No songs yet!", 
    				HttpStatus.OK);
    	}
    	
    	
    	//make request to /auth/{token} to check for authorization
    	boolean authorized = this.checkToken(token);
    	
    	//If user not authorized
    	if(authorized == false) {
    		return new ResponseEntity<String>("Unauthorized", 
                    HttpStatus.UNAUTHORIZED);
    	}
    	
    	//If Accept-Header: application:xml, than return xml data
        if(acceptType.equals(MediaType.APPLICATION_XML_VALUE)) {
        	MultiValueMap mvm = new HttpHeaders();
            mvm.add("Content-Type", "application/xml");
            String outputXML = "<songs>" + System.lineSeparator();
            for (Song song : songlist) {
                outputXML += song.toStringXML();
            }
            outputXML += "</songs>";
            System.out.println(outputXML);
            return new ResponseEntity<String>(outputXML, mvm, HttpStatus.OK);
            
        //If Accept-Header: application:json, than return json data
        }else{
        	MultiValueMap mvm = new HttpHeaders();
            mvm.add("Content-Type", "application/json");
            return new ResponseEntity<String>(songlist.toString(), mvm, HttpStatus.OK);
        }
        }
    
    
    //GET http://localhost:8083/songs/1
    @GetMapping(value="/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<String> getSong(
	          @PathVariable (value="id") Integer id, @RequestHeader("Accept") String acceptType,
	          @RequestHeader("Authorization") String token) throws IOException {
    	  Song song = null;
	      //Getting requested song from the DB
    	  try {
    		  Optional<Song> result = repository.findById(id); //Exception handling needed!!!	  
    		  song = result.get();
    	  }catch(NoSuchElementException nsee) {
    		  //If the song doesnt exist
    		  return new ResponseEntity<String>("Song with id " + Integer.toString(id) + " does not exist", HttpStatus.NOT_FOUND);  		  
    	  }
    	  
    	  //If the song exists
	      if (song != null) {
	    	  
	    	  //Check Authorization
	    	  boolean authorized = this.checkToken(token);
	    	  
	    	  //If the User is authorized
	          if(authorized == false) {
	        	  return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
	        	}
	          //Response in XML
              if(acceptType.equals(MediaType.APPLICATION_XML_VALUE)) {
            	  MultiValueMap mvm = new HttpHeaders();
                  mvm.add("Content-Type", "application/xml");
                  return new ResponseEntity<String>(song.toStringXML(), mvm, HttpStatus.OK);
              //Response in JSON
              }else{
            	  MultiValueMap mvm = new HttpHeaders();
                  mvm.add("Content-Type", "application/json");
                  return new ResponseEntity<String>(song.toString(), mvm, HttpStatus.OK);
              }
	      }
	    //If the song doesnt exist
	    //Boiletplate code !!!
		  return new ResponseEntity<String>("Song with id " + Integer.toString(id) + " does not exist", HttpStatus.NOT_FOUND);
	  }

    //POST http://localhost:8083/songs
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addSong (@RequestBody Song song,
    		@RequestHeader(value="Authorization") String token){
    	
	        
    	//If the request-body is a valid data
    	//What is if the body is not a valid DATA????
    	if(song != null){
    		
    		//Check authorization
    		boolean authorized = this.checkToken(token);
    		
    		//if not authorized
    		if(authorized == false) {
    			return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
    		}
        try {
        	//Save song to the DB
        	Song savedSong = repository.save(song);
        	
        	//if the song was successfully created
        	Integer id = savedSong.getId();
            System.out.println("SongID: "+id);
            HttpHeaders header = new HttpHeaders();
            header.add("id", id.toString());
            return new ResponseEntity<String>(song.toString(), header, HttpStatus.CREATED);
            
        //if the error occured
        }catch(Exception ex){
            return new ResponseEntity<String>("Song was not created", HttpStatus.BAD_REQUEST);
        }
        
        //if the request is not valid
        }else{
            return new ResponseEntity<String>("Song was not created, RequestBody was null", HttpStatus.BAD_REQUEST);
            }
        
    }
    
    /**
     * Handling PUT-Request
     * Changes the songinformation of the song, that already exists in the Database
     * Requirements:
     * - Content-Type Header: application/json
     * - Authorization Header with a valid token
     * - the SongID in the URL Matches the SongID in the JSON-Data
     * URL: http://localhost:8083/songs/1
     * @param id
     * @param song
     * @param token
     * @return
     */
    @PutMapping (value= "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> putSong (@PathVariable (value="id") Integer id,
    		@RequestBody Song song, @RequestHeader("Authorization") String token){
    	
    	//Check the Authorization
    	boolean authorized = this.checkToken(token);
    	
    	//If not authorized
        if(authorized == false) {
      	  return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
      	}
        
        //If song exists and IDs match
        if(song != null && id == song.getId()){
        try {
        	
        	//Check if the requested song exists in DB
        	if(repository.findById(id).get() == null) {
        		return new ResponseEntity<String>("404 Error. Song does not exist!",HttpStatus.NOT_FOUND);
        	}
        	//the number of rows that were updated in the DB
            int rows = repository.updateSong(id, song.getTitle(), song.getArtist(), song.getLabel(), song.getReleased()); //ERROR HERE!!!
            System.out.println("----------------------------------------------------------");
            System.out.println("Number of rows has to be 1:" + rows);
            System.out.println("----------------------------------------------------------");
            
            boolean success = false;
            //If exectly one row was updated than the operation was successful
            if(rows == 1) {
            	success = true;
            }
            //Why no Content?
            if(success) {
                return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
            }else{
                return new ResponseEntity<String>("Song was not updated", HttpStatus.BAD_REQUEST);
            }
            
         //Error occured, the song was not updated
        }catch(Exception ex){
            return new ResponseEntity<String>("Song was not updated", HttpStatus.BAD_REQUEST);
        }
    }else{
        return new ResponseEntity<String>("Song was not updated, RequestBody was null or ID did not match", HttpStatus.BAD_REQUEST);
    }}

    //DELETE http://localhost:8083/songs/1
    @DeleteMapping(value= "/{id}")
    public ResponseEntity<String> deleteSong (@PathVariable (value="id") Integer id,
    		@RequestHeader("Authorization") String token){
    	//Authorization check
    	boolean authorized = this.checkToken(token);
    	
    	//If not authorized
        if(authorized == false) {
      	  return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
      	}
            try {
            	
            	//If the songs exists in the DB
            	Song reqSong = repository.findById(id).get();
                if(reqSong != null) {
                	
                	//Delete the song
                	repository.delete(reqSong);
                	return new ResponseEntity<String>("Song was deleted", HttpStatus.NO_CONTENT);
                }else{
                    return new ResponseEntity<String>("Song doesnt exist", HttpStatus.NOT_FOUND);
                }
            }catch(Exception ex){
                return new ResponseEntity<String>("Song was not deleted", HttpStatus.BAD_REQUEST);
            }
        }
    
    public boolean checkSongExistence(Song s) {
    	return repository.existsById(s.getId());
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
//    	return true;
    }
}
