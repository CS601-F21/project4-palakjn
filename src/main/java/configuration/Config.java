package configuration;

/**
 * Class holds the configuration parameters to run the program.
 *
 * @author Palak Jain
 */

public class Config {
    private static String clientId;
    private static String clientSecret;
    private static String redirectUrl;
    private static String url;
    private static String username;
    private static String password;

    /**
     * Get the Slack Client ID
     */
    public static String getClientId() {
        return clientId;
    }

    /**
     * Set the Slack ClientId
     * @param clientId
     */
    public static void setClientId(String clientId) {
        Config.clientId = clientId;
    }

    /**
     * Get the client secret key
     * @return
     */
    public static String getClientSecret() {
        return clientSecret;
    }

    /**
     * Set the client secret key
     * @param clientSecret
     */
    public static void setClientSecret(String clientSecret) {
        Config.clientSecret = clientSecret;
    }

    /**
     * Get the redirect URL
     * @return
     */
    public static String getRedirectUrl() {
        return redirectUrl;
    }

    /**
     * Set the redirect URL.
     * @param redirectUrl
     */
    public static void setRedirectUrl(String redirectUrl) {
        Config.redirectUrl = redirectUrl;
    }

    /**
     * Get the JDBC connection URL
     * @return
     */
    public static String getUrl() {
        return url;
    }

    /**
     * Set the JDBC connection URL
     * @param url
     */
    public static void setUrl(String url) {
        Config.url = url;
    }

    /**
     * Get the username to connect to MySQL Server
     * @return
     */
    public static String getUsername() {
        return username;
    }

    /**
     * Set the username to connect to MySQL server
     * @param username
     */
    public static void setUsername(String username) {
        Config.username = username;
    }

    /**
     * Get the password to connect to MySQL Server
     * @return
     */
    public static String getPassword() {
        return password;
    }

    /**
     * Set the password to connect to MySQL Server
     * @param password
     */
    public static void setPassword(String password) {
        Config.password = password;
    }
}
