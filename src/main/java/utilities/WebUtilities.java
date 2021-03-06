package utilities;
import configuration.Constants;
import models.ClientInfo;
import models.Event;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

/**
 * This class holds the middleware functionalities of Rest calls.
 *
 * @author Palak Jain
 */
public class WebUtilities {

    /**
     * Generate the nounce using session id
     * @param sessionId Active session Id
     * @return nounce
     */
    public static String generateNonce(String sessionId) {
        String sha256hex = DigestUtils.sha256Hex(sessionId);
        return sha256hex;
    }

    /**
     * Generates the URL to make the initial request to the authorize API.
     * @param clientId
     * @param state
     * @param nonce
     * @param redirectURI
     * @return
     */
    public static String generateSlackAuthorizeURL(String clientId, String state, String nonce, String redirectURI) {

        String url = String.format("https://%s/%s?%s=%s&%s=%s&%s=%s&%s=%s&%s=%s&%s=%s",
                Constants.HOST,
                Constants.AUTH_PATH,
                Constants.RESPONSE_TYPE_KEY,
                Constants.RESPONSE_TYPE_VALUE,
                Constants.SCOPE_KEY,
                Constants.SCOPE_VALUE,
                Constants.CLIENT_ID_KEY,
                clientId,
                Constants.STATE_KEY,
                state,
                Constants.NONCE_KEY,
                nonce,
                Constants.REDIRECT_URI_KEY,
                redirectURI
        );
        return url;
    }

    /**
     * Generates the URL to exchange the initial auth for a token.
     * @param clientId
     * @param clientSecret
     * @param code
     * @param redirectURI
     * @return
     */
    public static String generateSlackTokenURL(String clientId, String clientSecret, String code, String redirectURI) {

        String url = String.format("https://%s/%s?%s=%s&%s=%s&%s=%s&%s=%s",
                Constants.HOST,
                Constants.TOKEN_PATH,
                Constants.CLIENT_ID_KEY,
                clientId,
                Constants.CLIENT_SECRET_KEY,
                clientSecret,
                Constants.CODE_KEY,
                code,
                Constants.REDIRECT_URI_KEY,
                redirectURI
        );
        return url;
    }

    /**
     * Convert a JSON-formatted String to a Map.
     * @param jsonString
     * @return
     */
    public static Map<String, Object> jsonStrToMap(String jsonString) {
        return JsonManager.fromJson(new StringReader(jsonString), Map.class);
    }

    /**
     * Verify the response from the token API.
     * If successful, returns a ClientInfo object with information about the authed client.
     * Returns null if not successful.
     * @param map
     * @param sessionId
     * @return
     */
    public static ClientInfo verifyTokenResponse(Map<String, Object> map, String sessionId) {
        // verify ok: true
        if(!map.containsKey(Constants.OK_KEY) || !(boolean)map.get(Constants.OK_KEY)) {
            System.out.printf("Got false response from slack. %s.\n", map);
            return null;
        }

        // verify state is the users session cookie id
        if(!map.containsKey(Constants.STATE_KEY) || !map.get(Constants.STATE_KEY).equals(sessionId)) {
            System.out.println(map.get(Constants.STATE_KEY));
            System.out.println(sessionId);

            System.out.printf("Current session id %s is not equal to the session id %s received from slack.\n", sessionId, map.getOrDefault(Constants.STATE_KEY, null));
            return null;
        }

        // retrieve and decode id_token
        String idToken = (String)map.get("id_token");
        Map<String, Object> payloadMap = decodeIdTokenPayload(idToken);

        //verify nonce
        String expectedNonce = generateNonce(sessionId);
        String actualNonce = (String) payloadMap.get(Constants.NONCE_KEY);
        if(!expectedNonce.equals(actualNonce)) {
            System.out.printf("Expected nounce %s is different from the one received from slack i.e. %s.\n", expectedNonce, actualNonce);
            return null;
        }

        // extract name from response and return
        return new ClientInfo((String) payloadMap.get(Constants.NAME_KEY), (String) payloadMap.get(Constants.EMAIL_KEY));
    }

