package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusEntity;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.SurplusRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.SurplusMapper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation.SurplusValidatorService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@Validated
@Service
@RequiredArgsConstructor
public class SurplusService {

    private final SurplusRepository surplusRepository;
    private final SurplusValidatorService validatorService;
    private static final SurplusMapper SURPLUS_MAPPER = Mappers.getMapper(SurplusMapper.class);

    @Transactional
    public void updateAccountSurplusGained(SurplusUpdateDTO surplusUpdateDTO, Long accountId, AppUser appUser) {
        final SurplusCreate surplusCreate = SURPLUS_MAPPER.toSurplusCreate(surplusUpdateDTO, accountId, appUser);
        updateOrCreateSurplus(surplusCreate, SchemeVersion.CCA_2);
    }

    @Transactional
    public void updateFacilitySurplusGained(SurplusUpdateDTO surplusUpdateDTO, Long facilityId, AppUser appUser) {
        final SurplusCreate surplusCreate = SURPLUS_MAPPER.toSurplusCreate(surplusUpdateDTO, facilityId, appUser);
        updateOrCreateSurplus(surplusCreate, SchemeVersion.CCA_3);
    }

    @Transactional
    public void bankSurplus(@Valid @NotNull SurplusCreate surplusCreate, SchemeVersion schemeVersion) {
        updateOrCreateSurplus(surplusCreate, schemeVersion);
    }
    
    @Transactional
    public void bankSurplus(@NotEmpty List<@Valid @NotNull SurplusCreate> surplusEntries, @NotNull SchemeVersion schemeVersion) {
       
        validatorService.validateNoDuplicateResourceIds(surplusEntries);

        List<Long> resourceIds = surplusEntries.stream()
                .map(SurplusCreate::getResourceId)
                .toList();

        Map<Long, SurplusEntity> existingEntries = surplusRepository
                .findAllByResourceIdInAndSchemeVersion(resourceIds, schemeVersion)
                .stream()
                .collect(Collectors.toMap(
                        SurplusEntity::getResourceId,
                        Function.identity()
                ));
        
        PerformanceDataResourceType resourceType = schemeVersion.getResourceType();
        List<SurplusEntity> newEntities = new ArrayList<>();
        for (SurplusCreate create : surplusEntries) {

            SurplusEntity entity = existingEntries.get(create.getResourceId());
            if (entity != null) {
                entity.setSurplusGained(create.getSurplusGained());
                entity.addHistory(SURPLUS_MAPPER.toSurplusHistory(create));
            } else {
                entity = SURPLUS_MAPPER.toSurplusEntity(create, schemeVersion, resourceType);
                entity.addHistory(SURPLUS_MAPPER.toSurplusHistory(create));
                newEntities.add(entity);
            }
        }

        if(!newEntities.isEmpty()) {
            surplusRepository.saveAll(newEntities);
        }
    }

    private void updateOrCreateSurplus(SurplusCreate surplusCreate, SchemeVersion schemeVersion) {
        PerformanceDataResourceType resourceType = schemeVersion.getResourceType();
        surplusRepository.findByResourceIdAndSchemeVersion(surplusCreate.getResourceId(), schemeVersion)
                .ifPresentOrElse(
                        entity -> {
                            entity.setSurplusGained(surplusCreate.getSurplusGained());
                            entity.addHistory(SURPLUS_MAPPER.toSurplusHistory(surplusCreate));
                        },
                        () -> {
                            SurplusEntity newEntity = SURPLUS_MAPPER.toSurplusEntity(surplusCreate, schemeVersion, resourceType);
                            newEntity.addHistory(SURPLUS_MAPPER.toSurplusHistory(surplusCreate));
                            surplusRepository.save(newEntity);
                        }
                );
    }
    
}
