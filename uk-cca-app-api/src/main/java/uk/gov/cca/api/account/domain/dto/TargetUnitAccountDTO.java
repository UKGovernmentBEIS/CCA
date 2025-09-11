package uk.gov.cca.api.account.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.account.domain.*;
import uk.gov.netz.api.account.domain.dto.AccountDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TargetUnitAccountDTO extends AccountDTO {

    @NotNull
    private TargetUnitAccountOperatorType operatorType;

    @Size(max = 255)
    private String companyRegistrationNumber;

    @Size(max = 255)
    private String registrationNumberMissingReason;

    @Size(max = 4)
    private List<@NotBlank @Size(max = 255)String> sicCodes;

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
