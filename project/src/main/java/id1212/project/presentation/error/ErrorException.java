package id1212.project.presentation.error;

// RENAME?
public class ErrorException extends RuntimeException {

	public ErrorException(String msg, int id){
		super(msg + id);
	}
}
