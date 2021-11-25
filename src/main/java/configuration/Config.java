package configuration;

public class Config {
    private static String clientId;
    private static String clientSecret;
    private static String redirectUrl;
    private static String url;
    private static String username;
    private static String password;

    public static String getClientId() {
        return clientId;
    }

    public static void setClientId(String clientId) {
        Config.clientId = clientId;
    }

    public static String getClientSecret() {
        return clientSecret;
    }

    public static void setClientSecret(String clientSecret) {
        Config.clientSecret = clientSecret;
    }

    public static String getRedirectUrl() {
        return redirectUrl;
    }

    public static void setRedirectUrl(String redirectUrl) {
        Config.redirectUrl = redirectUrl;
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        Config.url = url;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Config.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Config.password = password;
    }
}
