package htwb.ai.SongsSonglistsService.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import htwb.ai.SongsSonglistsService.dao.*;
import htwb.ai.SongsSonglistsService.model.*;

@RestController
@RequestMapping("/songs")
public class SongControllerDI {
	
	@Autowired
    private ISongDAO songDAO;
	
	List<Song> songlist;
    
    public SongControllerDI (ISongDAO uDAO) {
        this.songDAO = uDAO;
    }
    
    // GET http://localhost:8080/songsWS-4Gewinnt/rest/songs
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getSongs (@RequestHeader("Accept") String acceptType, @RequestHeader("Authorization") String token) throws IOException {
        songlist = songDAO.findAllSongs();
        if (songlist == null) {
            return new ResponseEntity<String>("Declined: No songs yet!", 
                    HttpStatus.OK);
        }else {
        	boolean authorized = UserControllerDI.checkToken(token);
        	if(authorized == false) {
        		return new ResponseEntity<String>("Unauthorized", 
                        HttpStatus.UNAUTHORIZED);
        	}
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
            }else{
            	MultiValueMap mvm = new HttpHeaders();
                mvm.add("Content-Type", "application/json");
                return new ResponseEntity<String>(songlist.toString(), mvm, HttpStatus.OK);
            }
        }
    }
    
    //GET http://localhost:8080/songsWS-4Gewinnt/rest/songs/1
    @GetMapping(value="/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<String> getSong(
	          @PathVariable (value="id") Integer id, @RequestHeader("Accept") String acceptType, @RequestHeader("Authorization") String token) throws IOException {
	      Song song = songDAO.getSongById(Integer.toString(id));
	      if (song != null) {
	    	  boolean authorized = UserControllerDI.checkToken(token);
	          if(authorized == false) {
	        	  return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
	        	}
              if(acceptType.equals(MediaType.APPLICATION_XML_VALUE)) {
            	  MultiValueMap mvm = new HttpHeaders();
                  mvm.add("Content-Type", "application/xml");
                  return new ResponseEntity<String>(song.toStringXML(), mvm, HttpStatus.OK);
              }else{
            	  MultiValueMap mvm = new HttpHeaders();
                  mvm.add("Content-Type", "application/json");
                  return new ResponseEntity<String>(song.toString(), mvm, HttpStatus.OK);
              }
	      }
	      return new ResponseEntity<String>("Song with id " + Integer.toString(id) + " does not exist", HttpStatus.NOT_FOUND);
	  }

    //POST http://localhost:8080/songsWS-4Gewinnt/rest/songs
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addSong (@RequestBody Song song, @RequestHeader("Authorization") String token){
    	System.out.println("------------------------------------------");
    	System.out.println("inside");
    	System.out.println("Song value" + song.toString());
        if(song != null){
        	boolean authorized = UserControllerDI.checkToken(token);
	        if(authorized == false) {
	        	return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
	        }
        try {
            Integer id = songDAO.saveSong(song);
            System.out.println("SongID: "+id);
            HttpHeaders header = new HttpHeaders();
            header.add("id", id.toString());
            return new ResponseEntity<String>(song.toString(), header, HttpStatus.CREATED);
        }catch(Exception ex){
            return new ResponseEntity<String>("Song was not created", HttpStatus.BAD_REQUEST);
        }
        }else{
            return new ResponseEntity<String>("Song was not created, RequestBody was null", HttpStatus.BAD_REQUEST);
            }
    }
    //PUT http://localhost:8080/songsWS-4Gewinnt/rest/songs/1
    @PutMapping (value= "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> putSong (@PathVariable (value="id") Integer id, @RequestBody Song song, @RequestHeader("Authorization") String token){
    	boolean authorized = UserControllerDI.checkToken(token);
        if(authorized == false) {
      	  return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
      	}
        if(song != null && id == song.getId()){
        try {
        	if(songDAO.getSongById(id.toString()) == null) {
        		return new ResponseEntity<String>("404 Error. Song does not exist!",HttpStatus.NOT_FOUND);
        	}
            boolean success = songDAO.updateSong(id, song);
            if(success) {
                return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
            }else{
                return new ResponseEntity<String>("Song was not updated", HttpStatus.BAD_REQUEST);
            }
        }catch(Exception ex){
            return new ResponseEntity<String>("Song was not updated", HttpStatus.BAD_REQUEST);
        }
    }else{
        return new ResponseEntity<String>("Song was not updated, RequestBody was null or ID did not match", HttpStatus.BAD_REQUEST);
    }}

    //DELETE http://localhost:8080/songsWS-TEAMNAME/rest/songs/1
    @DeleteMapping(value= "/{id}")
    public ResponseEntity<String> deleteSong (@PathVariable (value="id") Integer id, @RequestHeader("Authorization") String token){
    	boolean authorized = UserControllerDI.checkToken(token);
        if(authorized == false) {
      	  return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
      	}
            try {
                if(songDAO.getSongById(id.toString()) != null) {
                    songDAO.deleteSong(id);
                    return new ResponseEntity<String>("Song was deleted", HttpStatus.NO_CONTENT);
                }else{
                    return new ResponseEntity<String>("Song doesnt exist", HttpStatus.NOT_FOUND);
                }
            }catch(Exception ex){
                return new ResponseEntity<String>("Song was not deleted", HttpStatus.BAD_REQUEST);
            }
        }
}
