package utilities;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;

/**
 * Serialize and deserialize objects
 *
 * @author Palak Jain
 */
public class JsonManager {
    /**
     * Parse JSON string into an object
     * @param reader Reader object
     * @param classOfT Type of Class
     * @return Parsed object
     */
    public static <T> T fromJson(Reader reader, Class<T> classOfT) {
        Gson gson = new Gson();

        T object = null;

        try {
            object = gson.fromJson(reader, classOfT);
        }
        catch (JsonSyntaxException exception) {
            System.out.println("Unable to parse json");
        }

        return object;
    }

    /**
     * Parse JSON string into an object
     * @param json JSON string
     * @param classOfT Type of Class
     * @return Paresed object
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        Gson gson = new Gson();

        T object = null;

        try {
            object = gson.fromJson(json, classOfT);
        }
        catch (JsonSyntaxException exception) {
            System.out.printf("Unable to parse json %s. %s \n", json, exception);
        }

        return object;
    }
}
