package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.SurplusRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation.SurplusValidatorService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@Validated
@Service
public class FacilitySurplusService extends AbstractSurplusService {
        
    public FacilitySurplusService(SurplusRepository surplusRepository, SurplusValidatorService validatorService) {
        super(surplusRepository, validatorService);
    }

    @Override
    protected PerformanceDataResourceType getResourceType() {
        return PerformanceDataResourceType.FACILITY;
    }

    @Transactional(readOnly = true)
    public SurplusGainedDTO getSurplusGainedWithHistoryFlag(Long facilityId) {
        return super.getSurplusGainedWithHistoryFlag(facilityId, SchemeVersion.CCA_3);
    }

    @Transactional(readOnly = true)
    public List<SurplusHistoryDTO> getSurplusHistory(Long facilityId) {
        return super.getSurplusHistory(facilityId, SchemeVersion.CCA_3);
    }

    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> getPositiveSurplusMap(List<Long> facilityIds, SchemeVersion schemeVersion) {
        return super.getSurplusByResourceIdInAndSchemeVersion(facilityIds, schemeVersion)
                .stream()
                .filter(entry -> entry.getSurplusGained().compareTo(BigDecimal.ZERO) > 0 )
                .collect(Collectors.toMap(SurplusDTO::getResourceId, SurplusDTO::getSurplusGained));
    }
    
    @Transactional
    public void updateSurplusGained(SurplusUpdateDTO surplusUpdateDTO, Long facilityId, AppUser appUser) {
        super.updateSurplusGained(surplusUpdateDTO, facilityId, SchemeVersion.CCA_3, appUser);
    }

    @Override
    @Transactional
    public void bankSurplus(@Valid @NotNull SurplusCreate surplusCreate, @NotNull SchemeVersion schemeVersion) {
        super.bankSurplus(surplusCreate, schemeVersion);
    }
    
    @Override
    @Transactional
    public void bankSurplus(@NotEmpty List<@Valid @NotNull SurplusCreate> surplusEntries, @NotNull SchemeVersion schemeVersion){
        super.bankSurplus(surplusEntries, schemeVersion);
    }
}
