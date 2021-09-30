package htwb.ai.AuthenticationService.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import htwb.ai.AuthenticationService.Exception.BadRequestException;
import htwb.ai.AuthenticationService.Exception.ForbiddenException;
import htwb.ai.AuthenticationService.Exception.NotFoundException;
import htwb.ai.AuthenticationService.Exception.UnathorizedException;
import htwb.ai.AuthenticationService.dto.LoginRequest;
import htwb.ai.AuthenticationService.model.User;
import htwb.ai.AuthenticationService.repository.UserRepository;

@Service
public class AuthenticationService {
	
	private final UserRepository repository;
	
	private static HashMap<String, String> authTokens = new HashMap<String, String>();
	
	private String serviceToken = "fuiwei72r723if";
	
	public AuthenticationService(UserRepository repository) {
		this.repository = repository;
	}
	
	public String login(LoginRequest loginRequest) throws UnathorizedException{
			
	        User DBuser = repository.findByUserId(loginRequest.getUserId());
	        
	        // If the user exists in the DB
	        if(DBuser == null) {
	        	throw new UnathorizedException("Declined: No such user!");
	        }
	        // If the password is wrong
	        if(!DBuser.getPassword().equals(loginRequest.getPassword())) { 	
	        	throw new UnathorizedException("Declined: Bad password!");
	        }
	        
			return tokenGen(DBuser.getUserId());
	}
	
	public String tokenToUserJSON(String token, String serviceTokenRecieved) throws BadRequestException, ForbiddenException, NotFoundException {
    	if(serviceTokenRecieved == null) {
    		throw new BadRequestException("Bad request");
    	}
    	if(!serviceToken.equals(serviceTokenRecieved)) {
    		throw new ForbiddenException("Request is not permitted");
    	}
    	String userId = getUserIdByToken(token);
    	if(userId == null) {
    		throw new NotFoundException("User doesnt exist");
    	}
    	return repository.findByUserId(userId).toJSONString();
	}
	
    private String getUserIdByToken(String token) {
    	if(token == null) {
    		return null;
    	}
    	return (String) getKey(authTokens, token);
    }
    
    private static <K, V> K getKey(Map<K, V> map, V value) {
        for (Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * Checkt if the token exists
     * @param token
     * @return
     */
    public boolean checkToken(String token) {
    	if(token != null && authTokens.containsValue(token)) {
        	return true;
        }else {
        	return false;
        }
    }
	
    /**
     * Generates a token for user authentication
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
