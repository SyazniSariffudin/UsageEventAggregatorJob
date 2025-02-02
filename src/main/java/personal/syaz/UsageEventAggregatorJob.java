package personal.syaz;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import personal.syaz.dto.JobStatusDto;
import personal.syaz.dto.UsageEvent;
import personal.syaz.factory.HBaseConnectionManager;
import personal.syaz.factory.SparkFactory;
import personal.syaz.repository.JobStatusRepository;
import personal.syaz.repository.UsageEventRepository;
import personal.syaz.util.DateUtils;
import scala.Tuple2;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil.convertScanToString;

public class UsageEventAggregatorJob {
    private static final Logger logger = LoggerFactory.getLogger(UsageEventAggregatorJob.class);

    public static void main(String[] args) {
        logger.info("Starting HBaseSparkExample application with parameterized configuration...");

        if (args.length == 0) {
            logger.error("No date argument provided.");
            return;
        }

        // Parse input to YearMonth
        YearMonth yearMonth = YearMonth.parse(args[0], DateTimeFormatter.ofPattern("yyyy-MM"));

        JobStatusDto jobStatusDto = new JobStatusDto();
        SparkSession spark = SparkFactory.getInstance();
        try {
            JavaRDD<UsageEvent> usageEventRDD = getUsageEventJavaRDD(spark, yearMonth);
            JavaPairRDD<String, Long> groupedByUserRDD = groupedUsageEventByUserRDD(usageEventRDD);
            writeAggregateResult(groupedByUserRDD);
        } catch (Exception e) {
            logger.error("An error occurred while processing HBase data with Spark:", e);
            jobStatusDto.setSuccess(Boolean.FALSE);
            jobStatusDto.setMessage(e.getMessage());
        } finally {
            JobStatusRepository jobStatusRepository = new JobStatusRepository();
            jobStatusRepository.insertJobStatus(jobStatusDto);
            try {
                if (spark != null) {
                    spark.stop();
                    logger.info("SparkSession stopped.");
                }
            } catch (Exception e) {
                logger.error("Failed to close resources:", e);
            }
        }
    }

    private static JavaRDD<UsageEvent> getUsageEventJavaRDD(SparkSession spark, YearMonth yearMonth) throws IOException {
        // Read HBase configurations
        String inputTable = "usage_event";

        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        // HBase configuration
        Configuration hbaseConf = HBaseConnectionManager.getConfiguration(inputTable);

        // Read data from HBase table
        UsageEventRepository usageEventRepository = new UsageEventRepository();
        hbaseConf.set(TableInputFormat.SCAN, convertScanToString(usageEventRepository.getUsageEventScan(yearMonth)));
        JavaPairRDD<ImmutableBytesWritable, Result> hbaseRDD = jsc.newAPIHadoopRDD(
                hbaseConf,
                TableInputFormat.class,
                ImmutableBytesWritable.class,
                Result.class
        );

        logger.info("Successfully read data from HBase table: {}, with size: {}", inputTable, hbaseRDD.count());

        // Map HBase Result to a JavaRDD of UsageEvent
        return hbaseRDD.map(tuple -> {
            Result result = tuple._2;
            String id = Bytes.toString(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("id")));
            String userId = Bytes.toString(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("user_id")));
            String date = Bytes.toString(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("date")));
            String amount = Bytes.toString(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("amount")));

            UsageEvent usageEvent = new UsageEvent(Long.parseLong(id), userId, DateUtils.toDate(date), Long.parseLong(amount));
            logger.info(usageEvent.toString());
            return usageEvent;
        });
    }

    private static JavaPairRDD<String, Long> groupedUsageEventByUserRDD(JavaRDD<UsageEvent> usageEventRDD) {
        // Group by user_id and sum the amount
        logger.info("Grouping filtered data by user_id and summing the amount...");
        return usageEventRDD
                .mapToPair(event -> new Tuple2<>(event.getUserId(), event.getAmount()))
                .reduceByKey(Long::sum);
    }

    private static void writeAggregateResult(JavaPairRDD<String, Long> groupedByUserRDD) throws IOException {
        String outputTable = "usage_event_aggregator";

        // Write results back to HBase
        logger.info("Writing aggregated results into HBase table: {}", outputTable);

        Connection hbaseConnection = HBaseConnectionManager.getConnection();
        Table aggregatorTable = hbaseConnection.getTable(TableName.valueOf(outputTable));

        groupedByUserRDD.collect().forEach(tuple -> {
            String userId = tuple._1;
            Long totalAmount = tuple._2;

            logger.info("Inserting record into {}: userId={}, totalAmount={}", outputTable, userId, totalAmount);

            Put put = new Put(Bytes.toBytes(userId));
            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("total_amount"), Bytes.toBytes(totalAmount));

            try {
                aggregatorTable.put(put);
                logger.info("Inserted record into {}: userId={}, totalAmount={}", outputTable, userId, totalAmount);
            } catch (Exception e) {
                logger.error("Failed to insert record for userId={}: {}", userId, e.getMessage());
            }
        });
    }
}
