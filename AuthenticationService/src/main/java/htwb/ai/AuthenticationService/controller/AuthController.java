package htwb.ai.AuthenticationService.controller;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;

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

import htwb.ai.AuthenticationService.dao.DBUserDAO;
import htwb.ai.AuthenticationService.dao.IUserDAO;
import htwb.ai.AuthenticationService.model.User;
import htwb.ai.AuthenticationService.repository.UserRepository;

@RestController
@RequestMapping(value="/auth")
public class AuthController {
    
	private final UserRepository ur;
	
	private static HashMap<String, String> authTokens = new HashMap<String, String>();
    
    public AuthController (UserRepository repository) {
        ur = repository;
    }
    
    @PostMapping(produces = "text/plain", consumes = "application/json")
    public ResponseEntity<String> login(@RequestBody User user, @RequestHeader HttpHeaders headers){
    	
    	InetSocketAddress host = headers.getHost();
    	
        if (user == null || user.getUserId() == null ||
                user.getPassword() == null ) {
                return new ResponseEntity<String>("User cant be authenticated", 
                        HttpStatus.UNAUTHORIZED);
            }
        
        Optional<User> result = ur.findByUserId(user.getUserId());
        User DBuser = null;
        if(result.isEmpty()) {
        	DBuser = null;
        }else {
        	DBuser = result.get();
        }
        
        // If the user exists in the DB
        if(DBuser != null) {
        	System.out.println("DBuser is not null");
        	//if the password of the user matches the password in the DB
        	if(DBuser.getPassword().equals(user.getPassword())) {
        		return new ResponseEntity<String> (tokenGen(DBuser.getUserId()), HttpStatus.OK);
        	}else{
        		return new ResponseEntity<String>("Declined: Bad password!", 
                        HttpStatus.UNAUTHORIZED);
        	}
        }else {
        	System.out.println("DBuser is null");
        	return new ResponseEntity<String>("Declined: No such user!", 
                    HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * Checks if a received token belongs to any user in the system.
     * @param token for logging in the User
     * @return
     */
    @GetMapping(value="/{token}")
    public ResponseEntity<String> checkAuth(@PathVariable (value="token") String token){
    	if(checkToken(token)) {
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
     * @return the userspecifi information
     */
    @GetMapping(value="/getUser/{token}")
    public ResponseEntity<String> getUserByToken(@PathVariable (value="token") String token,
    		@RequestHeader HttpHeaders headers){
    	String serviceToken = "fuiwei72r723if";
    	String serviceTokenRecieved = headers.getFirst("ServiceToken");
    	if(serviceTokenRecieved == null) {
    		return new ResponseEntity<String> ("Bad request!", HttpStatus.BAD_REQUEST);
    	}
    	if(!serviceToken.equals(serviceTokenRecieved)) {
    		return new ResponseEntity<String> ("Request is not permitted", HttpStatus.FORBIDDEN);
    	}
    	String userId = getUserIdByToken(token);
    	if(userId == null) {
    		return new ResponseEntity<String> ("User doesnt exist", HttpStatus.NOT_FOUND);
    	}
    	User user = ur.findByUserId(userId).get();
    	return new ResponseEntity<String> (user.toJSONString(), HttpStatus.OK);
    }
    
    
    private static <K, V> K getKey(Map<K, V> map, V value) {
        for (Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    //Überprüft ob der Token existiert
    private boolean checkToken(String token) {
    	if(token != null && authTokens.containsValue(token)) {
        	return true;
        }else {
        	return false;
        }
    }
    
    private String getUserIdByToken(String token) {
    	if(token == null) {
    		return null;
    	}
    	return (String) getKey(authTokens, token);
    }
    
//    public static boolean checkUserExistence(String userId) {
//    	userDAO.getUserByUserId(userId);
//    }
    
    /**
     * Generates a token for user authentification
     * @param userId
     * @return a token in a String-format
     */
    private String tokenGen(String userId) {
    	int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        
        if(authTokens.containsKey(userId)) {
        	return authTokens.get(userId);
        }else {
        	String generatedToken = random.ints(leftLimit, rightLimit + 1)
        			.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        			.limit(targetStringLength)
        			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        			.toString();
        	
        	authTokens.put(userId, generatedToken);
        	
        	return generatedToken;
        }

    }
    
}
