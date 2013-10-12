package reedey.shared.exceptions;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = -4750544849790158105L;

	public ServiceException() {

	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable caught) {
		super(message, caught);
	}

	public ServiceException(Throwable caught) {
		super(caught);
	}
}
