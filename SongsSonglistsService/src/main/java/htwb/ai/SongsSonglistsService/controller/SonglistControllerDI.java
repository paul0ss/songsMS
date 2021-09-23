package htwb.ai.SongsSonglistsService.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import htwb.ai.SongsSonglistsService.model.*;
import htwb.ai.SongsSonglistsService.repository.SonglistRepository;

@RestController
@RequestMapping(value="/songs/playlist")
public class SonglistControllerDI {
		
		private SonglistRepository repository;
		
		private static HashMap<String, String> authTokens = new HashMap<String, String>();
	    
		private static final String SERVICETOKEN = "fuiwei72r723if";
		
		private final RestTemplate restTemplate;
		
		public SonglistControllerDI(SonglistRepository songlistRepository) {
			repository = songlistRepository;
			restTemplate = new RestTemplate();
		}
		
	    //GET http://localhost:8083/songlist
	    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	    public ResponseEntity<String> getSongs (@RequestHeader("Accept") String acceptType, 
	    		@RequestParam(value="userId", required=false) String userId,
	    		@RequestHeader(value="Authorization", required=true) String token) throws IOException {
	    	
	    	List<Songlist> lists = null;
	    	
	    	//Retrieves a user by token from Authentication-Service
	    	User user = getUserByToken(token);

	    	//If token doesnt match any user
	    	if(user == null) {
	    		
	    		return new ResponseEntity<String>("Wrong token", 
                        HttpStatus.UNAUTHORIZED);
	    	//User is authorized
	    	}else{
	    		
	    		//User requests its own playlist
	    		if(user.getUserId().equals(userId)) {
	    			
	    			//Get all songslists of this user
	    			lists = repository.findAllByOwnerId(userId);
	    			
	    		//User requests another users playlist
	    		}else {
	    			try {
	    				lists = repository.findAllPublicByOwnerId(userId);
	    			}catch(NullPointerException npe) {
	    				return new ResponseEntity<String>("Playlist doesnt exist", 
	                            HttpStatus.NOT_FOUND);
	    			}
	    		}
	    		
	    		//Generating XML or JSON Output
	    		if(acceptType.equals(MediaType.APPLICATION_XML_VALUE)) {
	            	MultiValueMap mvm = new HttpHeaders();
	                mvm.add("Content-Type", "application/xml");
	                String outputXML = "<songlists>" + System.lineSeparator();
	                for (Songlist l : lists) {
	                    outputXML += l.toStringXML();
	                }
	                outputXML += "</songlists>";
	                System.out.println(outputXML);
	                return new ResponseEntity<String>(outputXML, mvm, HttpStatus.OK);
	            }else{
	            	MultiValueMap mvm = new HttpHeaders();
	                mvm.add("Content-Type", "application/json");
	                String outputJSON = "{" + System.lineSeparator();
	                List<Songlist> listOfSonglists = new ArrayList<Songlist>();
	                listOfSonglists.addAll(lists);
	                for(int i = 0; i < listOfSonglists.size(); i++) {
	                	Songlist s = listOfSonglists.get(i);
	                	if(i != listOfSonglists.size() - 1) {
	                		outputJSON += s.toString() + "," + System.lineSeparator();
	                	}else {
	                		outputJSON += s.toString() + System.lineSeparator();
	                	}
	                }
	                outputJSON += System.lineSeparator() + "}";
	                return new ResponseEntity<String>(outputJSON.toString(), mvm, HttpStatus.OK);
	            }
	    	}
	    }
	    
