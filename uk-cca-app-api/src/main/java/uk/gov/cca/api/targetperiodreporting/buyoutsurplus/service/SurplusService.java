package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusEntity;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.SurplusRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.SurplusMapper;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@Validated
@Service
@RequiredArgsConstructor
public class SurplusService {

    private final SurplusRepository surplusRepository;
    private final TargetPeriodService targetPeriodService;
    private static final SurplusMapper SURPLUS_MAPPER = Mappers.getMapper(SurplusMapper.class);

    @Transactional
    public void updateSurplusGained(SurplusUpdateDTO surplusUpdateDTO, Long accountId, AppUser appUser) {

        final SurplusCreate surplusCreate = SURPLUS_MAPPER.toSurplusCreate(surplusUpdateDTO, accountId, appUser);
        this.bankSurplus(surplusCreate);
    }

    @Transactional
    public void bankSurplus(@Valid @NotNull SurplusCreate surplusCreate) {
        surplusRepository.findByAccountIdAndTargetPeriod_BusinessId(surplusCreate.getAccountId(), surplusCreate.getTargetPeriodType())
                .ifPresentOrElse(
                        entity -> {
                            entity.setSurplusGained(surplusCreate.getSurplusGained());
                            entity.addHistory(SURPLUS_MAPPER.toSurplusHistory(surplusCreate));
                        },
                        () -> {
                            SurplusEntity newEntity = createNewSurplusEntity(surplusCreate);
                            newEntity.addHistory(SURPLUS_MAPPER.toSurplusHistory(surplusCreate));
                            surplusRepository.save(newEntity);
                        }
                );
    }

    private SurplusEntity createNewSurplusEntity(SurplusCreate surplusCreate) {
        TargetPeriod targetPeriod = targetPeriodService.findByTargetPeriodType(surplusCreate.getTargetPeriodType());

        return SURPLUS_MAPPER.toSurplusEntity(surplusCreate, targetPeriod);
    }
}
