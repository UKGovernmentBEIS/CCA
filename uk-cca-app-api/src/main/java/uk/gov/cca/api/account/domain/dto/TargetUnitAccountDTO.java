package uk.gov.cca.api.account.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.account.domain.*;
import uk.gov.netz.api.account.domain.dto.AccountDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TargetUnitAccountDTO extends AccountDTO {

    @NotNull
    private TargetUnitAccountOperatorType operatorType;

    private String companyRegistrationNumber;

    private String registrationNumberMissingReason;

    private String sicCode;

    private Long subsectorAssociationId;

    @NotNull
    private CcaEmissionTradingScheme emissionTradingScheme;

    @NotNull
    private TargetUnitAccountStatus status;

    @NotNull
    private Long sectorAssociationId;

    @NotNull
    private AccountAddressDTO address;

    private FinancialIndependenceStatus financialIndependenceStatus;

    @Valid
    @NotNull
    private TargetUnitAccountContactDTO responsiblePerson;

    @Valid
    @NotNull
    private TargetUnitAccountContactDTO administrativeContactDetails;

    @NotNull
    private Boolean isCompanyRegistrationNumber;

    private String createdBy;

    private LocalDateTime creationDate;

    private String businessId;
}