	    //GET http://localhost:8083/songlist/{id}
	    @GetMapping(value="/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
		public ResponseEntity<String> getSonglistByID(
		          @PathVariable (value="id") Integer id, 
		          @RequestHeader("Accept") String acceptType, 
		          @RequestHeader(value="Authorization", required=true) String token) throws IOException {
	    	
	    	//Recieves the user from Authentication-Service according to the token from request
	    	User user = getUserByToken(token);
	    	
	    	//If the user is not null
	    	if(user != null) {
	    		
	    		String userIdFromRequest = user.getUserId();
	    		
	    		//Get the matching songlist by ID
	    		Songlist list = null;
    			list = repository.findById(id).get();
    			if(list == null) { // DOESNT WORK!!!
    				return new ResponseEntity<String>("Songlist doesnt exist", 
                            HttpStatus.NOT_FOUND);
    			}
	    		
	    		//If the list doesnt exist
//	    		if(list == null) {
//	    			return new ResponseEntity<String>("Songlist doesnt exist!", 
//	                        HttpStatus.NOT_FOUND);
//	    		}
//	    		
	    		//Get the listowner
	    		String listOwner = list.getOwnerId();
	    		if(listOwner == null) {
    				return new ResponseEntity<String>("Playlist doesnt exist", 
                            HttpStatus.NOT_FOUND);
    			}
	    		
	    		//Die Liste gehört dem RequestCreator
	    		if(listOwner.equals(userIdFromRequest)) {
	    			if(acceptType.equals(MediaType.APPLICATION_XML_VALUE)) {
	                	MultiValueMap mvm = new HttpHeaders();
	                    mvm.add("Content-Type", "application/xml");
	                    String outputXML = list.toStringXML();
	                    return new ResponseEntity<String>(outputXML, mvm, HttpStatus.OK);
	    			}else {
	    				MultiValueMap mvm = new HttpHeaders();
	                    mvm.add("Content-Type", "application/json");
	                    String outputJSON = list.toString();
	                    return new ResponseEntity<String>(outputJSON, mvm, HttpStatus.OK);
	    			}
	    			//Die Liste gehört nicht dem RequestCreator
	    		}else {
	    			//die liste ist public
	    			if(!list.isPrivate()) {
	    				if(acceptType.equals(MediaType.APPLICATION_XML_VALUE)) {
		                	MultiValueMap mvm = new HttpHeaders();
		                    mvm.add("Content-Type", "application/xml");
		                    String outputXML = list.toStringXML();
		                    return new ResponseEntity<String>(outputXML, mvm, HttpStatus.OK);
		    			}else {
		    				MultiValueMap mvm = new HttpHeaders();
		                    mvm.add("Content-Type", "application/json");
		                    String outputJSON = list.toString();
		                    return new ResponseEntity<String>(outputJSON, mvm, HttpStatus.OK);
		    			}
	    			}else {
	    				return new ResponseEntity<String>("Forbidden", HttpStatus.FORBIDDEN);
	    			}
	    		}
	    	}else {
	    		return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
	    	}
	    }
	    
	    /**
	     * Creates a Songlist for the user and persists it in the DB.
	     * 
	     * Requirements:
	     * - Authorization-Header contains a valid token
	     * - Received Data must be in JSON-Format
	     * - All songs contained in the songlist have to be present in the DB
	     * 
	     * URL: http://localhost:8080/songlist
	     * @param songlist
	     * @param token
	     * @return:
	     * - 401(UNATHORIZED) when the user token is not valid
	     * - 201(CREATED) when the songlist was created succsessfuly
	     * - 400(BAD_REQUEST) when the songs in the request are not contained in the DB, or if the request body is empty
	     * - The Location-Header must contain the url to the songlist
	     */
	    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, headers = "content-type=application/json")
	    public ResponseEntity<String> addSonglist (@RequestBody Songlist songlist,
	    		@RequestHeader(value="Authorization", required=true) String token,
	    		@RequestHeader(value="Content-Type", required=true) String contentType){
	    	
	    	//If the request-body is not empty
	        if(songlist != null){
	        	
	        	//Retrieve user from Authentication-Service
	        	User user = getUserByToken(token);
	        	
	        	//If user is not authorized
		        if(user == null) {
		        	return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
		        }
		        
	        try {
	        	
	        	//Retrieve the userID from the user
	        	String userId = user.getUserId();
	        	
	        	//Set the owner of the songlist
	        	songlist.setOwnerId(userId);
	        	
	        	//Persist the songlist in the DB
	        	Songlist savedList = repository.save(songlist);
	        	
	        	//ID of the Songlist
	            Integer id = savedList.getId();
	            
	            //Setting the Location-Header
	            HttpHeaders header = new HttpHeaders();
	            header.set("Location", "/songlist/" + id);
	            return new ResponseEntity<String>("Id of the added songlist is: " + id, header, HttpStatus.CREATED);
	        }catch(Exception ex){
	            return new ResponseEntity<String>("Songlist was not created! All songs have to exist in the DB!", HttpStatus.BAD_REQUEST);
	        }
	        //If the request-body is empty
	        }else{
	            return new ResponseEntity<String>("Songlist was not created, RequestBody was null", HttpStatus.BAD_REQUEST);
	            }
	    }
	    
	    /**
	     * Updates the existing songlist
	     * 
	     * Requirements:
	     * - Authorization-Header contains a valid token
	     * - The songlist belongs to the user (the Auth-Token belongs to the ownerID of the songlist)
	     * - All songs from the new songlist have to be present in the DB
	     * @param songlist
	     * @param token
	     * @param contentType
	     * @return
	     */
	    @PutMapping(value="/{id}",consumes = MediaType.APPLICATION_JSON_VALUE, headers = "content-type=application/json")
	    public ResponseEntity<String> updateSonglist (
	    		@RequestBody Songlist songlist,
	    		@PathVariable (value="id") Integer id,
	    		@RequestHeader(value="Authorization", required=true) String token,
	    		@RequestHeader(value="Content-Type") String contentType){
	    	
	    	//Retrieve user from Authentication-Service
        	User user = getUserByToken(token);
        	
        	//If user is not authorized
	        if(user == null) {
	        	return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
	        }
	        
	        //If the request body is valid
	        if(songlist != null) {
	        	
	        	Set<Song> songs = songlist.getSongList();
	        	
	        	for(Song s : songs) {
	        		if(songExists(s, token) == false) {
	        			return new ResponseEntity<String>("Songlist was not updated! All songs have to exist in the DB!", HttpStatus.BAD_REQUEST);
	        		}
	        	}
	        	
        		//The songlist with the requested ID, from DB
        		Songlist songlistDB = repository.findById(id).get();
        		
        		//Owner of the songlist in the DB
        		String ownerId = songlistDB.getOwnerId();
        		
        		//If the user is the owner of the songlist
        		if(user.getUserId().equals(ownerId)) {
        			
        			//Updating the existing songlist
        			songlistDB.setName(songlist.getName());
        			songlistDB.setPublic(songlist.isPrivate());;
        			songlistDB.setSongList(songlist.getSongList());
        			repository.save(songlistDB);
        			
        			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
        		}else {
        			return new ResponseEntity<String>("Forbidden", HttpStatus.FORBIDDEN);
        		}
	        		
	        //If the request body is not valid
	        }else {
	        	return new ResponseEntity<String>("Songlist was not updated, RequestBody was null", HttpStatus.BAD_REQUEST);
	        }
	    }
	    
