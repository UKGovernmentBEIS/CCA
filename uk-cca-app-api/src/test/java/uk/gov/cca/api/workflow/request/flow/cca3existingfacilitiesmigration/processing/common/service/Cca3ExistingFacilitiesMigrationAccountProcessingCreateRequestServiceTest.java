package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3ExistingFacilitiesMigrationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingCreateRequestServiceTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingCreateRequestService service;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createRequest() {
        final Long accountId = 1L;
        final Long sectorId = 2L;
        final String parentRequestId = "parentRequestId";
        final String parentRequestBusinessKey = "bk-parentRequestId";
        final String defaultSignatory = "regulator";

        final String accountBusinessId = "accountBusinessId";
        final List<Cca3FacilityMigrationData> facilityMigrationDataList = List.of(
                Cca3FacilityMigrationData.builder().participatingInCca3Scheme(false).build(),
                Cca3FacilityMigrationData.builder().participatingInCca3Scheme(true).calculatorFileUuid("3e7ef542-ff79-4cef-a751-a304707be663").calculatorFileName("filename").build()
        );
        final Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder()
                .accountId(accountId)
                .accountBusinessId(accountBusinessId)
                .facilityMigrationDataList(facilityMigrationDataList)
                .cca3Participating(true)
                .build();
        final Request request = Request.builder()
                .id(parentRequestId)
                .payload(Cca3ExistingFacilitiesMigrationRunRequestPayload.builder()
                        .defaultSignatory(defaultSignatory)
                        .accountStates(Map.of(accountId, accountState))
                        .build())
                .build();
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();
        Map<UUID, String> underlyingAgreementAttachments = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        underlyingAgreementAttachments.put(uuid, "una");
        final UnderlyingAgreementContainer underlyingAgreementContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .underlyingAgreementAttachments(underlyingAgreementAttachments)
                .build();
        final SectorAssociationDetails sectorAssociationDetails = SectorAssociationDetails.builder()
                .subsectorAssociationName("subsectorAssociationName")
                .build();
        final AccountReferenceData accountReferenceData = AccountReferenceData.builder()
                .targetUnitAccountDetails(TargetUnitAccountDetails.builder().sectorAssociationId(sectorId).build())
                .sectorAssociationDetails(sectorAssociationDetails)
                .build();
        final Map<String, String> requestResources = Map.of(
                ResourceType.ACCOUNT, accountId.toString(),
                CcaResourceType.SECTOR_ASSOCIATION, "2"
        );

        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING)
                .requestResources(requestResources)
                .requestMetadata(Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING)
                        .parentRequestId(parentRequestId)
                        .accountBusinessId(accountBusinessId)
                        .cca3Participating(true)
                        .build())
                .requestPayload(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_PAYLOAD)
                        .sectorAssociationId(sectorId)
                        .defaultSignatory(defaultSignatory)
                        .accountReferenceData(accountReferenceData)
                        .underlyingAgreement(underlyingAgreement)
                        .facilityMigrationDataList(facilityMigrationDataList)
                        .underlyingAgreementAttachments(Map.of(uuid, "una", UUID.fromString("3e7ef542-ff79-4cef-a751-a304707be663"), "filename"))
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountId,
                        CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_RUN_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE, accountState
                ))
                .build();

        when(requestService.findRequestById(parentRequestId)).thenReturn(request);
        when(underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId))
                .thenReturn(underlyingAgreementContainer);
        when(accountReferenceDetailsService.getAccountReferenceData(accountId))
                .thenReturn(accountReferenceData);
        when(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .thenReturn(requestResources);

        // Invoke
        service.createRequest(accountId, parentRequestId, parentRequestBusinessKey);

        // Verify
        verify(requestService, times(1)).findRequestById(parentRequestId);
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementContainerByAccountId(accountId);
        verify(accountReferenceDetailsService, times(1))
                .getAccountReferenceData(accountId);
        verify(requestCreateAccountAndSectorResourcesService, times(1))
                .createRequestResources(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}
