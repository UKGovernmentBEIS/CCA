package uk.gov.cca.api.underlyingagreement.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementValidatorService underlyingAgreementValidatorService;

    @Spy
    private ArrayList<UnderlyingAgreementSectionContextValidator> underlyingAgreementSectionContextValidators;

    @Mock
    private UnderlyingAgreementTargetPeriod6ContextValidatorService underlyingAgreementTargetPeriod6ContextValidatorService;
    
    @Mock
    private UnderlyingAgreementFacilitiesContextValidatorService underlyingAgreementFacilitiesContextValidatorService;

    @BeforeEach
    void setUp() {
        underlyingAgreementSectionContextValidators.add(underlyingAgreementTargetPeriod6ContextValidatorService);
    }

    @Test
    void validate() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder().build();

        when(underlyingAgreementTargetPeriod6ContextValidatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3)))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        underlyingAgreementValidatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        verify(underlyingAgreementTargetPeriod6ContextValidatorService, times(1)).validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));
    }

    @Test
    void validate_no_valid() {
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder().build();

        when(underlyingAgreementTargetPeriod6ContextValidatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3)))
                .thenReturn(BusinessValidationResult.invalid(List.of()));

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                underlyingAgreementValidatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3)));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT);
        verify(underlyingAgreementTargetPeriod6ContextValidatorService, times(1)).validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));
    }
    
    @Test
    void getValidationResultsExceptFacilities() {
    	underlyingAgreementSectionContextValidators.add(underlyingAgreementFacilitiesContextValidatorService);
    	
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder().build();

        when(underlyingAgreementTargetPeriod6ContextValidatorService.validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3)))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        underlyingAgreementValidatorService.getValidationResultsExceptFacilities(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));

        // Verify
        verify(underlyingAgreementFacilitiesContextValidatorService, never()).validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));
        verify(underlyingAgreementTargetPeriod6ContextValidatorService, times(1)).validate(container, new UnderlyingAgreementValidationContext(SchemeVersion.CCA_3));
    }
}
