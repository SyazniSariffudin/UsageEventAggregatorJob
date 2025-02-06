package personal.syaz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.YearMonth;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobStatusDto implements Serializable {
    private static final long serialVersionUID = 9192463531572210880L;

    private boolean success = true;
    private String jobId;
    private String processMonth;
    private YearMonth yearMonthToProcess;
    private String message = "";

    public String getJobId() {
        return UUID.randomUUID().toString();
    }
}
