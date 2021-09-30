package htwb.ai.LyricsService.exception;

public class UnathorizedException extends Exception{
	private String message;
	
	public UnathorizedException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
