package configuration;

public class Config {

    private static String clientId;
    private static String clientSecret;
    private static String redirectUrl;

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
}
