package uk.gov.cca.api.account.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetUnitAccountInfoResponseDTO {
	
    private List<TargetUnitAccountInfoDTO> accountsWithSiteContact;
    private boolean editable;
    private Long totalItems;
}
