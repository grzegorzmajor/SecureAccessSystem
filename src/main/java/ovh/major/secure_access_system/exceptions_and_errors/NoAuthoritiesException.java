package ovh.major.secure_access_system.exceptions_and_errors;

public class NoAuthoritiesException extends RuntimeExceptionWithLog {
    public NoAuthoritiesException(String message) {
        super(message);
    }
}
