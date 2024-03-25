package ovh.major.secure_access_system.exceptions_and_errors;

import lombok.extern.log4j.Log4j2;

@Log4j2
class RuntimeExceptionWithLog extends RuntimeException {

    public RuntimeExceptionWithLog(String message) {
        super(message);
        log.error(message);
    }
}
