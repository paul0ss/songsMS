package htwb.ai.AuthenticationService.Exception;

public class ForbiddenException extends Exception{
	private String message;
	
	public ForbiddenException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
