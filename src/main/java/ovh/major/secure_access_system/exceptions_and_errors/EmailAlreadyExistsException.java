package ovh.major.secure_access_system.exceptions_and_errors;

public class EmailAlreadyExistsException extends RuntimeExceptionWithLog {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
