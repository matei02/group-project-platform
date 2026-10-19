package groupproject.groupproject_backend.controller.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WithdrawRequest {
    private String tokenId;
    private long amount;
}
