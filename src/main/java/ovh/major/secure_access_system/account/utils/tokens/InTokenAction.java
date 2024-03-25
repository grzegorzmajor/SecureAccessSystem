package ovh.major.secure_access_system.account.utils.tokens;

public enum InTokenAction {

    ACCOUNT_REGISTRATION("Major_AccountRegistration"),
    PASSWORD_RESETTING_REQUEST("Major_PasswordResetRequest"),
    PASSWORD_RESETTING_FORM("Major_PasswordResettingForm");

    private final String message;

    InTokenAction(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
