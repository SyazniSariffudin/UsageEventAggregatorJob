package personal.syaz.repository;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import personal.syaz.dto.JobStatusDto;
import personal.syaz.factory.HBaseConnectionManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JobStatusRepository {

    private static final Logger logger = LoggerFactory.getLogger(JobStatusRepository.class);

    public void insertJobStatus(JobStatusDto jobStatusDto) {
        try (Connection connection = HBaseConnectionManager.getConnection();
             Table jobStatusTable = connection.getTable(TableName.valueOf("job_status"))) {

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            Put put = new Put(Bytes.toBytes(jobStatusDto.getJobId()));
            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("status"), Bytes.toBytes(String.valueOf(jobStatusDto.isSuccess())));
            if (jobStatusDto.getProcessMonth() != null)
                put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("yearmonth_to_process"), Bytes.toBytes(jobStatusDto.getProcessMonth()));
            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("timestamp"), Bytes.toBytes(timestamp));
            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("message"), Bytes.toBytes(jobStatusDto.getMessage()));

            jobStatusTable.put(put);
            logger.info("Job status inserted into HBase table 'job_status': jobId={}, status={}, message={}", jobStatusDto.getJobId(), jobStatusDto.isSuccess(), jobStatusDto.getMessage());

        } catch (Exception e) {
            logger.error("Failed to insert job status into HBase:", e);
        }
    }
}
