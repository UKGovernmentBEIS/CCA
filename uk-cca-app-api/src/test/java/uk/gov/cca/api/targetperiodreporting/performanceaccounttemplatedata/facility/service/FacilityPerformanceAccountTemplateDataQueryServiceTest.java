package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.dto.FacilityPerformanceAccountTemplateDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.dto.FacilityPerformanceAccountTemplateDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.repository.FacilityPerformanceAccountTemplateDataCustomRepository;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.repository.FacilityPerformanceAccountTemplateDataEntityRepository;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataQueryServiceTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataQueryService facilityPerformanceAccountTemplateDataQueryService;

    @Mock
    private FacilityPerformanceAccountTemplateDataEntityRepository facilityPerformanceAccountTemplateDataEntityRepository;

    @Mock
    private FacilityPerformanceAccountTemplateDataCustomRepository facilityPerformanceAccountTemplateDataCustomRepository;

    @Test
    void getFacilityPerformanceAccountTemplateDataReportInfo() {
        final Long facilityId = 1L;
        final Year targetPeriodYear = Year.of(2026);

        final LocalDateTime submissionDate = LocalDate.of(2026, 1, 1).atStartOfDay();
        final FacilityPerformanceAccountTemplateDataEntity entity = FacilityPerformanceAccountTemplateDataEntity.builder()
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(1)
                .submissionDate(submissionDate)
                .build();
        final FacilityPerformanceAccountTemplateDataReportInfoDTO expected = FacilityPerformanceAccountTemplateDataReportInfoDTO.builder()
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(1)
                .submissionDate(submissionDate)
                .build();

        when(facilityPerformanceAccountTemplateDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.of(entity));

        // Invoke
        FacilityPerformanceAccountTemplateDataReportInfoDTO result = facilityPerformanceAccountTemplateDataQueryService
                .getFacilityPerformanceAccountTemplateDataReportInfo(facilityId, targetPeriodYear);

        // Verify
        assertThat(result).isEqualTo(expected);
        verify(facilityPerformanceAccountTemplateDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }

    @Test
    void getFacilityPerformanceAccountTemplateDataReportInfo_empty() {
        final Long facilityId = 1L;
        final Year targetPeriodYear = Year.of(2026);

        when(facilityPerformanceAccountTemplateDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.empty());

        // Invoke
        FacilityPerformanceAccountTemplateDataReportInfoDTO result = facilityPerformanceAccountTemplateDataQueryService
                .getFacilityPerformanceAccountTemplateDataReportInfo(facilityId, targetPeriodYear);

        // Verify
        assertThat(result).isNull();
        verify(facilityPerformanceAccountTemplateDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }

    @Test
    void getFacilityPerformanceAccountTemplateDataReportDetails() {
        final Long facilityId = 1L;
        final Year targetPeriodYear = Year.of(2026);

        final LocalDateTime submissionDate = LocalDate.of(2026, 1, 1).atStartOfDay();
        final FacilityPerformanceAccountTemplateDataContainer data = FacilityPerformanceAccountTemplateDataContainer.builder()
                .savingActions(List.of(FacilityPerformanceAccountTemplateSavingAction.builder()
                        .actionCategoryType(ActionCategoryType.NO_ACTION)
                        .notes("Notes")
                        .build()))
                .build();
        final FacilityPerformanceAccountTemplateDataEntity entity = FacilityPerformanceAccountTemplateDataEntity.builder()
                .targetPeriodYear(targetPeriodYear)
                .submissionDate(submissionDate)
                .data(data)
                .build();
        final FacilityPerformanceAccountTemplateDataReportDetailsDTO expected = FacilityPerformanceAccountTemplateDataReportDetailsDTO.builder()
                .targetPeriodYear(targetPeriodYear)
                .submissionDate(submissionDate)
                .data(data)
                .build();

        when(facilityPerformanceAccountTemplateDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.of(entity));

        // Invoke
        FacilityPerformanceAccountTemplateDataReportDetailsDTO result = facilityPerformanceAccountTemplateDataQueryService
                .getFacilityPerformanceAccountTemplateDataReportDetails(facilityId, targetPeriodYear);

        // Verify
        assertThat(result).isEqualTo(expected);
        verify(facilityPerformanceAccountTemplateDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }

    @Test
    void getFacilityPerformanceAccountTemplateDataReportDetails_not_found() {
        final Long facilityId = 1L;
        final Year targetPeriodYear = Year.of(2026);

        when(facilityPerformanceAccountTemplateDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                facilityPerformanceAccountTemplateDataQueryService
                        .getFacilityPerformanceAccountTemplateDataReportDetails(facilityId, targetPeriodYear));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(facilityPerformanceAccountTemplateDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }

    @Test
    void getSectorFacilityPerformanceDataReportListBySearchCriteria() {
        final Long sectorAssociationId = 1L;
        final SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria.builder()
                .targetPeriodYear(Year.of(2026))
                .paging(PagingRequest.builder().pageNumber(0).pageSize(25).build())
                .build();

        final SectorPerformanceAccountTemplateDataReportListDTO reportList = SectorPerformanceAccountTemplateDataReportListDTO.builder()
                .items(List.of(SectorPerformanceAccountTemplateDataReportItemDTO.builder().businessId("facility1").build()))
                .total(1L)
                .build();

        when(facilityPerformanceAccountTemplateDataCustomRepository
                .getSectorFacilityPerformanceDataReportListBySearchCriteria(sectorAssociationId, criteria))
                .thenReturn(reportList);

        // Invoke
        SectorPerformanceAccountTemplateDataReportListDTO result = facilityPerformanceAccountTemplateDataQueryService
                .getSectorPerformanceAccountTemplateDataReportListDTO(sectorAssociationId, criteria);

        // Verify
        assertThat(result).isEqualTo(reportList);
        verify(facilityPerformanceAccountTemplateDataCustomRepository, times(1))
                .getSectorFacilityPerformanceDataReportListBySearchCriteria(sectorAssociationId, criteria);
    }
}
