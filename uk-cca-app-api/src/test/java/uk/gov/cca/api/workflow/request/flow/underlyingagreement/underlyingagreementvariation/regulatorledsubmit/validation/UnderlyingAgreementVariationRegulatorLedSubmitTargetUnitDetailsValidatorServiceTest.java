package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmitTargetUnitDetailsValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmitTargetUnitDetailsValidatorService service;

    @Mock
    private DataValidator<UnderlyingAgreementTargetUnitDetails> validator;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Mock
    private SubsectorAssociationService subsectorAssociationService;

    @Test
    void validate() {
        final Long accountId = 1L;
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .build())
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .requestResources(List.of(RequestResource.builder()
                                .resourceType(ResourceType.ACCOUNT)
                                .resourceId(accountId.toString())
                                .build())
                        )
                        .build())
                .payload(taskPayload)
                .build();

        final Long sectorAssociationId = 1L;

        when(validator.validate(targetUnitDetails)).thenReturn(Optional.empty());
        when(targetUnitAccountQueryService.getAccountSectorAssociationId(accountId)).thenReturn(sectorAssociationId);
        when(subsectorAssociationService.getSubsectorAssociationInfoDTOBySectorAssociationId(sectorAssociationId))
                .thenReturn(List.of());

        // Invoke
        BusinessValidationResult result = service.validate(requestTask);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1)).validate(targetUnitDetails);
        verify(targetUnitAccountQueryService, times(1)).getAccountSectorAssociationId(accountId);
        verify(subsectorAssociationService, times(1)).getSubsectorAssociationInfoDTOBySectorAssociationId(sectorAssociationId);
    }

    @Test
    void getPayloadType() {
        assertThat(service.getPayloadType()).isEmpty();
    }
}
