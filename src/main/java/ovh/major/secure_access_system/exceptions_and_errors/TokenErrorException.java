package ovh.major.secure_access_system.exceptions_and_errors;

public class TokenErrorException extends RuntimeExceptionWithLog {
    public TokenErrorException(String message) {
        super(message);
    }
}
