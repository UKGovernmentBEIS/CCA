package uk.gov.cca.api.account.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.netz.api.account.domain.dto.AccountSearchResultInfoDTO;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CcaAccountSearchResultInfoDTO extends AccountSearchResultInfoDTO {

    public CcaAccountSearchResultInfoDTO(Long id, String name, String businessId, String status) {
        super(id, name, businessId, status != null ? TargetUnitAccountStatus.valueOf(status) : null);
    }
}
