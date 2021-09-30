package htwb.ai.AuthenticationService.controller;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import htwb.ai.AuthenticationService.Exception.BadRequestException;
import htwb.ai.AuthenticationService.Exception.ForbiddenException;
import htwb.ai.AuthenticationService.Exception.NotFoundException;
import htwb.ai.AuthenticationService.Exception.UnathorizedException;
import htwb.ai.AuthenticationService.dao.DBUserDAO;
import htwb.ai.AuthenticationService.dao.IUserDAO;
import htwb.ai.AuthenticationService.dto.LoginRequest;
import htwb.ai.AuthenticationService.model.User;
import htwb.ai.AuthenticationService.repository.UserRepository;
import htwb.ai.AuthenticationService.service.AuthenticationService;

@RestController
@RequestMapping(value="/auth")
public class AuthController {
	
	private final UserRepository repository;
	
	private final AuthenticationService authService;
	
	private static HashMap<String, String> authTokens = new HashMap<String, String>();
    
    public AuthController (UserRepository repository, AuthenticationService authService) {
        this.repository = repository;
        this.authService = authService;
    }
    
    
    /**
     * Logs user in according to userdata recieved in JSON format.
     * @param loginRequest contains userdate
     * @param headers
     * @return
     */
    @PostMapping(produces = "text/plain", consumes = "application/json")
    public ResponseEntity<String> login(
    		@Valid @RequestBody LoginRequest loginRequest){
    	try {
    		String token = authService.login(loginRequest);
    		return new ResponseEntity<String> (token, HttpStatus.OK);
    	}catch(UnathorizedException ue) {
    		return new ResponseEntity<String> (ue.getMessage(), HttpStatus.UNAUTHORIZED);
    	}
    }

    
    /**
     * Checks if a received token belongs to any user in the system.
     * @param token for logging in the User
     * @return
     */
    @GetMapping(value="/{token}")
    public ResponseEntity<String> checkAuth(@PathVariable (value="token") String token){
    	if(authService.checkToken(token)) {
    		return new ResponseEntity<String> ("Authorized user", HttpStatus.OK);
    	}else {
    		return new ResponseEntity<String> ("Wrong token", HttpStatus.UNAUTHORIZED);
    	}
    
    }
    
    /**
     * This GET-Requests are supposed to come only from intern Services.
     * They retrieve the user sensitive information based on the recieved token.
     * Such information will be used in other Services.
     * Here we defined a Service-specific token, to protect the sensitive data from
     * unpermitted access.
     * @param token for logging in the User
     * @param headers contains a specific key-value pair to authenticate the intern Service
     * @return the userspecific information
     */
    @GetMapping(value="/getUser/{token}")
    public ResponseEntity<String> getUserByToken(
    		@PathVariable (value="token") String token,
    		@RequestHeader HttpHeaders headers){
    	String responseString;
    	try {
    		responseString = authService.tokenToUserJSON(token, headers.getFirst("ServiceToken"));
    	}catch(BadRequestException bae) {
    		return new ResponseEntity<String> (bae.getMessage(), HttpStatus.BAD_REQUEST);
    	}catch(ForbiddenException fe) {
    		return new ResponseEntity<String> (fe.getMessage(), HttpStatus.FORBIDDEN);
    	}catch(NotFoundException nfe) {
    		return new ResponseEntity<String> (nfe.getMessage(), HttpStatus.NOT_FOUND);
    	}
    	return new ResponseEntity<String> (responseString, HttpStatus.OK);
    }
    
}
