package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
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
public class AccountSurplusService extends AbstractSurplusService {
    
    public AccountSurplusService(SurplusRepository surplusRepository, SurplusValidatorService validatorService) {
        super(surplusRepository, validatorService);
    }
    
    @Override
    public PerformanceDataResourceType getResourceType() {
        return PerformanceDataResourceType.ACCOUNT;
    }
    
    @Transactional(readOnly = true)
    public SurplusGainedDTO getSurplusGainedWithHistoryFlag(Long accountId) {
        return super.getSurplusGainedWithHistoryFlag(accountId, SchemeVersion.CCA_2);
    }
    
    public Optional<SurplusDTO> getSurplus(Long accountId) {
        return super.getSurplusByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2);
    }
    
    @Transactional(readOnly = true)
    public List<SurplusHistoryDTO> getSurplusHistory(Long accountId) {
        return super.getSurplusHistory(accountId, SchemeVersion.CCA_2);
    }
    
    @Transactional
    public void updateSurplusGained(SurplusUpdateDTO surplusUpdateDTO, Long accountId, AppUser appUser) {
        super.updateSurplusGained(surplusUpdateDTO, accountId, SchemeVersion.CCA_2, appUser);
    }
    
    @Transactional
    public void bankSurplus(@Valid @NotNull SurplusCreate surplusCreate) {
        super.bankSurplus(surplusCreate, SchemeVersion.CCA_2);
    }
}
