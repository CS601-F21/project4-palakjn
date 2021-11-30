package utilities;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileStorage {

    public static boolean createDirectory(String directory) {
        boolean created = false;

        try {
            Path path = Paths.get(directory);
            if(!Files.exists(path)) {
                Files.createDirectory(path);
            }
            created = true;

            System.out.printf("Created a directory %s.\n", directory);
        } catch (IOException ioException) {
            System.err.printf("Unable to create directory %s. %s.\n", directory, ioException);
        }

        return created;
    }

    public static boolean exists(String directory) {
        Path path = Paths.get(directory);
        return Files.exists(path);
    }

    public static boolean createFile(MultipartFile file, String directory, String fileName) {
        boolean created = false;

        try {
            Path path = Paths.get(directory, "/", fileName);
            Files.write(path, file.getBytes());
            created = true;

            System.out.printf("Created a file %s at a location %s.\n", fileName, directory);
        } catch (IOException ioException) {
            System.err.printf("Unable to create file %s/%s. %s.\n", directory, fileName, ioException);
        }

        return created;
    }
}
