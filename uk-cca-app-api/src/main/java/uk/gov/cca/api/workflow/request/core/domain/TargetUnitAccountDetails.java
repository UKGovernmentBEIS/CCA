package uk.gov.cca.api.workflow.request.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetUnitAccountDetails {

    private String operatorName;
    private TargetUnitAccountOperatorType operatorType;
    private String companyRegistrationNumber;
    private String registrationNumberMissingReason;
    private AccountAddressDTO address;
    private TargetUnitAccountContactDTO responsiblePerson;
    private TargetUnitAccountContactDTO administrativeContactDetails;
    private Long sectorAssociationId;
    private Long subsectorAssociationId;

}
