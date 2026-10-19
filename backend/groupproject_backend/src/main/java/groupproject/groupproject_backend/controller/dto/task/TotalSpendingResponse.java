package groupproject.groupproject_backend.controller.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalSpendingResponse {
    private double totalPayout;
    private double totalBudget;
}