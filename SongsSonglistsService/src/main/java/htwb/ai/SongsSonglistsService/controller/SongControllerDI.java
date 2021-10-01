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

import htwb.ai.SongsSonglistsService.exception.BadRequestException;
import htwb.ai.SongsSonglistsService.exception.NotFoundException;
import htwb.ai.SongsSonglistsService.exception.UnathorizedException;
import htwb.ai.SongsSonglistsService.model.*;
import htwb.ai.SongsSonglistsService.repository.SongRepository;
import htwb.ai.SongsSonglistsService.service.SongsService;

/**
 * Controller of the Song service
 * 
 * @author lyubar
 *
 */
@RestController
@RequestMapping("/songs")
public class SongControllerDI {

	private final SongsService songsService;

	public SongControllerDI(SongsService songsService) {
		this.songsService = songsService;
	}

	/**
	 * Handling GET-Request Requirements: - Accept-Header: application/xml or
	 * application/json - Authorization-Header with a valid token URL:
	 * http://localhost:8080/songs
	 * 
	 * @param acceptType
	 * @param token
	 * @return
	 * @throws IOException
	 */
	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> getSongs(@RequestHeader("Accept") String acceptType,
			@RequestHeader(value = "Authorization") String token) throws IOException {

		List<Song> songlist;

		try {
			songlist = songsService.getSongs(token);
		} catch (UnathorizedException ue) {
			return new ResponseEntity<String>(ue.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (NotFoundException nfe) {
			return new ResponseEntity<String>(nfe.getMessage(), HttpStatus.NOT_FOUND);
		}

		MultiValueMap mvm = new HttpHeaders();

		// If Accept-Header: application:xml, than return xml data
		if (acceptType.equals(MediaType.APPLICATION_XML_VALUE)) {

			mvm.add("Content-Type", "application/xml");

			return new ResponseEntity<String>(songsService.songlistToXMLString(songlist), mvm, HttpStatus.OK);

			// If Accept-Header: application:json, than return json data
		} else {

			mvm.add("Content-Type", "application/json");

			return new ResponseEntity<String>(songlist.toString(), mvm, HttpStatus.OK);
		}
	}

	// GET http://localhost:8083/songs/1
	@GetMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> getSong(@PathVariable(value = "id") Integer id,
			@RequestHeader("Accept") String acceptType, @RequestHeader("Authorization") String token)
			throws IOException {

		Song song;

		try {
			song = songsService.getSong(token, id);
		} catch (UnathorizedException ue) {
			return new ResponseEntity<String>(ue.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (NotFoundException nfe) {
			return new ResponseEntity<String>(nfe.getMessage(), HttpStatus.NOT_FOUND);
		}

		MultiValueMap mvm = new HttpHeaders();

		// Response in XML
		if (acceptType.equals(MediaType.APPLICATION_XML_VALUE)) {
			mvm.add("Content-Type", "application/xml");
			return new ResponseEntity<String>(song.toStringXML(), mvm, HttpStatus.OK);
			// Response in JSON
		} else {
			mvm.add("Content-Type", "application/json");
			return new ResponseEntity<String>(song.toString(), mvm, HttpStatus.OK);
		}
	}

	// POST http://localhost:8083/songs
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addSong(@RequestBody Song song,
			@RequestHeader(value = "Authorization", required = true) String token) {

		Integer id;
		try {
			id = songsService.saveSong(token, song);
		} catch (UnathorizedException ue) {
			return new ResponseEntity<String>(ue.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (BadRequestException bre) {
			return new ResponseEntity<String>(bre.getMessage(), HttpStatus.BAD_REQUEST);
		}

		HttpHeaders header = new HttpHeaders();
		header.add("id", id.toString());

		return new ResponseEntity<String>(song.toString(), header, HttpStatus.CREATED);
	}

	/**
	 * Handling PUT-Request Changes the songinformation of the song, that already
	 * exists in the Database 
	 * Requirements: 
	 * - Content-Type Header: application/json
	 * - Authorization Header with a valid token 
	 * - the SongID in the URL Matches the SongID in the JSON-Data
	 * URL: http://localhost:8083/songs/1
	 * 
	 * @param id
	 * @param song
	 * @param token
	 * @return
	 */
	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> putSong(@PathVariable(value = "id") Integer id, @RequestBody Song song,
			@RequestHeader(value = "Authorization", required = true) String token) {

		try {
			if (songsService.updateSong(token, song, id)) {
				return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<String>("Persistance failure occured", HttpStatus.BAD_REQUEST);
			}
		} catch (UnathorizedException ue) {
			return new ResponseEntity<String>(ue.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (BadRequestException bre) {
			return new ResponseEntity<String>(bre.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotFoundException nfe) {
			return new ResponseEntity<String>(nfe.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	// DELETE http://localhost:8083/songs/1
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> deleteSong(@PathVariable(value = "id") Integer id,
			@RequestHeader("Authorization") String token) {

		try {
			songsService.deleteSong(token, id);
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		} catch (UnathorizedException ue) {
			return new ResponseEntity<String>(ue.getMessage(), HttpStatus.UNAUTHORIZED);
		} catch (NotFoundException nfe) {
			return new ResponseEntity<String>(nfe.getMessage(), HttpStatus.NOT_FOUND);
		}

	}
}
