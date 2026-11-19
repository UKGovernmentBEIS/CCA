package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.dto.FacilityPerformanceAccountTemplateDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.dto.FacilityPerformanceAccountTemplateDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.repository.FacilityPerformanceAccountTemplateDataCustomRepository;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.repository.FacilityPerformanceAccountTemplateDataEntityRepository;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.transform.FacilityPerformanceAccountTemplateMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataQueryService {

    private final FacilityPerformanceAccountTemplateDataEntityRepository facilityPerformanceAccountTemplateDataEntityRepository;
    private final FacilityPerformanceAccountTemplateDataCustomRepository facilityPerformanceAccountTemplateDataCustomRepository;
    private static final FacilityPerformanceAccountTemplateMapper MAPPER = Mappers.getMapper(FacilityPerformanceAccountTemplateMapper.class);

    public FacilityPerformanceAccountTemplateDataReportInfoDTO getFacilityPerformanceAccountTemplateDataReportInfo(Long facilityId,
                                                                                                                   Year targetPeriodYear) {
        return facilityPerformanceAccountTemplateDataEntityRepository
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear)
                .map(MAPPER::toFacilityPerformanceAccountTemplateDataReportInfoDTO)
                .orElse(null);
    }

    public FacilityPerformanceAccountTemplateDataReportDetailsDTO getFacilityPerformanceAccountTemplateDataReportDetails(Long facilityId,
                                                                                                                         Year targetPeriodYear) {
        return facilityPerformanceAccountTemplateDataEntityRepository
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear)
                .map(MAPPER::toFacilityPerformanceAccountTemplateDataReportDetailsDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public SectorPerformanceAccountTemplateDataReportListDTO getSectorPerformanceAccountTemplateDataReportListDTO(
            Long sectorAssociationId, SectorPerformanceAccountTemplateDataReportSearchCriteria criteria) {
        return facilityPerformanceAccountTemplateDataCustomRepository
                .getSectorFacilityPerformanceDataReportListBySearchCriteria(sectorAssociationId, criteria);
    }
}
