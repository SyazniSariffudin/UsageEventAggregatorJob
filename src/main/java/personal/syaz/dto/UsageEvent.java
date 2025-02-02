package personal.syaz.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class UsageEvent implements Serializable {

    private static final long serialVersionUID = 627526094215256151L;

    private long id;
    private String userId;
    private LocalDate date;
    private long amount;

    public UsageEvent(long id, String userId, LocalDate date, long amount) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "UsageEvent{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", date='" + date + '\'' +
                ", amount=" + amount +
                '}';
    }
}
