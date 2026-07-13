package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.SectorFacilityPerformanceDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.SectorFacilityPerformanceDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.SectorFacilityPerformanceDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityDataCustomRepository;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityValidator;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class SectorPerformanceDataFacilityDataReportServiceTest {

	@Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private PerformanceDataFacilityDataCustomRepository repository;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Mock
    private PerformanceDataFacilityReferenceDataService referenceDataService;

    @Mock
    private PerformanceDataFacilityValidator validator;

    @InjectMocks
    private SectorPerformanceDataFacilityDataReportService service;
    
    @Test
    void shouldReturnEmptyWhenNoTargetPeriodsExist() {

        var criteria = new SectorFacilityPerformanceDataReportSearchCriteria();
        criteria.setTargetPeriodType(TargetPeriodType.TP7);
        criteria.setTargetPeriodReportType(PerformanceDataReportType.FINAL);

        when(targetPeriodService.getTargetPeriodYearsByTypeAndReportType(any(), any()))
                .thenReturn(List.of());

        var result = service.getSectorFacilityPerformanceDataReportList(1L, criteria);

        assertThat(result.getPerformanceDataReportItems()).isEmpty();
        assertThat(result.getTotal()).isZero();

        verifyNoInteractions(repository);
    }
    
    @Test
    void shouldThrowWhenMoreThanOneTargetPeriodReturned() {

    	var criteria = new SectorFacilityPerformanceDataReportSearchCriteria();
        criteria.setTargetPeriodType(TargetPeriodType.TP7);
        criteria.setTargetPeriodReportType(PerformanceDataReportType.FINAL);
        
        when(targetPeriodService.getTargetPeriodYearsByTypeAndReportType(any(), any()))
                .thenReturn(List.of(mock(TargetPeriodYear.class),
                                    mock(TargetPeriodYear.class)));

        assertThatThrownBy(() ->
                service.getSectorFacilityPerformanceDataReportList(1L, criteria))
                .isInstanceOf(BusinessException.class);
    }
    
    @Test
    void shouldReturnSubmittedItemsOnly() {
        TargetPeriodYear targetPeriodYear = mock(TargetPeriodYear.class);

        SectorFacilityPerformanceDataReportSearchCriteria criteria =
                new SectorFacilityPerformanceDataReportSearchCriteria();
        criteria.setTargetPeriodType(TargetPeriodType.TP7);
        criteria.setTargetPeriodReportType(PerformanceDataReportType.FINAL);
        criteria.setReportStatus(PerformanceDataFacilityTargetPeriodResultType.SUBMITTED);
        criteria.setPaging(PagingRequest.builder().pageNumber(0).pageSize(5).build());

        when(targetPeriodService.getTargetPeriodYearsByTypeAndReportType(
        		TargetPeriodType.TP7,
        		PerformanceDataReportType.FINAL.name()))
                .thenReturn(List.of(targetPeriodYear));

        SectorFacilityPerformanceDataReportItemDTO item =
                SectorFacilityPerformanceDataReportItemDTO.builder()
                        .facilityBusinessId("FAC001")
                        .build();

        SectorFacilityPerformanceDataReportListDTO submitted =
                SectorFacilityPerformanceDataReportListDTO.builder()
                        .performanceDataReportItems(List.of(item))
                        .total(1L)
                        .build();

        when(repository.getSectorFacilityPerformanceDataReportListSubmittedBySearchCriteria(
                1L, criteria, targetPeriodYear))
                .thenReturn(submitted);

        SectorFacilityPerformanceDataReportListDTO result =
                service.getSectorFacilityPerformanceDataReportList(1L, criteria);

        // Assert
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getPerformanceDataReportItems())
                .containsExactly(item);

        verify(repository)
                .getSectorFacilityPerformanceDataReportListSubmittedBySearchCriteria(
                        1L, criteria, targetPeriodYear);

        verifyNoInteractions(validator);
        verifyNoInteractions(referenceDataService);
        verifyNoInteractions(underlyingAgreementQueryService);
        verify(repository, never())
                .getSectorFacilityPerformanceDataReportListOutstandingBySearchCriteria(
                        anyLong(), any(), any());
    }

	@Test
    void shouldReturnOnlyEligibleOutstandingItems() {

        TargetPeriodYear targetPeriodYear = mock(TargetPeriodYear.class);
        when(targetPeriodYear.getTargetYear()).thenReturn(Year.of(2027));

        SectorFacilityPerformanceDataReportSearchCriteria criteria =
                new SectorFacilityPerformanceDataReportSearchCriteria();
        criteria.setTargetPeriodType(TargetPeriodType.TP7);
        criteria.setTargetPeriodReportType(PerformanceDataReportType.FINAL);
        criteria.setReportStatus(PerformanceDataFacilityTargetPeriodResultType.OUTSTANDING);
        criteria.setPaging(PagingRequest.builder().pageNumber(0).pageSize(5).build());

        when(targetPeriodService.getTargetPeriodYearsByTypeAndReportType(any(), any()))
                .thenReturn(List.of(targetPeriodYear));

        SectorFacilityPerformanceDataReportItemDTO eligible =
                SectorFacilityPerformanceDataReportItemDTO.builder()
                        .facilityBusinessId("FAC001")
                        .accountId(1L)
                        .build();

        SectorFacilityPerformanceDataReportItemDTO ineligible =
                SectorFacilityPerformanceDataReportItemDTO.builder()
                        .facilityBusinessId("FAC002")
                        .accountId(2L)
                        .build();

        SectorFacilityPerformanceDataReportListDTO outstanding =
                SectorFacilityPerformanceDataReportListDTO.builder()
                        .performanceDataReportItems(List.of(eligible, ineligible))
                        .total(2L)
                        .build();

        when(repository.getSectorFacilityPerformanceDataReportListOutstandingBySearchCriteria(
                1L, criteria, targetPeriodYear))
                .thenReturn(outstanding);

        UnderlyingAgreementContainer agreement = mock(UnderlyingAgreementContainer.class);

        when(underlyingAgreementQueryService.getUnderlyingAgreementContainersByAccounts(anySet()))
                .thenReturn(Map.of(
                        1L, agreement,
                        2L, agreement));

        PerformanceDataFacilityBaselineAndTargets baseline =
                mock(PerformanceDataFacilityBaselineAndTargets.class);

        when(referenceDataService.getFacilityOriginalBaselineAndTargets(
                anyString(), any(), any()))
                .thenReturn(baseline);

        when(validator.validateFacilityBaselineDateEligibility(any(TargetPeriodYear.class),
                any(PerformanceDataFacilityBaselineAndTargets.class)))
                .thenReturn(BusinessValidationResult.valid());

        when(validator.validateFacilityProductsEligibility(any(TargetPeriodYear.class),
                any(PerformanceDataFacilityBaselineAndTargets.class)))
                .thenReturn(BusinessValidationResult.valid())
                .thenReturn(BusinessValidationResult.invalid(null));

        SectorFacilityPerformanceDataReportListDTO result =
                service.getSectorFacilityPerformanceDataReportList(1L, criteria);

        // Assert
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getPerformanceDataReportItems()).containsExactly(eligible);

        verify(validator, times(2)).validateFacilityBaselineDateEligibility(targetPeriodYear, baseline);
        verify(validator, times(2)).validateFacilityProductsEligibility(targetPeriodYear, baseline);
        verify(referenceDataService).getFacilityOriginalBaselineAndTargets("FAC001", targetPeriodYear.getTargetYear(), agreement);
        verify(underlyingAgreementQueryService).getUnderlyingAgreementContainersByAccounts(Set.of(1L, 2L));
        verify(repository).getSectorFacilityPerformanceDataReportListOutstandingBySearchCriteria(
        		1L, criteria, targetPeriodYear);
    }
}
