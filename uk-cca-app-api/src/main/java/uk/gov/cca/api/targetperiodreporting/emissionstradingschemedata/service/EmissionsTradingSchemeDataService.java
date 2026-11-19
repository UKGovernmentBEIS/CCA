package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataEntity;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.repository.EmissionsTradingSchemeDataEntityRepository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Optional;

@Validated
@Service
@RequiredArgsConstructor
public class EmissionsTradingSchemeDataService {

    private final EmissionsTradingSchemeDataEntityRepository emissionsTradingSchemeDataEntityRepository;

    @Transactional
    public int submitEmissionsTradingSchemeData(@NotNull @Valid EmissionsTradingSchemeDataContainer container, Long facilityId, Year targetPeriodYear) {
        EmissionsTradingSchemeDataEntity entity = emissionsTradingSchemeDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear)
                .map(existingEntity -> {
                    existingEntity.setData(container);
                    existingEntity.setReportVersion(existingEntity.getReportVersion() + 1);
                    existingEntity.setSubmissionDate(LocalDateTime.now());

                    return existingEntity;
                }).orElse(EmissionsTradingSchemeDataEntity.builder()
                        .facilityId(facilityId)
                        .targetPeriodYear(targetPeriodYear)
                        .data(container)
                        .reportVersion(1)
                        .build());

        EmissionsTradingSchemeDataEntity persistedEntity = emissionsTradingSchemeDataEntityRepository.save(entity);

        return persistedEntity.getReportVersion();
    }

    public Optional<EmissionsTradingSchemeDataContainer> getEmissionsTradingSchemeData(Long facilityId, Year targetPeriodYear) {
        return emissionsTradingSchemeDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear)
                .map(EmissionsTradingSchemeDataEntity::getData);
    }
}
