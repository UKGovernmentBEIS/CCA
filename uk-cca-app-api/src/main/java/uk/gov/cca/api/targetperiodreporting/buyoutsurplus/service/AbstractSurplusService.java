package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusEntity;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.SurplusRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.SurplusMapper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation.SurplusValidatorService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@RequiredArgsConstructor
public abstract class AbstractSurplusService {
    
    protected final SurplusRepository surplusRepository;
    protected final SurplusValidatorService validatorService;
    private static final SurplusMapper SURPLUS_MAPPER = Mappers.getMapper(SurplusMapper.class);
    
    protected SurplusGainedDTO getSurplusGainedWithHistoryFlag(Long resourceId, SchemeVersion schemeVersion) {
        validateVersion(schemeVersion);
        return surplusRepository.findByResourceIdAndSchemeVersion(resourceId, schemeVersion)
                .map(SURPLUS_MAPPER::toSurplusGainedDTO)
                .orElse(SurplusGainedDTO.builder()
                        .schemeVersion(schemeVersion)
                        .surplusGained(BigDecimal.ZERO)
                        .hasHistory(false)
                        .build());
    }
    
    protected Optional<SurplusDTO> getSurplusByResourceIdAndSchemeVersion(Long resourceId, SchemeVersion schemeVersion) {
        validateVersion(schemeVersion);
        return surplusRepository.findByResourceIdAndSchemeVersion(resourceId, schemeVersion)
                .map(SURPLUS_MAPPER::toSurplusDTO);
    }
    
    protected List<SurplusDTO> getSurplusByResourceIdInAndSchemeVersion(List<Long> resourceIds, SchemeVersion schemeVersion) {
        validateVersion(schemeVersion);
        return surplusRepository
                .findAllByResourceIdInAndSchemeVersion(resourceIds, schemeVersion)
                .stream()
                .map(SURPLUS_MAPPER::toSurplusDTO)
                .collect(Collectors.toList());
    }
    
    protected List<SurplusHistoryDTO> getSurplusHistory(Long resourceId, SchemeVersion schemeVersion) {
        validateVersion(schemeVersion);
        List<SurplusHistory> surplusHistories = surplusRepository.findByResourceIdAndSchemeVersion(resourceId, schemeVersion)
                .map(SurplusEntity::getSurplusHistoryList).orElse(List.of());

        return surplusHistories.stream().map(SURPLUS_MAPPER::toSurplusHistoryDTO).toList();
    }
    
    protected void updateSurplusGained(SurplusUpdateDTO surplusUpdateDTO, Long resourceId, SchemeVersion schemeVersion, AppUser appUser) {
        validateVersion(schemeVersion);
        final SurplusCreate surplusCreate = SURPLUS_MAPPER.toSurplusCreate(surplusUpdateDTO, resourceId, appUser);
        updateOrCreateSurplus(surplusCreate, schemeVersion);
    }
    
    protected void bankSurplus(@Valid @NotNull SurplusCreate surplusCreate, @NotNull SchemeVersion schemeVersion) {
        validateVersion(schemeVersion);
        updateOrCreateSurplus(surplusCreate, schemeVersion);
    }
    
    protected void bankSurplus(@NotEmpty List<@Valid @NotNull SurplusCreate> surplusEntries, @NotNull SchemeVersion schemeVersion) {
        validateVersion(schemeVersion);
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
        
        List<SurplusEntity> newEntities = new ArrayList<>();
        for (SurplusCreate create : surplusEntries) {

            SurplusEntity entity = existingEntries.get(create.getResourceId());
            if (entity != null) {
                entity.setSurplusGained(create.getSurplusGained());
                entity.addHistory(SURPLUS_MAPPER.toSurplusHistory(create));
            } else {
                entity = SURPLUS_MAPPER.toSurplusEntity(create, schemeVersion, getResourceType());
                entity.addHistory(SURPLUS_MAPPER.toSurplusHistory(create));
                newEntities.add(entity);
            }
        }

        if(!newEntities.isEmpty()) {
            surplusRepository.saveAll(newEntities);
        }
    }

    private void updateOrCreateSurplus(SurplusCreate surplusCreate, SchemeVersion schemeVersion) {
        surplusRepository.findByResourceIdAndSchemeVersion(surplusCreate.getResourceId(), schemeVersion)
                .ifPresentOrElse(
                        entity -> {
                            entity.setSurplusGained(surplusCreate.getSurplusGained());
                            entity.addHistory(SURPLUS_MAPPER.toSurplusHistory(surplusCreate));
                        },
                        () -> {
                            SurplusEntity newEntity = SURPLUS_MAPPER.toSurplusEntity(surplusCreate, schemeVersion, getResourceType());
                            newEntity.addHistory(SURPLUS_MAPPER.toSurplusHistory(surplusCreate));
                            surplusRepository.save(newEntity);
                        }
                );
    }
    
    protected abstract PerformanceDataResourceType getResourceType();
    
    protected void validateVersion(SchemeVersion schemeVersion) {
        if (schemeVersion.getResourceType() != this.getResourceType()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }
    
}
