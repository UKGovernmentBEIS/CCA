package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.service.ChargingPeriodService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.core.service.SectorReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaReport;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunCreateActionHandlerTest {

	@InjectMocks
    private SubsistenceFeesRunCreateActionHandler service;

    @Mock
    private SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Mock
	private AccountReferenceDetailsService accountReferenceDetailsService;

	@Mock
	private SectorReferenceDetailsService sectorReferenceDetailsService;

	@Mock
	private ChargingPeriodService chargingPeriodService;

    @Test
    void getType() {
    	assertThat(service.getRequestType()).isEqualTo(CcaRequestType.SUBSISTENCE_FEES_RUN);
    }

    @Test
    void process() {
        LocalDate currentDate = LocalDate.now();
        Year chargingYear = Year.now();
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
    	RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
    	AppUser appUser = AppUser.builder()
    			.userId("userId")
    			.firstName("fn").lastName("ln")
    			.authorities(List.of(AppAuthority.builder().competentAuthority(ca).build()))
    			.build();

    	SectorAssociationInfoDTO sectorAssociationInfoDTO = SectorAssociationInfoDTO.builder().id(10L).build();
    	List<SectorAssociationInfo> sectorsDetails = List.of(SectorAssociationInfo.builder()
    			.id(10L)
    			.acronym("ADS")
    			.name("sector name")
    			.build());
    	Map<TargetUnitAccountDTO, SectorAssociationInfo> accountsDetails = Map.of(
    			TargetUnitAccountDTO.builder().id(1L).businessId("businessId").name("operator name").build(),
    			SectorAssociationInfo.builder().build()
    			);

    	CcaRequestParams requestParams = CcaRequestParams.builder()
    			.type(CcaRequestType.SUBSISTENCE_FEES_RUN)
    			.requestResources(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()))
    			.requestPayload(SubsistenceFeesRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.SUBSISTENCE_FEES_RUN_REQUEST_PAYLOAD)
                        .submitterId(appUser.getUserId())
                        .build())
                .requestMetadata(SubsistenceFeesRunRequestMetadata.builder()
                		.sectorsReports(Map.of(
                				10L, MoaReport.builder()
                				.moaType(MoaType.SECTOR_MOA)
                				.sectorAcronym("ADS")
                				.sectorName("sector name")
                				.build()
                				))
                		.accountsReports(Map.of(
                				1L, MoaReport.builder()
                				.moaType(MoaType.TARGET_UNIT_MOA)
                				.businessId("businessId")
                				.operatorName("operator name")
                				.build()
                				))
                		.type(CcaRequestMetadataType.SUBSISTENCE_FEES_RUN)
						.chargingYear(chargingYear)
						.build())
                .processVars(Map.of(CcaBpmnProcessConstants.SECTOR_IDS, Set.of(10L),
						CcaBpmnProcessConstants.NUMBER_OF_SECTORS_COMPLETED, 0,
                		CcaBpmnProcessConstants.ACCOUNT_IDS, Set.of(1L),
						CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        when(chargingPeriodService.getChargingYear(currentDate)).thenReturn(chargingYear);
    	when(sectorAssociationQueryService.getRegulatorSectorAssociations(appUser)).thenReturn(List.of(sectorAssociationInfoDTO));
    	when(subsistenceFeesRunQueryService.getTargetAccountIdsForSubsistenceFeesRun(chargingYear)).thenReturn(Set.of(1L));
    	when(sectorReferenceDetailsService.getSectorAssociationsInfo(List.of(10L))).thenReturn(sectorsDetails);
    	when(accountReferenceDetailsService.getTargetUnitAccountsAndSectorsDetails(List.of(1L))).thenReturn(accountsDetails);
    	when(startProcessRequestService.startProcess(requestParams)).thenReturn(Request.builder().id("1").build());

        String result = service.process(ca, payload, appUser);

        assertThat(result).isEqualTo("1");
        verify(chargingPeriodService, times(1)).getChargingYear(currentDate);
        verify(sectorAssociationQueryService, times(1)).getRegulatorSectorAssociations(appUser);
        verify(subsistenceFeesRunQueryService, times(1)).getTargetAccountIdsForSubsistenceFeesRun(chargingYear);
        verify(sectorReferenceDetailsService, times(1)).getSectorAssociationsInfo(List.of(10L));
        verify(accountReferenceDetailsService, times(1)).getTargetUnitAccountsAndSectorsDetails(List.of(1L));
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}
