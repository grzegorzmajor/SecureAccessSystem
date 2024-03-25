package ovh.major.secure_access_system.exceptions_and_errors;

public class ResourceNotFoundException extends RuntimeExceptionWithLog {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