	    /**
	     * Deletes the songlist from the DB.
	     * 
	     * Requirements:
	     * - Authorization-Header contains a valid token
	     * - The songlist belongs to the user
	     * 
	     * URL: http://localhost:8080/songlist/{id}
	     * @param id of the playlist to delete
	     * @param token to validate the user
	     * @return
	     */
	    @DeleteMapping(value="/{id}")
		public ResponseEntity<String> deleteSonglistByID(
		          @PathVariable (value="id") Integer id,
		          @RequestHeader(value="Authorization", required=true) String token){
	    	
	    	Songlist list = null;
	    	
	    	//Retrieve user from Authentication-Service by token
	    	User user = getUserByToken(token);
	    	
	    	//If the token is wrong
	    	if(user == null) {
	    		return new ResponseEntity<String>("Unathorized! Bad token!", HttpStatus.UNAUTHORIZED);
	    	}
	    	
	    	//get userID from User
    		String userId = user.getUserId();
    		
    		//Retrieve the list from the DB
    		try {    			
    			list = repository.findById(id).get();
    		}catch(NoSuchElementException noElement) {
    			return new ResponseEntity<String>("List doesnt exist!", HttpStatus.NOT_FOUND);
    		}
    		
    		//Check if the operation is permitted
    		if(list.getOwnerId().equals(userId)) {
    			
    			//Delete the songlist from DB
    			repository.deleteById(id);
    			
    			return new ResponseEntity<String>("Songlist with id " + id + " was successfully deleted!", HttpStatus.OK);
    			
    		}else {
    			
    			return new ResponseEntity<String>("Forbidden", HttpStatus.FORBIDDEN);
    			
    		}
	    	}
//	    private boolean checkToken(String token) {
//	    	String url = "http://localhost:8082/auth/" + token;
//	    	ResponseEntity<String> response = null;
//	    	try {
//	    		response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
//	    	}catch(Exception ex) {
//	    		System.out.println("Something happend"); //NEED HANDLING! EXCEPTION CUZ OF 401-Code!
//	    	}
//	    	boolean authorized = false;
//	    	if(response == null) { //null when 4XX or 5XX Status-Code
//	    		return false;
//	    	}
//	    	if(response.getStatusCode() == HttpStatus.OK) {
//	    		authorized = true;
//	    	}
//	    	System.out.println("---------------------------------------------------------------");
//	    	System.out.println(response.getStatusCode().toString());
//	    	return authorized;
//	    	return true;
//	    }
	    /**
	     * Retrieves a user information from Authentication Service(/auth) according to the token.
	     * In order to retrieve a data from Auth-Service, each requesting service needs to authenticate
	     * with its own Token (ServiceToken).
	     * If the user doesnt exist in the DB, returns null.
	     * @param token
	     * @return User if exists, null if user doesnt exist
	     */
	    private User getUserByToken(String token) {
	    	//URL for the Request on Auth-Service
	    	String url = "http://localhost:8082/auth/getUser/" + token;
	    	
	    	//Sets the Header for Authentification of the Service
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.set("ServiceToken", SERVICETOKEN);
	    	
	    	//Adding Service-Token to the header
	    	HttpEntity<String> entity = new HttpEntity<>(headers);
	    	ResponseEntity<User> response = null;
	    	
	    	//Requesting the user information
	    	try {
	    		response = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);		
	    	}catch(HttpClientErrorException e) {
	    		return null;
	    	}
	    	
	    	return response.getBody();
	    }
	    
	    private boolean songExists(Song s, String token) {
	    	String url = "http://localhost:8080/songs/" + s.getId();
	    	
	    	//Set the token of the user
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.set("Authorization", token);
	    	
	    	HttpEntity<String> entity = new HttpEntity<>(headers);
	    	ResponseEntity<String> response = null;
	    	
	    	//Requesting the song existence information
	    	try {
	    		response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);		
	    	}catch(HttpClientErrorException e) {
	    		return false;
	    	}
	    	if(response.getStatusCode() == HttpStatus.OK) {
	    		return true;
	    	}else {
	    		return false;
	    	}
	    }
	    
}

