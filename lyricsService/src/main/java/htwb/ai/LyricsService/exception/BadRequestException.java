package htwb.ai.LyricsService.exception;

public class BadRequestException extends Exception{
	private String message;
	
	public BadRequestException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
