package personal.syaz.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class JobStatusDto implements Serializable {
    private static final long serialVersionUID = 9192463531572210880L;

    private boolean success = true; // Track job success status
    private String jobId = UUID.randomUUID().toString();
    private LocalDate processMonth;
    private String message = "";

    public LocalDate getProcessMonth() {
        return processMonth;
    }

    public void setProcessMonth(LocalDate processMonth) {
        this.processMonth = processMonth;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
