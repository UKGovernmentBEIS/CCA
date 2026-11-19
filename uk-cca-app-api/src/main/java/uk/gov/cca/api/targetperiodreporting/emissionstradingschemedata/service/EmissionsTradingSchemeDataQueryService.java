package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.repository.EmissionsTradingSchemeDataEntityRepository;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.transform.EmissionsTradingSchemeDataMapper;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class EmissionsTradingSchemeDataQueryService {


    private final EmissionsTradingSchemeDataEntityRepository emissionsTradingSchemeDataEntityRepository;
    private static final EmissionsTradingSchemeDataMapper ETS_MAPPER = Mappers.getMapper(EmissionsTradingSchemeDataMapper.class);

    public EmissionsTradingSchemeDataReportInfoDTO getEmissionsTradingSchemeDataReportInfo(Long facilityId,
                                                                                           Year targetPeriodYear) {

        return emissionsTradingSchemeDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear)
                .map(ETS_MAPPER::toEmissionsTradingSchemeDataReportInfoDTO)
                .orElse(null);
    }
}
