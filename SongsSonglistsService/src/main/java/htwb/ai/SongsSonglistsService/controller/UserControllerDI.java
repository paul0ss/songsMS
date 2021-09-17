package htwb.ai.SongsSonglistsService.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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

import htwb.ai.SongsSonglistsService.dao.*;
import htwb.ai.SongsSonglistsService.model.*;

@RestController
@RequestMapping(value="/auth")
public class UserControllerDI {
    
	@Autowired
    private IUserDAO userDAO;
	
	private static HashMap<String, String> authTokens = new HashMap<String, String>();
    
    public UserControllerDI (IUserDAO uDAO) {
        this.userDAO = uDAO;
    }
    
    @PostMapping(produces = "text/plain", consumes = "application/json")
    public ResponseEntity<String> login(@RequestBody User user, @RequestHeader HttpHeaders headers){
    	
    	InetSocketAddress host = headers.getHost();
    	
        if (user == null || user.getUserId() == null ||
                user.getPassword() == null ) {
                return new ResponseEntity<String>("User cant be authenticated", 
                        HttpStatus.UNAUTHORIZED);
            }
        
        System.out.println("-------------------------------------");
        System.out.println(user.toString());
        //get User from Database
        User DBuser = userDAO.getUserByUserId(user.getUserId());
        
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
    
    private static <K, V> K getKey(Map<K, V> map, V value) {
        for (Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    //Überprüft ob der Token existiert
    public static boolean checkToken(String token) {
    	if(token != null && authTokens.containsValue(token)) {
        	return true;
        }else {
        	return false;
        }
    }
    
    public static String getUserIdByToken(String token) {
    	return (String) getKey(authTokens, token);
    }
    
//    public static boolean checkUserExistence(String userId) {
//    	userDAO.getUserByUserId(userId);
//    }
    
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
