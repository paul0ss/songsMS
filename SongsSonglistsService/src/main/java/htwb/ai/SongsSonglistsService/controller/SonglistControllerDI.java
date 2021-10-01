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

import htwb.ai.SongsSonglistsService.exception.BadRequestException;
import htwb.ai.SongsSonglistsService.exception.ForbiddenException;
import htwb.ai.SongsSonglistsService.exception.NotFoundException;
import htwb.ai.SongsSonglistsService.exception.UnathorizedException;
import htwb.ai.SongsSonglistsService.model.*;
import htwb.ai.SongsSonglistsService.repository.SonglistRepository;
import htwb.ai.SongsSonglistsService.service.SonglistsService;

@RestController
@RequestMapping(value="/songs/playlists")
public class SonglistControllerDI {
		
		private final SonglistsService songlistsService;
		
		
		public SonglistControllerDI(SonglistsService songlistsService) {
			this.songlistsService = songlistsService;
		}
		
	    //GET http://localhost:8083/songlist
	    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	    public ResponseEntity<String> getSongs (@RequestHeader("Accept") String acceptType, 
	    		@RequestParam(value="userId", required=true) String userId,
	    		@RequestHeader(value="Authorization", required=true) String token) throws IOException {
	    	
	    	List<Songlist> lists;
	    	
			try {
				lists = songlistsService.getSonglist(token, userId);
			} catch (NotFoundException nfe) {
				return new ResponseEntity<String>(nfe.getMessage(), HttpStatus.NOT_FOUND);
			} catch (UnathorizedException ue) {
				return new ResponseEntity<String>(ue.getMessage(), HttpStatus.UNAUTHORIZED);
			}
	    	
	    	MultiValueMap mvm = new HttpHeaders();
	    	
    		//Generating XML or JSON Output
    		if(acceptType.equals(MediaType.APPLICATION_XML_VALUE)) {
                mvm.add("Content-Type", "application/xml");
                return new ResponseEntity<String>(songlistsService.songlistsToXML(lists), mvm, HttpStatus.OK);
            }else{
                mvm.add("Content-Type", "application/json");
                return new ResponseEntity<String>(songlistsService.songlistsToJSON(lists), mvm, HttpStatus.OK);
            }
	    	
	    }
	    
	    //GET http://localhost:8083/songlist/{id}
	    @GetMapping(value="/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
		public ResponseEntity<String> getSonglistByID(
		          @PathVariable (value="id") Integer id, 
		          @RequestHeader("Accept") String acceptType, 
		          @RequestHeader(value="Authorization", required=true) String token) throws IOException {
	    	
	    	Songlist list;
	    	
			try {
				list = songlistsService.getSonlistByID(token, id);
			} catch (ForbiddenException fe) {
				return new ResponseEntity<String>(fe.getMessage(), HttpStatus.FORBIDDEN);
			} catch (UnathorizedException ue) {
				return new ResponseEntity<String>(ue.getMessage(), HttpStatus.UNAUTHORIZED);
			} catch (NotFoundException nfe) {
				return new ResponseEntity<String>(nfe.getMessage(), HttpStatus.NOT_FOUND);
			}
			
			MultiValueMap mvm = new HttpHeaders();
	    	
			//Generate response
			if(acceptType.equals(MediaType.APPLICATION_XML_VALUE)) {
                mvm.add("Content-Type", "application/xml");
                return new ResponseEntity<String>(list.toStringXML(), mvm, HttpStatus.OK);
			}else {
                mvm.add("Content-Type", "application/json");
                return new ResponseEntity<String>(list.toString(), mvm, HttpStatus.OK);
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
	     * URL: http://localhost:8080/songs/playlists
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

	    	Integer id;
			try {
				id = songlistsService.addSonglist(songlist, token);
				
				//Setting the Location-Header
				HttpHeaders header = new HttpHeaders();
				header.set("Location", "http://localhost:8080/songs/playlists/" + id);
				return new ResponseEntity<String>("Id of the added songlist is: " + id, header, HttpStatus.CREATED);
				
			} catch (UnathorizedException ue) {
				return new ResponseEntity<String>(ue.getMessage(), HttpStatus.UNAUTHORIZED);
			} catch (BadRequestException bre) {
				return new ResponseEntity<String>(bre.getMessage(), HttpStatus.BAD_REQUEST);
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
	    	
	    	try {
				songlistsService.updateSonglist(token, songlist, id);
				return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
			} catch (UnathorizedException ue) {
				return new ResponseEntity<String>(ue.getMessage(), HttpStatus.UNAUTHORIZED);
			} catch (BadRequestException bre) {
				return new ResponseEntity<String>(bre.getMessage(), HttpStatus.BAD_REQUEST);
			} catch (ForbiddenException fe) {
				return new ResponseEntity<String>(fe.getMessage(), HttpStatus.UNAUTHORIZED);
			}
	    }
	    
	    /**
	     * Deletes the songlist from the DB.
	     * 
	     * Requirements:
	     * - Authorization-Header contains a valid token
	     * - The songlist belongs to the user
	     * 
	     * URL: http://localhost:8080/songs/playlists{id}
	     * @param id of the playlist to delete
	     * @param token to validate the user
	     * @return
	     */
	    @DeleteMapping(value="/{id}")
		public ResponseEntity<String> deleteSonglistByID(
		          @PathVariable (value="id") Integer id,
		          @RequestHeader(value="Authorization", required=true) String token){
	    	
	    	try {
				songlistsService.deleteSong(token, id);
				return new ResponseEntity<String>("Songlist with id " + id + " was successfully deleted!", HttpStatus.OK);
			} catch (UnathorizedException ue) {
				return new ResponseEntity<String>(ue.getMessage(), HttpStatus.UNAUTHORIZED);
			} catch (ForbiddenException fe) {
				return new ResponseEntity<String>(fe.getMessage(), HttpStatus.FORBIDDEN);
			} catch (NotFoundException nfe) {
				return new ResponseEntity<String>(nfe.getMessage(), HttpStatus.NOT_FOUND);
			}
	    	
	    	
	    }
    
}

