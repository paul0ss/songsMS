package htwb.ai.SongsSonglistsService.exception;

public class ForbiddenException extends Exception{
	private String message;
	
	public ForbiddenException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
