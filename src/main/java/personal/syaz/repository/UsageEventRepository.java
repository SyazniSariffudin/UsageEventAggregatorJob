package personal.syaz.repository;

import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;

import java.time.LocalDate;
import java.time.YearMonth;

public class UsageEventRepository {

    public Scan getUsageEventScan(YearMonth yearMonth) {
        // Set up the HBase Scan
        Scan scan = new Scan();

        SingleColumnValueFilter startDateFilter = new SingleColumnValueFilter(
                Bytes.toBytes("cf"),
                Bytes.toBytes("date"),
                CompareOp.GREATER_OR_EQUAL,
                Bytes.toBytes(yearMonth.atDay(1).toString())
        );

        SingleColumnValueFilter endDateFilter = new SingleColumnValueFilter(
                Bytes.toBytes("cf"),
                Bytes.toBytes("date"),
                CompareOp.LESS_OR_EQUAL,
                Bytes.toBytes(yearMonth.atEndOfMonth().toString())
        );

        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(startDateFilter);
        filterList.addFilter(endDateFilter);

        scan.setFilter(filterList);

        return scan;
    }
}
