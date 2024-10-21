package uk.gov.cca.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetUnitAccountDetailsDTO {

    private Long id;
    private String businessId;
    private TargetUnitAccountStatus status;
    private String name;
    private CompetentAuthorityEnum competentAuthority;
    private TargetUnitAccountOperatorType operatorType;
    private String companyRegistrationNumber;
    private String registrationNumberMissingReason;
    private String sicCode;
    private Long sectorAssociationId;
    private Long subsectorAssociationId;
    private FinancialIndependenceStatus financialIndependenceStatus;
    private AccountAddressDTO address;
    private TargetUnitAccountContactDTO responsiblePerson;
    private TargetUnitAccountContactDTO administrativeContactDetails;
    private String createdBy;
}
