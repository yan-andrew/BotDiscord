package access.secrets;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static String getProperty(String property){
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);
            return props.getProperty(property);
        } catch (IOException e) {
            return null;
        }
    }

    public static String getToken() {
        return getProperty("DISCORD_TOKEN");
    }

    public static String getUserDB() {
        return getProperty("USER_DB");
    }

    public static String getPasswordDB() {
        return getProperty("PASSWORD_DB");
    }

    public static String getIdDB() {
        return getProperty("ID_DB");
    }

    public static String getNetfily() {
        return getProperty("NETLIFY_TOKEN");
    }

    public static String getSite() {
        return getProperty("SITE_NETLIFY");
    }
}
