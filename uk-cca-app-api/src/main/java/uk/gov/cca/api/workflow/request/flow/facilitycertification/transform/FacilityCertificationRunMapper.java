package uk.gov.cca.api.workflow.request.flow.facilitycertification.transform;

import org.mapstruct.Mapper;

import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunSummary;
import uk.gov.netz.api.common.config.MapperConfig;

import java.util.Map;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FacilityCertificationRunMapper {

    default FacilityCertificationRunSummary toFacilityCertificationRunSummary(Map<Long, FacilityCertificationAccountState> accountStates) {
        long failedAccounts = accountStates.values().stream()
                .filter(acc -> !acc.isSucceeded())
                .count();
        long facilitiesCertified = accountStates.values().stream()
                .filter(FacilityCertificationAccountState::isSucceeded)
                .mapToLong(FacilityCertificationAccountState::getFacilitiesCertified)
                .sum();

        return FacilityCertificationRunSummary.builder()
                .totalAccounts((long) accountStates.size())
                .failedAccounts(failedAccounts)
                .facilitiesCertified(facilitiesCertified)
                .build();
    }
}
