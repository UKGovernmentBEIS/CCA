package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestCreateValidatorService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataUploadCreateValidatorTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataUploadCreateValidator validator;

    @Mock
    private CcaRequestCreateValidatorService ccaRequestCreateValidatorService;

    @Mock
    private FacilityPerformanceAccountTemplateDataUploadValidator facilityPerformanceAccountTemplateDataUploadValidator;

    @Test
    void validateAction() {
        final Long sectorId = 1L;

        when(facilityPerformanceAccountTemplateDataUploadValidator.isAvailable()).thenReturn(true);
        when(ccaRequestCreateValidatorService
                .validate(sectorId, CcaResourceType.SECTOR_ASSOCIATION, Set.of(CcaRequestType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD)))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(sectorId);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(facilityPerformanceAccountTemplateDataUploadValidator, times(1)).isAvailable();
        verify(ccaRequestCreateValidatorService, times(1))
                .validate(sectorId, CcaResourceType.SECTOR_ASSOCIATION, Set.of(CcaRequestType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD));
    }

    @Test
    void validateAction_not_available() {
        final Long sectorId = 1L;

        when(facilityPerformanceAccountTemplateDataUploadValidator.isAvailable()).thenReturn(false);

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(sectorId);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).isAvailable(false).build());
        verify(facilityPerformanceAccountTemplateDataUploadValidator, times(1)).isAvailable();
        verifyNoInteractions(ccaRequestCreateValidatorService);
    }

    @Test
    void getRequestType() {
        assertThat(validator.getRequestType())
                .isEqualTo(CcaRequestType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD);
    }
}
