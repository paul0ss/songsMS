package htwb.ai.AuthenticationService.dto;

import javax.validation.constraints.NotEmpty;

public class LoginRequest {
		
		@NotEmpty(message="userId empty")
		private String userId;
		
		@NotEmpty(message="password is empty")
	    private String password;
		
//		public LoginRequest(String userId, String password) {
//			this.userId = userId;
//			this.password = password;
//		}
		
		public LoginRequest() {
			
		}
	    
	    
	    public String getUserId() {
	        return userId;
	    }
	    
	    public void setUserId(String userid) {
	        this.userId = userid;
	    }
	    
	    
	    public String getPassword() {
	    	return password;
	    }
	    
	    public void setPassword(String password) {
	    	this.password = password;
	    }
}
