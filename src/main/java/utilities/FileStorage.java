package utilities;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Responsible for handling all requests for accessing local directories and files.
 *
 * @author Palak Jain
 */
public class FileStorage {

    /**
     * Check if the directory exists or not
     * @param directory Path of the directory
     * @return true if exists else false
     */
    public static boolean exists(String directory) {
        Path path = Paths.get(directory);
        return Files.exists(path);
    }

    /**
     * Create file in a mentioned directory
     * @param file Multipart file
     * @param directory Relative path of the directory
     * @param fileName File name
     * @return true if successful else false
     */
    public static boolean createFile(MultipartFile file, String directory, String fileName) {
        boolean created = false;
        Path path = Paths.get(directory, "/", fileName);

        try {
            Files.write(path, file.getBytes());
            created = true;

            System.out.printf("Created a file %s at a location %s.\n", fileName, directory);
        } catch (IOException ioException) {
            System.err.printf("Unable to create file %s/%s. %s.\n", directory, fileName, ioException);
        }

        return created;
    }

    /**
     * Delete the local file if exists
     * @param directory Relative path of the directory
     * @param fileName file name
     * @return true if successful else false
     */
    public static boolean deleteFile(String directory, String fileName) {
        boolean deleted = false;
        Path path = Paths.get(directory, "/", fileName);

        try {
            Files.deleteIfExists(path);
            deleted = true;

            System.out.printf("Deleted a file %s at a location %s.\n", fileName, directory);
        } catch (IOException ioException) {
            System.err.printf("Unable to delete file %s/%s. %s.\n", directory, fileName, ioException);
        }

        return deleted;
    }
}
