package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestCreateActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestCreateValidatorService;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.config.EmissionsTradingSchemeDataConfig;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestCreateActionPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataCreateValidatorTest {

    @InjectMocks
    private EmissionsTradingSchemeDataCreateValidator validator;

    @Mock
    private CcaRequestCreateValidatorService ccaRequestCreateValidatorService;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private EmissionsTradingSchemeDataConfig emissionsTradingSchemeDataConfig;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Test
    void validate_available() {
        final Long facilityId = 1L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final LocalDate submissionDate = LocalDate.of(2027, 1, 1);
        final List<TargetPeriodDetailsDTO> targetPeriods = getAvailableTargetPeriods(schemeVersion);
        final RequestCreateValidationResult expected = RequestCreateValidationResult.builder().available(true).valid(true).build();
        final FacilityDTO facilityDTO = FacilityDTO.builder()
                .id(1L)
                .facilityBusinessId("businessId")
                .siteName("siteName")
                .createdDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .closedDate(null)
                .participatingSchemeVersions(Set.of(schemeVersion))
                .build();

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion)).thenReturn(targetPeriods);
        when(emissionsTradingSchemeDataConfig.getSubmissionDate()).thenReturn(submissionDate);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facilityDTO);
        when(ccaRequestCreateValidatorService.validate(facilityId, CcaResourceType.FACILITY, Set.of(CcaRequestType.EMISSIONS_TRADING_SCHEME_DATA)))
                .thenReturn(expected);

        // invoke
        RequestCreateValidationResult actual = validator.validateAction(facilityId);

        // verify
        assertThat(actual).isEqualTo(expected);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion);
        verify(emissionsTradingSchemeDataConfig, times(1)).getSubmissionDate();
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(ccaRequestCreateValidatorService, times(1))
                .validate(facilityId, CcaResourceType.FACILITY, Set.of(CcaRequestType.EMISSIONS_TRADING_SCHEME_DATA));
    }

    @Test
    void validate_not_available() {
        final Long facilityId = 1L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final List<TargetPeriodDetailsDTO> targetPeriods = getAvailableTargetPeriods(schemeVersion);
        final LocalDate submissionDate = LocalDate.of(2025, 1, 1);
        final RequestCreateValidationResult expected = RequestCreateValidationResult.builder().available(false).valid(true).build();

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion)).thenReturn(targetPeriods);
        when(emissionsTradingSchemeDataConfig.getSubmissionDate()).thenReturn(submissionDate);

        // invoke
        RequestCreateValidationResult actual = validator.validateAction(facilityId);

        // verify
        assertThat(actual).isEqualTo(expected);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion);
        verify(emissionsTradingSchemeDataConfig, times(1)).getSubmissionDate();
        verifyNoInteractions(facilityDataQueryService, ccaRequestCreateValidatorService);
    }

    @Test
    void validate_not_available_for_CC2_facility() {
        final Long facilityId = 1L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final LocalDate submissionDate = LocalDate.of(2027, 1, 1);
        final List<TargetPeriodDetailsDTO> targetPeriods = getAvailableTargetPeriods(schemeVersion);
        final RequestCreateValidationResult expected = RequestCreateValidationResult.builder().available(false).valid(true).build();
        final FacilityDTO facilityDTO = FacilityDTO.builder()
                .id(1L)
                .facilityBusinessId("businessId")
                .siteName("siteName")
                .createdDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .closedDate(null)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .build();

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion)).thenReturn(targetPeriods);
        when(emissionsTradingSchemeDataConfig.getSubmissionDate()).thenReturn(submissionDate);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facilityDTO);

        // invoke
        RequestCreateValidationResult actual = validator.validateAction(facilityId);

        // verify
        assertThat(actual).isEqualTo(expected);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion);
        verify(emissionsTradingSchemeDataConfig, times(1)).getSubmissionDate();
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verifyNoInteractions(ccaRequestCreateValidatorService);
    }

    @Test
    void validate_in_progress() {
        final Long facilityId = 1L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final List<TargetPeriodDetailsDTO> targetPeriods = getAvailableTargetPeriods(schemeVersion);
        final LocalDate submissionDate = LocalDate.of(2027, 1, 1);
        final FacilityDTO facilityDTO = FacilityDTO.builder()
                .id(1L)
                .facilityBusinessId("businessId")
                .siteName("siteName")
                .createdDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .closedDate(null)
                .participatingSchemeVersions(Set.of(schemeVersion))
                .build();
        final RequestCreateValidationResult invalidResult = RequestCreateValidationResult.builder().valid(false).build();

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion)).thenReturn(targetPeriods);
        when(emissionsTradingSchemeDataConfig.getSubmissionDate()).thenReturn(submissionDate);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facilityDTO);
        when(ccaRequestCreateValidatorService.validate(facilityId, CcaResourceType.FACILITY, Set.of(CcaRequestType.EMISSIONS_TRADING_SCHEME_DATA)))
                .thenReturn(invalidResult);

        // invoke
        RequestCreateValidationResult actual = validator.validateAction(facilityId);

        // verify
        assertThat(actual).isEqualTo(invalidResult);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion);
        verify(emissionsTradingSchemeDataConfig, times(1)).getSubmissionDate();
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(ccaRequestCreateValidatorService, times(1))
                .validate(facilityId, CcaResourceType.FACILITY, Set.of(CcaRequestType.EMISSIONS_TRADING_SCHEME_DATA));
    }

    @Test
    void validate_start_process_request() {
        final Long facilityId = 1L;
        final Year targetYear = Year.of(2026);
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final List<TargetPeriodDetailsDTO> targetPeriods = getAvailableTargetPeriods(schemeVersion);

        final EmissionsTradingSchemeDataRequestCreateActionPayload actionPayload = EmissionsTradingSchemeDataRequestCreateActionPayload.builder()
                .targetYear(targetYear)
                .payloadType(CcaRequestCreateActionPayloadType.EMISSIONS_TRADING_SCHEME_DATA_CREATE_ACTION_PAYLOAD)
                .build();

        final FacilityDTO facilityDTO = FacilityDTO.builder()
                .id(1L)
                .facilityBusinessId("businessId")
                .siteName("siteName")
                .createdDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .closedDate(null)
                .build();

        final RequestCreateValidationResult expected = RequestCreateValidationResult.builder().available(true).valid(true).build();

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion)).thenReturn(targetPeriods);
        when(emissionsTradingSchemeDataConfig.getSubmissionDate()).thenReturn(LocalDate.of(2027, 1, 1));
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facilityDTO);

        // invoke
        RequestCreateValidationResult actual = validator.validateAction(facilityId, actionPayload);

        // verify
        assertThat(actual).isEqualTo(expected);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
    }

    @Test
    void validate_start_process_request_with_facility_exception() {
        final Long facilityId = 1L;
        final Year targetYear = Year.of(2026);
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final LocalDate submissionDate = LocalDate.of(2027, 1, 1);
        final EmissionsTradingSchemeDataRequestCreateActionPayload actionPayload = EmissionsTradingSchemeDataRequestCreateActionPayload.builder()
                .targetYear(targetYear)
                .payloadType(CcaRequestCreateActionPayloadType.EMISSIONS_TRADING_SCHEME_DATA_CREATE_ACTION_PAYLOAD)
                .build();
        final FacilityDTO facilityDTO = FacilityDTO.builder()
                .id(1L)
                .facilityBusinessId("businessId")
                .siteName("siteName")
                .createdDate(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .closedDate(LocalDate.of(2020, 1, 1))
                .build();
        final List<TargetPeriodDetailsDTO> targetPeriods = getAvailableTargetPeriods(schemeVersion);

        when(emissionsTradingSchemeDataConfig.getSubmissionDate()).thenReturn(submissionDate);
        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion)).thenReturn(targetPeriods);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facilityDTO);

        // invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateAction(facilityId, actionPayload));

        // verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.EMISSIONS_TRADING_SCHEME_DATA_FACILITY_NOT_ELIGIBLE);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion);
        verify(emissionsTradingSchemeDataConfig, times(1)).getSubmissionDate();
    }

    @Test
    void validate_start_process_request_with_target_year_exception() {
        final Long facilityId = 1L;
        final Year targetYear = Year.of(2030);
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final List<TargetPeriodDetailsDTO> targetPeriods = getAvailableTargetPeriods(schemeVersion);
        final EmissionsTradingSchemeDataRequestCreateActionPayload actionPayload = EmissionsTradingSchemeDataRequestCreateActionPayload.builder()
                .targetYear(targetYear)
                .payloadType(CcaRequestCreateActionPayloadType.EMISSIONS_TRADING_SCHEME_DATA_CREATE_ACTION_PAYLOAD)
                .build();

        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion)).thenReturn(targetPeriods);

        // invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateAction(facilityId, actionPayload));

        // verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.EMISSIONS_TRADING_SCHEME_DATA_TARGET_YEAR_NOT_ELIGIBLE);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion);
        verify(facilityDataQueryService, never()).getFacilityInfoData(facilityId);
    }

    @Test
    void validate_start_process_request_with_submission_date_exception() {
        final Long facilityId = 1L;
        final Year targetYear = Year.of(2026);
        final LocalDate submissionDate = LocalDate.of(2025, 1, 1);
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final List<TargetPeriodDetailsDTO> targetPeriods = getAvailableTargetPeriods(schemeVersion);
        final EmissionsTradingSchemeDataRequestCreateActionPayload actionPayload = EmissionsTradingSchemeDataRequestCreateActionPayload.builder()
                .targetYear(targetYear)
                .payloadType(CcaRequestCreateActionPayloadType.EMISSIONS_TRADING_SCHEME_DATA_CREATE_ACTION_PAYLOAD)
                .build();

        when(emissionsTradingSchemeDataConfig.getSubmissionDate()).thenReturn(submissionDate);
        when(targetPeriodService.getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion)).thenReturn(targetPeriods);

        // invoke
        // invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateAction(facilityId, actionPayload));

        // verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.EMISSIONS_TRADING_SCHEME_DATA_SUBMISSION_DATE_NOT_VALID);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(schemeVersion);
        verify(facilityDataQueryService, never()).getFacilityInfoData(facilityId);
    }

    private List<TargetPeriodDetailsDTO> getAvailableTargetPeriods(final SchemeVersion schemeVersion) {
        return List.of(
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP7)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2026))
                                                .reportingStartDate(LocalDate.of(2025, 1, 1))
                                                .reportingEndDate(LocalDate.of(2125, 1, 2))
                                                .build()
                                ))
                                .build()
                        )
                        .schemeVersion(schemeVersion)
                        .build(),
                TargetPeriodDetailsDTO.builder()
                        .businessId(TargetPeriodType.TP8)
                        .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                                .targetPeriodYears(List.of(
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2027))
                                                .reportingStartDate(LocalDate.of(2026, 1, 1))
                                                .reportingEndDate(LocalDate.of(2126, 1, 1))
                                                .build(),
                                        TargetPeriodYear.builder()
                                                .targetYear(Year.of(2028))
                                                .reportingStartDate(LocalDate.of(2127, 1, 1))
                                                .build()
                                ))
                                .build()
                        )
                        .schemeVersion(schemeVersion)
                        .build()
        );
    }
}
