package groupproject.groupproject_backend.controller.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserResponse {
    private Long id;
    private String username;
    private double virtualMoney;
}
