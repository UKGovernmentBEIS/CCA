package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3ExistingFacilitiesMigrationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3ExistingFacilitiesMigrationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation.Cca3ExistingFacilitiesMigrationAccountProcessingCreateValidator;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation.Cca3ExistingFacilitiesMigrationRunValidator;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation.Cca3ExistingFacilitiesMigrationViolation;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationCreateRunServiceTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationCreateRunService service;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private Cca3ExistingFacilitiesMigrationParseCsvService cca3ExistingFacilitiesMigrationParseCsvService;

    @Mock
    private RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    @Mock
    private Cca3ExistingFacilitiesMigrationRunValidator cca3ExistingFacilitiesMigrationRunValidator;

    @Mock
    private Cca3ExistingFacilitiesMigrationAccountProcessingCreateValidator cca3ExistingFacilitiesMigrationAccountProcessingCreateValidator;

    @Test
    void createCca3ExistingFacilitiesMigrationRun() {
        final String account1 = "account1";
        final String account2 = "account2";
        final String csvFile = "csvFile";
        final Cca3FacilityMigrationData facility1 = Cca3FacilityMigrationData.builder()
                .accountBusinessId(account1)
                .participatingInCca3Scheme(true)
                .build();
        final Cca3FacilityMigrationData facility2 = Cca3FacilityMigrationData.builder()
                .accountBusinessId(account1)
                .participatingInCca3Scheme(true)
                .build();
        final Cca3FacilityMigrationData facility3 = Cca3FacilityMigrationData.builder()
                .accountBusinessId(account2)
                .participatingInCca3Scheme(true)
                .build();
        final List<TargetUnitAccountBusinessInfoDTO> liveAccounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId(account1).build(),
                TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).businessId(account2).build()
        );

        Request request = Request.builder().build();
        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN)
                .requestResources(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()))
                .requestPayload(Cca3ExistingFacilitiesMigrationRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN_REQUEST_PAYLOAD)
                        .defaultSignatory("regulator1")
                        .accountStates(Map.of(
                                1L, Cca3FacilityMigrationAccountState.builder()
                                        .accountId(1L)
                                        .accountBusinessId(account1)
                                        .facilityMigrationDataList(List.of(facility1, facility2))
                                        .succeeded(false)
                                        .cca3Participating(true)
                                        .errors(new ArrayList<>())
                                        .build(),
                                2L, Cca3FacilityMigrationAccountState.builder()
                                        .accountId(2L)
                                        .accountBusinessId(account2)
                                        .facilityMigrationDataList(List.of(facility3))
                                        .succeeded(false)
                                        .cca3Participating(true)
                                        .errors(List.of(Cca3ExistingFacilitiesMigrationViolation.Cca3ExistingFacilitiesMigrationViolationMessage.ACCOUNT_NOT_ELIGIBLE_FOR_MIGRATION.getMessage()))
                                        .build()
                        ))
                        .csvSourceFile(csvFile)
                        .build())
                .requestMetadata(Cca3ExistingFacilitiesMigrationRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN)
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, Set.of(1L),
                        CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        when(cca3ExistingFacilitiesMigrationParseCsvService.submitAndGetSourceFile())
                .thenReturn(csvFile);
        when(cca3ExistingFacilitiesMigrationParseCsvService.parseSourceFile(csvFile, new ArrayList<>()))
                .thenReturn(List.of(facility1, facility2, facility3));
        when(targetUnitAccountQueryService.getActiveAccountsByBusinessIds(Set.of(account1, account2)))
                .thenReturn(liveAccounts);
        when(cca3ExistingFacilitiesMigrationAccountProcessingCreateValidator.validateAction(1L))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(cca3ExistingFacilitiesMigrationAccountProcessingCreateValidator.validateAction(2L))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());
        when(regulatorAuthorityResourceService.findUsersByCompetentAuthority(CompetentAuthorityEnum.ENGLAND))
                .thenReturn(List.of("regulator1", "regulator2"));
        when(startProcessRequestService.startProcess(any()))
                .thenReturn(request);

        // Invoke
        service.createCca3ExistingFacilitiesMigrationRun();

        // Verify
        assertThat(request.getSubmissionDate()).isNotNull();
        verify(cca3ExistingFacilitiesMigrationParseCsvService, times(1))
                .submitAndGetSourceFile();
        verify(cca3ExistingFacilitiesMigrationRunValidator, times(1))
                .validateCreation();
        verify(cca3ExistingFacilitiesMigrationParseCsvService, times(1))
                .parseSourceFile(csvFile, new ArrayList<>());
        verify(targetUnitAccountQueryService, times(1))
                .getActiveAccountsByBusinessIds(Set.of(account1, account2));
        verify(cca3ExistingFacilitiesMigrationRunValidator, times(1))
                .validate(anyMap(), eq(liveAccounts), anyList());
        verify(cca3ExistingFacilitiesMigrationAccountProcessingCreateValidator, times(1))
                .validateAction(1L);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingCreateValidator, times(1))
                .validateAction(2L);
        verify(regulatorAuthorityResourceService, times(1))
                .findUsersByCompetentAuthority(CompetentAuthorityEnum.ENGLAND);
        verify(startProcessRequestService, times(1))
                .startProcess(requestParams);
    }
}
