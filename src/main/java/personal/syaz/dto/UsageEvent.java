package personal.syaz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsageEvent implements Serializable {

    private static final long serialVersionUID = 627526094215256151L;

    private long id;
    private String userId;
    private LocalDate date;
    private long amount;
}
