package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.handler;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestCACreateActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;

@Component
@RequiredArgsConstructor
public class SubsistenceFeesRunCreateActionHandler implements RequestCACreateActionHandler<RequestCreateActionEmptyPayload>{

	private final StartProcessRequestService startProcessRequestService;
	private final SectorAssociationQueryService sectorAssociationQueryService;
	private final AccountReferenceDetailsService accountReferenceDetailsService;
	private final SectorReferenceDetailsService sectorReferenceDetailsService;
	private final SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;
    private final ChargingPeriodService chargingPeriodService;

    @Override
	public String process(CompetentAuthorityEnum ca, RequestCreateActionEmptyPayload payload, AppUser appUser) {

        final Year chargingYear = chargingPeriodService.getChargingYear(LocalDate.now());

		// Get all sectors for regulator/CA
		List<Long> sectorIds = sectorAssociationQueryService.getRegulatorSectorAssociations(appUser).stream()
				.map(SectorAssociationInfoDTO::getId)
				.collect(Collectors.toList());
		// Get eligible accounts
		Set<Long> accountIds = subsistenceFeesRunQueryService.getTargetAccountIdsForSubsistenceFeesRun(chargingYear);

		Map<Long, SectorAssociationInfo> sectorsDetails =
				sectorReferenceDetailsService.getSectorAssociationsInfo(sectorIds).stream()
				.collect(Collectors.toMap(SectorAssociationInfo::getId, Function.identity()));
		Map<TargetUnitAccountDTO, SectorAssociationInfo> accountsDetails = accountReferenceDetailsService
				.getTargetUnitAccountsAndSectorsDetails(new ArrayList<>(accountIds));

	   // Create process for subsistence fees run
       CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.SUBSISTENCE_FEES_RUN)
                .requestResources(Map.of(ResourceType.CA, appUser.getCompetentAuthority().name()))
                .requestPayload(SubsistenceFeesRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.SUBSISTENCE_FEES_RUN_REQUEST_PAYLOAD)
                        .submitterId(appUser.getUserId())
                        .build())
                .requestMetadata(SubsistenceFeesRunRequestMetadata.builder()
						.type(CcaRequestMetadataType.SUBSISTENCE_FEES_RUN)
						.chargingYear(chargingYear)
						.sectorsReports(initializeSectorsReports(sectorsDetails))
						.accountsReports(initializeAccountsReports(accountsDetails))
						.build())
                .processVars(Map.of(
                		CcaBpmnProcessConstants.SECTOR_IDS, new HashSet<>(sectorIds),
                		CcaBpmnProcessConstants.ACCOUNT_IDS, accountIds,
						CcaBpmnProcessConstants.NUMBER_OF_SECTORS_COMPLETED, 0,
                		CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
	}

	private Map<Long, MoaReport> initializeSectorsReports(Map<Long, SectorAssociationInfo> sectorsDetails) {
		return sectorsDetails.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> MoaReport.builder()
						.moaType(MoaType.SECTOR_MOA)
						.sectorAcronym(entry.getValue().getAcronym())
						.sectorName(entry.getValue().getName())
						.build()));
	}

	private Map<Long, MoaReport> initializeAccountsReports(Map<TargetUnitAccountDTO, SectorAssociationInfo> accountsDetails) {
		return accountsDetails.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getKey().getId(), entry -> MoaReport.builder()
						.moaType(MoaType.TARGET_UNIT_MOA)
						.sectorAcronym(entry.getValue().getAcronym())
						.sectorName(entry.getValue().getName())
						.businessId(entry.getKey().getBusinessId())
						.operatorName(entry.getKey().getName())
						.build()));
	}

	@Override
	public String getRequestType() {
		return CcaRequestType.SUBSISTENCE_FEES_RUN;
	}

}
