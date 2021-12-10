package configuration;

/**
 * The class holding constant values. If there is a change in hardcoded strings, there will be only one place to change it.
 *
 * @author Palak Jain
 */
public class Constants {

    public static final String HOST = "slack.com";
    public static final String AUTH_PATH = "openid/connect/authorize";
    public static final String TOKEN_PATH = "api/openid.connect.token";
    public static final String RESPONSE_TYPE_KEY = "response_type";
    public static final String RESPONSE_TYPE_VALUE= "code";
    public static final String CODE_KEY= "code";
    public static final String SCOPE_KEY = "scope";
    public static final String SCOPE_VALUE = "openid%20profile%20email";
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String CLIENT_SECRET_KEY = "client_secret";
    public static final String STATE_KEY = "state";
    public static final String NONCE_KEY = "nonce";
    public static final String REDIRECT_URI_KEY = "redirect_uri";
    public static final String OK_KEY = "ok";

    public static final String CLIENT_USER_ID = "client_info_key";
    public static final String ERROR_KEY = "error";
    public static final String SUCCESS__KEY = "success";
    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String REDIRECT_URL = "redirectUrl";
    public static final String SQL_SERVER_URL = "url";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public enum METHOD {
        POST,
        GET
    }
    public static final String IS_AUTHED_KEY = "is_authed";
    public static final String NAME_KEY = "name";
    public static final String EMAIL_KEY = "email";

    public static final String PHOTOS_DIRECTORY = "event-photos";

    public static final String SHARED_BY_STRING = "Shared by %s";
    public static final String BOUGHT_STRING = "Bought %s tickets";
    public static final String SHARED_TO_STRING = "Shared %s number of tickets to %s";

    //Error messages
    public static class ERROR_MESSAGES {
        public static final String GENERIC = "Oops! Some problem occurred. Try again later!. Contact customer support if problem persist.";
        public static final String EVENT_NOT_CREATED = "Unable to create event. Try Again Later!";
        public static final String EVENT_NOT_UPDATED = "Unable to update event. Try Again Later!";
        public static final String EVENT_NOT_DELETED = "Unable to delete event. Try Again Later!";
        public static final String EVENT_NOT_BOOKED = "Unable to book an event. Try Again Later!";
        public static final String TICKET_NOT_SENT = "Unable to send ticket. Try Again Later!";
    }

    //Success messages
    public static class SUCCESS_MESSAGES {
        public static final String EVENT_CREATED = "Created New Event";
        public static final String EVENT_UPDATED = "Updated the Event";
        public static final String EVENT_DELETED = "Deleted the Event";
    }
}
