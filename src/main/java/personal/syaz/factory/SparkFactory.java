package personal.syaz.factory;

import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SparkFactory {

    private static final Logger logger = LoggerFactory.getLogger(SparkFactory.class);
    private static SparkSession sparkSession;

    private SparkFactory() {
        // Private constructor to prevent instantiation
    }

    public static SparkSession getInstance() {
        if (sparkSession == null) {
            synchronized (SparkFactory.class) {
                if (sparkSession == null) {
                    Properties properties = getProperties();

                    // Read Spark configurations
                    String appName = properties.getProperty("spark.app.name");
                    String master = properties.getProperty("spark.master");
                    sparkSession = SparkSession.builder()
                            .appName(appName)
                            .master(master)
                            .getOrCreate();

                    logger.info("SparkSession initialized with appName: {}, master: {}", appName, master);
                }
            }
        }
        return sparkSession;
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = SparkFactory.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new IOException("Unable to find config.properties in classpath");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}

