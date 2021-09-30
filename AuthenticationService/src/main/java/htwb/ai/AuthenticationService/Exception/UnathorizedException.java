package htwb.ai.AuthenticationService.Exception;

public class UnathorizedException extends Exception{
	private String message;
	
	public UnathorizedException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
