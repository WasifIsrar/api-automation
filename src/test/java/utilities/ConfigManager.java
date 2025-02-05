package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigManager {
    private static Properties properties = new Properties();

    static {
        try {
            FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"resources"+File.separator+"config.properties");
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String readFileAsString(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON file: " + e.getMessage());
        }
    }
}
