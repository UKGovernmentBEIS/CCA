package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestCreateValidatorService;
import uk.gov.cca.api.workflow.request.flow.common.validation.performancedata.PerformanceDataCreateSchemeValidator;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataDownloadCreateValidatorTest {

    @InjectMocks
    private PerformanceDataDownloadCreateValidator validator;

    @Mock
    private PerformanceDataCreateSchemeValidator performanceDataCreateSchemeValidator;

    @Mock
    private CcaRequestCreateValidatorService ccaRequestCreateValidatorService;

    @Test
    void validateAction() {
        final Long sectorId = 1L;

        when(performanceDataCreateSchemeValidator.isAvailableForScheme(eq(SchemeVersion.CCA_2), any()))
                .thenReturn(true);
        when(ccaRequestCreateValidatorService
                .validate(sectorId, CcaResourceType.SECTOR_ASSOCIATION, Set.of(CcaRequestType.PERFORMANCE_DATA_DOWNLOAD)))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(sectorId);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(performanceDataCreateSchemeValidator, times(1))
                .isAvailableForScheme(eq(SchemeVersion.CCA_2), any());
        verify(ccaRequestCreateValidatorService, times(1))
                .validate(sectorId, CcaResourceType.SECTOR_ASSOCIATION, Set.of(CcaRequestType.PERFORMANCE_DATA_DOWNLOAD));
    }

    @Test
    void validateAction_not_available() {
        final Long sectorId = 1L;

        when(performanceDataCreateSchemeValidator.isAvailableForScheme(eq(SchemeVersion.CCA_2), any()))
                .thenReturn(false);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(sectorId);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().isAvailable(false).build());
        verify(performanceDataCreateSchemeValidator, times(1))
                .isAvailableForScheme(eq(SchemeVersion.CCA_2), any());
        verifyNoInteractions(ccaRequestCreateValidatorService);
    }

    @Test
    void getMutuallyExclusiveRequests() {
        assertThat(validator.getMutuallyExclusiveRequests())
                .containsExactly(CcaRequestType.PERFORMANCE_DATA_DOWNLOAD);
    }

    @Test
    void getRequestType() {
        assertThat(validator.getRequestType())
                .isEqualTo(CcaRequestType.PERFORMANCE_DATA_DOWNLOAD);
    }
}
