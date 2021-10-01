package htwb.ai.SongsSonglistsService.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import htwb.ai.SongsSonglistsService.exception.BadRequestException;
import htwb.ai.SongsSonglistsService.exception.ForbiddenException;
import htwb.ai.SongsSonglistsService.exception.NotFoundException;
import htwb.ai.SongsSonglistsService.exception.UnathorizedException;
import htwb.ai.SongsSonglistsService.model.Song;
import htwb.ai.SongsSonglistsService.model.Songlist;
import htwb.ai.SongsSonglistsService.model.User;
import htwb.ai.SongsSonglistsService.repository.SonglistRepository;

@Service
public class SonglistsService {
	
	private SonglistRepository repository;
	
	private final SongsService songsService;
	
	private static HashMap<String, String> authTokens = new HashMap<String, String>();
    
	private static final String SERVICETOKEN = "fuiwei72r723if";
	
	private final RestTemplate restTemplate;
	
	public SonglistsService(SonglistRepository repository, SongsService songsService) {
		this.repository = repository;
		this.songsService = songsService;
		restTemplate = new RestTemplate();
	}
	
	public List<Songlist> getSonglist(String token, String userId) throws NotFoundException, UnathorizedException {
		List<Songlist> lists = null;
    	
    	//Retrieves a user by token from Authentication-Service
    	User user = getUserByToken(token);

    	//If token doesnt match any user
    	if(user == null) {
    		throw new UnathorizedException("Wrong token");
    	//User is authorized
    	}
    	
		System.out.println("User: " +user.toJSONString());
		
		//User requests its own playlist
		if(user.getUserId().equals(userId)) {
			
			lists = repository.findAllByOwnerId(userId);
			
		//User requests another users public playlist
		}else {
			try {
				lists = repository.findAllPublicByOwnerId(userId);
			}catch(NullPointerException npe) {
				throw new NotFoundException("Playlist doesnt exist");
			}
		}
		
		return lists;
	}
	
	public Songlist getSonlistByID(String token, Integer id) throws ForbiddenException, UnathorizedException, NotFoundException {
		
		//Recieves the user from Authentication-Service according to the token from request
    	User user = getUserByToken(token);
    	
    	if(user == null) {
    		throw new UnathorizedException("Unathorized");
    	}
    		
		String userIdFromRequest = user.getUserId();
		
		//Get the matching songlist by ID
		Optional<Songlist> opt = repository.findById(id);
		if(opt.isEmpty()) {
			throw new NotFoundException("Songlist doesnt exist");
		}
		
		Songlist list = opt.get();
		   		
		//Get the listowner
		String listOwner = list.getOwnerId();
		if(listOwner == null) {
			throw new NotFoundException("Songlist doesnt exist");
		}
		
		//Die Liste gehört dem user
		if(listOwner.equals(userIdFromRequest)) {
			return list;
		}
		
		//Die Liste gehört nicht dem RequestCreator
		
		//die liste ist private
		if(list.isPrivate()) {
			throw new ForbiddenException("Forbidden");
		}
		return list;
	}
	
	public Integer addSonglist(Songlist songlist, String token) throws UnathorizedException, BadRequestException {
		
		if(songlist == null) {
			throw new BadRequestException("Songlist was not created, RequestBody was null");
		}
        	
    	//Retrieve user from Authentication-Service
    	User user = getUserByToken(token);
    	
    	//If user is not authorized
        if(user == null) {
        	throw new UnathorizedException("Unauthorized");
        }
	        
        try {
        	
        	//Set the owner of the songlist
        	songlist.setOwnerId(user.getUserId());
        	
        	//Persist the songlist in the DB
        	Songlist savedList = repository.save(songlist);
        	
        	//ID of the Songlist
            Integer id = savedList.getId();
            
            return id;
            
        }catch(Exception ex){
        	throw new BadRequestException("Songlist was not created! All songs have to exist in the DB!");
        }
	}
	
	public void updateSonglist(String token, Songlist songlist, Integer id) throws UnathorizedException, BadRequestException, ForbiddenException {
    	//Retrieve user from Authentication-Service
    	User user = getUserByToken(token);
    	
    	//If user is not authorized
        if(user == null) {
        	throw new UnathorizedException("Unauthorized");
        }
        
        //If the request body is not valid
        if(songlist == null) {
        	throw new BadRequestException("Songlist was not updated, RequestBody was null");
        }
        
        	
    	Set<Song> songs = songlist.getSongList();
    	
    	for(Song s : songs) {
    		if(!songsService.songExists(s)) {
    			throw new BadRequestException("Songlist was not updated! All songs have to exist in the DB!");
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
			
		}else {
			throw new ForbiddenException("Forbidden");
		}
		
	}
	
	public void deleteSong(String token, Integer id) throws UnathorizedException, ForbiddenException, NotFoundException {
		
    	//Retrieve user from Authentication-Service by token
    	User user = getUserByToken(token);
    	
    	//If the token is wrong
    	if(user == null) {
    		throw new UnathorizedException("Unathorized");
    	}
    	
    	Songlist list = null;
    	
    	//get userID from User
		String userId = user.getUserId();
		
		//Retrieve the list from the DB
		try {    			
			list = repository.findById(id).get();
			
			//Check if the operation is permitted
			if(!list.getOwnerId().equals(userId)) {
				throw new ForbiddenException("forbidden");
			}
			
			//Delete the songlist from DB
			repository.deleteById(id);
			
		}catch(NoSuchElementException noElement) {
			throw new NotFoundException("List doesnt exist");
		}
	}
	
	public String songlistsToXML(List<Songlist> lists) {
		String outputXML = "<songlists>" + System.lineSeparator();
        for (Songlist l : lists) {
            outputXML += l.toStringXML();
        }
        outputXML += "</songlists>";
        return outputXML;
	}
	
	public String songlistsToJSON(List<Songlist> lists) {
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
        return outputJSON;
	}
	
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
    	String url = "http://localhost:8080/auth/getUser/" + token;
    	
    	//Sets the Header for Authentication of the Service
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
	
}
