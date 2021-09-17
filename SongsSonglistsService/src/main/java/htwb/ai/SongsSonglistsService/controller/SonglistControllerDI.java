package htwb.ai.SongsSonglistsService.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import htwb.ai.SongsSonglistsService.dao.*;
import htwb.ai.SongsSonglistsService.model.*;

@RestController
@RequestMapping(value="/songlist")
public class SonglistControllerDI {
	
		Set<Songlist> lists;
	    
		@Autowired
	    private ISonglistDAO songlistDAO;
		
		private static HashMap<String, String> authTokens = new HashMap<String, String>();
	    
	    public SonglistControllerDI (ISonglistDAO songlistDAO) {
	        this.songlistDAO = songlistDAO;
	    }
	    
	    //GET http://localhost:8080/songsWS-4Gewinnt/rest/songlist
	    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	    public ResponseEntity<String> getSongs (@RequestHeader("Accept") String acceptType, 
	    		@RequestParam(required=false) String userId,
	    		@RequestHeader("Authorization") String token) throws IOException {
	    	String response = "Bad Request";
	    	if(songlistDAO.checkUserExistence(userId) == false) {
	    		response = "User doesnt exist";
	    		return new ResponseEntity<String>(response, 
                        HttpStatus.NOT_FOUND);
	    	}
	    	// Wenn Token existiert
	    	if(UserControllerDI.checkToken(token)) {
	    		//User der den Request schickt
	    		String user = UserControllerDI.getUserIdByToken(token);
	    		//Wenn der Benutzer seine eigene Liste abfragt
	    		if(user.equals(userId)) {
	    			lists = songlistDAO.getSonglistsById(userId);
	    			// Wenn der Benutzer eine fremde Liste abfragt
	    		}else{
	    			lists = songlistDAO.getPublicSonglistsById(userId);
	    		}
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
	    		// Wenn der Token nicht existiert
    		}else {
    			response = "You are not authorized!";
    			return new ResponseEntity<String>(response, 
    					HttpStatus.UNAUTHORIZED);    			
    		}
	    }
	    
	    //GET http://localhost:8080/songsWS-4Gewinnt/rest/songlist/{id}
	    @GetMapping(value="/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
		public ResponseEntity<String> getSonglistByID(
		          @PathVariable (value="id") Integer id, 
		          @RequestHeader("Accept") String acceptType, 
		          @RequestHeader("Authorization") String token) throws IOException {
	    	if(UserControllerDI.checkToken(token)) {
	    		String userIdFromRequest = UserControllerDI.getUserIdByToken(token);
	    		Songlist list = songlistDAO.getSonglistById(String.valueOf(id));
	    		if(list == null) {
	    			return new ResponseEntity<String>("Songlist doesnt exist!", 
	                        HttpStatus.NOT_FOUND);
	    		}
	    		String listOwner = list.getOwnerId().getUserId();
	    		String response = songlistDAO.getSonglistById(String.valueOf(id)).getSongList().toString();
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
	    
	  //POST http://localhost:8080/songsWS-4Gewinnt/rest/songlist
	    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	    public ResponseEntity<String> addSonglist (@RequestBody Songlist songlist,
	    		@RequestHeader("Authorization") String token){
	        if(songlist != null){
	        	boolean authorized = UserControllerDI.checkToken(token);
		        if(authorized == false) {
		        	return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
		        }
	        try {
	        	String userId = UserControllerDI.getUserIdByToken(token);
	            Integer id = songlistDAO.saveSonglist(songlist, userId);
	            if(id < 0) {
	            	return new ResponseEntity<String>("Song that you want to add doesnt exists in the Database!", HttpStatus.BAD_REQUEST);
	            }
	            HttpHeaders header = new HttpHeaders();
	            header.set("Location", "/songsWS-4Gewinnt/rest/songlist/" + id);
	            return new ResponseEntity<String>("Id of the added songlist is: " + id, header, HttpStatus.CREATED);
	        }catch(Exception ex){
	            return new ResponseEntity<String>("Songlist was not created", HttpStatus.BAD_REQUEST);
	        }
	        }else{
	            return new ResponseEntity<String>("Song was not created, RequestBody was null", HttpStatus.BAD_REQUEST);
	            }
	    }
	    
	  //DELETE http://localhost:8080/songsWS-4Gewinnt/rest/songlist/{id}
	    @DeleteMapping(value="/{id}")
		public ResponseEntity<String> deleteSonglistByID(
		          @PathVariable (value="id") Integer id,
		          @RequestHeader("Authorization") String token) throws IOException {
	    	Songlist list = null;
	    	if(UserControllerDI.checkToken(token)) {
	    		String userId = UserControllerDI.getUserIdByToken(token);
	    		list = songlistDAO.getSonglistById(String.valueOf(id));
	    		if(list == null) {	    			
	    			return new ResponseEntity<String>("Songlist doesnt exist!", HttpStatus.NOT_FOUND);
	    		}
	    		if(list.getOwnerId().getUserId().equals(userId)) {
	    			if(songlistDAO.deleteSonglist(list.getId())) {
	    				return new ResponseEntity<String>("Songlist with id " + id + " was successfully deleted!", HttpStatus.OK);
	    			}else {
	    				return new ResponseEntity<String>("Something went wrong. Server error!", HttpStatus.INTERNAL_SERVER_ERROR);
	    			}
	    		}else {
	    			return new ResponseEntity<String>("Forbidden", HttpStatus.FORBIDDEN);
	    		}
	    	}else {
	    		return new ResponseEntity<String>("Forbidden", HttpStatus.FORBIDDEN);
	    	}
	    }
}

