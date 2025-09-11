package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.exception.CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_TARGET_UNIT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementTargetUnitDetailsValidatorServiceTest {

    @InjectMocks
    private EditedUnderlyingAgreementTargetUnitDetailsValidatorService validatorService;

    @Mock
    private DataValidator<UnderlyingAgreementTargetUnitDetails> validator;

    @Mock
    private SubsectorAssociationService subsectorAssociationService;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Test
    void validate() {
        final Long subsectorAssociationId = 1L;
        final Long sectorAssociationId = 1L;
        final Long accountId = 1L;
        final String subsectorAssociationName = "name";
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails
                .builder().subsectorAssociationName(subsectorAssociationName)
                .subsectorAssociationId(subsectorAssociationId).build();
        final Request request = Request.builder().build();
        addResourcesToRequest(accountId, request);
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(UnderlyingAgreementRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .build())
                        .build())
                .build();
        final SubsectorAssociationInfoDTO subsectorAssociationInfoDTO = SubsectorAssociationInfoDTO.builder()
                .id(subsectorAssociationId)
                .name(subsectorAssociationName)
                .build();

        when(validator.validate(targetUnitDetails)).thenReturn(Optional.empty());
        when(targetUnitAccountQueryService.getAccountSectorAssociationId(accountId)).thenReturn(sectorAssociationId);
        when(subsectorAssociationService.getSubsectorAssociationInfoDTOBySectorAssociationId(sectorAssociationId))
                .thenReturn(Collections.singletonList(subsectorAssociationInfoDTO));

        // Invoke
        BusinessValidationResult result = validatorService.validate(requestTask);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1)).validate(targetUnitDetails);
        verify(targetUnitAccountQueryService, times(1)).getAccountSectorAssociationId(accountId);
        verify(subsectorAssociationService, times(1)).getSubsectorAssociationInfoDTOBySectorAssociationId(sectorAssociationId);
    }

    @Test
    void validate_subsector_association_list_empty() {
        final Long subsectorAssociationId = 1L;
        final Long accountId = 1L;
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails
                .builder().subsectorAssociationId(subsectorAssociationId).build();
        final Request request = Request.builder().build();
        addResourcesToRequest(accountId, request);
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(UnderlyingAgreementRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .build())
                        .build())
                .build();

        when(validator.validate(targetUnitDetails)).thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> validatorService.validate(requestTask));

        // Verify
        assertThat(businessException.getErrorCode())
                .isEqualTo(INVALID_UNDERLYING_AGREEMENT_TARGET_UNIT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION);
    }

    @Test
    void validate_subsector_association_list_not_empty() {
        final Long subsectorAssociationId = 1L;
        final Long accountId = 1L;
        final Long sectorAssociationId = 1L;
        final String subsectorAssociationName = "name";
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails
                .builder().subsectorAssociationName("subsectorAssociationName")
                .subsectorAssociationId(subsectorAssociationId).build();
        final SubsectorAssociationInfoDTO subsectorAssociationInfoDTO = SubsectorAssociationInfoDTO.builder()
                .id(subsectorAssociationId)
                .name(subsectorAssociationName)
                .build();
        final Request request = Request.builder().build();
        addResourcesToRequest(accountId, request);
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(UnderlyingAgreementRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .build())
                        .build())
                .build();

        when(validator.validate(targetUnitDetails)).thenReturn(Optional.empty());
        when(targetUnitAccountQueryService.getAccountSectorAssociationId(accountId)).thenReturn(sectorAssociationId);
        when(subsectorAssociationService.getSubsectorAssociationInfoDTOBySectorAssociationId(sectorAssociationId))
                .thenReturn(Collections.singletonList(subsectorAssociationInfoDTO));

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> validatorService.validate(requestTask));

        // Verify
        assertThat(businessException.getErrorCode())
                .isEqualTo(INVALID_UNDERLYING_AGREEMENT_TARGET_UNIT_SUB_SECTOR_ASSOCIATION_NOT_RELATED_TO_SECTOR_ASSOCIATION);
        verify(targetUnitAccountQueryService, times(1)).getAccountSectorAssociationId(accountId);
        verify(subsectorAssociationService, times(1)).getSubsectorAssociationInfoDTOBySectorAssociationId(sectorAssociationId);
    }
    
    private void addResourcesToRequest(Long accountId, Request request) {
		RequestResource accountResource = RequestResource.builder()
				.resourceType(ResourceType.ACCOUNT)
				.resourceId(accountId.toString())
				.request(request)
				.build();

        request.getRequestResources().add(accountResource);
	}
}
