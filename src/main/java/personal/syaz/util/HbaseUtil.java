package personal.syaz.util;

import org.apache.hadoop.hbase.util.Bytes;

public class HbaseUtil {

    public static byte[] generateRowKeyWithPrefix(String prefix, String userId) {
        String rowKey = String.format("%s-%s", prefix, userId);
        return Bytes.toBytes(rowKey);
    }
}
