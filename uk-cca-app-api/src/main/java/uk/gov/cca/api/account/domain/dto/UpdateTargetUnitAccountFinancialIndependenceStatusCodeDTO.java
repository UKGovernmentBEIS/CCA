package uk.gov.cca.api.account.domain.dto;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO {

    @NotNull
    private FinancialIndependenceStatus financialIndependenceStatus;
}
