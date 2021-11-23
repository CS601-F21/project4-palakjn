package web;

import configuration.Config;
import configuration.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import utilities.JsonManager;
import utilities.Strings;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@SpringBootApplication
public class HttpServer {

    public static void main(String[] args) {
        HttpServer server = new HttpServer();

        //Getting configuration file
        String configFileLocation = server.getConfig(args);

        if(!Strings.isNullOrEmpty(configFileLocation)) {
            //Read configuration file
            server.readConfig(configFileLocation);

            //validate if values are non-empty and files exist at a given location
            boolean isValid = server.verifyConfig();

            if(isValid) {
                SpringApplication.run(HttpServer.class, args);
            }
        }
    }

    /**
     * Read comment line arguments and return the location of the configuration file.
     * @param args Command line arguments being passed when running a program
     * @return location of configuration file if passed arguments are valid else null
     */
    private String getConfig(String[] args) {
        String configFileLocation = null;

        if(args.length == 2 &&
                args[0].equalsIgnoreCase("-config") &&
                !Strings.isNullOrEmpty(args[1])) {
            configFileLocation = args[1];
        }
        else {
            System.out.println("Invalid Arguments.");
        }

        return configFileLocation;
    }

    private void readConfig(String configFilename) {
        try {
            Map<String, String> values = JsonManager.fromJson(new FileReader(configFilename), Map.class);
            if(values != null) {
                Config.setClientId(values.getOrDefault(Constants.CLIENT_ID, null));
                Config.setClientSecret(values.getOrDefault(Constants.CLIENT_SECRET, null));
                Config.setRedirectUrl(values.getOrDefault(Constants.REDIRECT_URL, null));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private boolean verifyConfig() {
        boolean isValid = false;

        if(Strings.isNullOrEmpty(Config.getClientId()) ||
            Strings.isNullOrEmpty(Config.getClientSecret()) ||
            Strings.isNullOrEmpty(Config.getRedirectUrl())) {
            System.out.println("Invalid Config. Some properties missing");
        } else {
            isValid = true;
        }

        return isValid;
    }
}
