package ovh.major.secure_access_system.security.error;

import org.springframework.http.HttpStatus;

record ErrorResponse(String message, HttpStatus status) {
}