    /**
     * Download the file in a directory
     * @param file Multipart file
     * @param uniqueId file name
     * @return
     */
    public static String downloadImage(MultipartFile file, String uniqueId) {
        String imageUrl = null;

        if(!file.isEmpty()) {
            System.out.printf("File is not empty %s. \n", file.getOriginalFilename());

            if(FileStorage.exists(Constants.PHOTOS_DIRECTORY)) {
                String fileName = getFileName(uniqueId, file.getOriginalFilename());
                boolean fileCreated = FileStorage.createFile(file, Constants.PHOTOS_DIRECTORY, fileName);
                if(fileCreated) {
                    imageUrl = fileName;
                }
            }
        }

        return imageUrl;
    }

    /**
     * Responsible for checking if error or success alert needs to be printed on the page
     * @param model Pass attribute to HTML page when it is rendered
     * @param request Request object
     * @param errorKey Key String in html file which will receive the error message if exists
     * @param successKey Key String in html file which will receive the success message if exists
     */
    public static void checksForAlerts(Model model, HttpServletRequest request, String errorKey, String successKey) {

        //Checks if there is an error being propagated by other routes which are being redirected here.
        checkForErrorAlert(model, request, errorKey);

        //Checks if there is a success being propagated by other routes which are being redirected here.
        checkForSuccessAlert(model, request, successKey);
    }

    /**
     * Responsible for checking if any error alert needs to be printed in the page
     * @param model Pass attribute to HTML page when it is rendered
     * @param request Request object
     * @param errorKey Key String in html file which will receive the error message if exists
     */
    public static void checkForErrorAlert(Model model, HttpServletRequest request, String errorKey) {

        Object error = request.getSession().getAttribute(Constants.ERROR_KEY);
        if(error != null) {
            model.addAttribute(errorKey, error);
            //Removing it in order to not do it again.
            request.getSession().removeAttribute(Constants.ERROR_KEY);
        }
    }

    /**
     * Responsible for checking if any success alert needs to be printed in the page
     * @param model Pass attribute to HTML page when it is rendered
     * @param request Request object
     * @param successKey Key String in html file which will receive the success message if exists
     */
    public static void checkForSuccessAlert(Model model, HttpServletRequest request, String successKey) {
        Object success = request.getSession().getAttribute(Constants.SUCCESS__KEY);
        if(success != null) {
            model.addAttribute(successKey, success);
            //Removing it in order to not do it again.
            request.getSession().removeAttribute(Constants.SUCCESS__KEY);
        }
    }

    /**
     * Create the file name with the passes unique id and with the extension of the original file name.
     * @param uniqueId
     * @param fileName
     * @return
     */
    private static String getFileName(String uniqueId, String fileName) {
        String extension = getExtension(fileName).isPresent() ? getExtension(fileName).get() : ".png";
        return String.format("%s.%s", uniqueId, extension);
    }

    /**
     * Gets the extension of file name
     *
     * Ref: https://www.baeldung.com/java-file-extension
     * @param filename
     * @return
     */
    private static Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    /**
     *
     * From the Slack documentation:
     * id_token is a standard JSON Web Token (JWT). You can decode it with off-the-shelf libraries in any programming
     * language, and most packages that handle OpenID will handle JWT decoding.
     *
     * Method decodes the String id_token and returns a Map with the contents of the payload.
     *
     * @param idToken
     * @return
     */
    private static Map<String, Object> decodeIdTokenPayload(String idToken) {
        // Decoding process taken from:
        // https://www.baeldung.com/java-jwt-token-decode
        String[] chunks = idToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        // convert the id_token payload to a map
        return JsonManager.fromJson(new StringReader(payload), Map.class);
    }
}
