package id1212.project.error;


public class ErrorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErrorException(String msg, int id){
		super(msg + id);
	}
}
