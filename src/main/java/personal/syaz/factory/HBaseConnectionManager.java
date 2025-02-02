package personal.syaz.factory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HBaseConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(HBaseConnectionManager.class);

    // Volatile ensures visibility and prevents half-initialized instance issues
    private static volatile Connection connection = null;

    private HBaseConnectionManager() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() throws IOException {
        if (connection == null) {
            synchronized (HBaseConnectionManager.class) {
                if (connection == null) {
                    logger.info("Initializing HBase connection...");
                    connection = ConnectionFactory.createConnection(getConfiguration());
                }
            }
        }
        return connection;
    }

    public static Configuration getConfiguration() {
        return HBaseConfiguration.create();
    }

    public static Configuration getConfiguration(String inputTable) {
        Configuration hbaseConfiguration = HBaseConfiguration.create();
        hbaseConfiguration.set(TableInputFormat.INPUT_TABLE, inputTable);
        return hbaseConfiguration;
    }

    /**
     * Closes the HBase connection if it is open.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("HBase connection closed.");
            } catch (IOException e) {
                logger.error("Error while closing HBase connection: ", e);
            }
        }
    }
}


