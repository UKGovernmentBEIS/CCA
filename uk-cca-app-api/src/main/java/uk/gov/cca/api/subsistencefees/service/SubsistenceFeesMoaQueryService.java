package uk.gov.cca.api.subsistencefees.service;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SubsistenceFeesMoaAuthorityInfoProvider;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoNameDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaDetails;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaRepository;
import uk.gov.cca.api.subsistencefees.transform.SubsistenceFeesMapper;
import uk.gov.netz.api.account.domain.dto.AccountInfoDTO;
import uk.gov.netz.api.account.service.AccountQueryService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesMoaQueryService implements SubsistenceFeesMoaAuthorityInfoProvider {
	
	private final SectorAssociationQueryService sectorAssociationQueryService;
	private final AccountQueryService accountQueryService;
	private final FileDocumentService fileDocumentService;
	private final SubsistenceFeesMoaRepository subsistenceFeesMoaRepository;
	private final SubsistenceFeesMapper SUBSISTENCE_FEES_MAPPER = Mappers.getMapper(SubsistenceFeesMapper.class);
	
	public SubsistenceFeesMoaSearchResults getSubsistenceFeesRunMoas(Long runId, SubsistenceFeesMoaSearchCriteria criteria) {

		SubsistenceFeesMoaSearchResultsInfo resultsInfo = subsistenceFeesMoaRepository.findBySearchCriteriaForCAView(runId, criteria);
		return toSubsistenceFeesMoaSearchResults(resultsInfo);
	}
	
	public SubsistenceFeesMoaSearchResults getSectorSubsistenceFeesMoas(Long sectorAssociationId, SubsistenceFeesMoaSearchCriteria criteria) {

		SubsistenceFeesMoaSearchResultsInfo resultsInfo = subsistenceFeesMoaRepository.findBySearchCriteriaForSectorAssociationView(sectorAssociationId, criteria);
		return toSubsistenceFeesMoaSearchResults(resultsInfo);
	}
	
	private SubsistenceFeesMoaSearchResults toSubsistenceFeesMoaSearchResults(
			SubsistenceFeesMoaSearchResultsInfo resultsInfo) {
		List<SubsistenceFeesMoaSearchResultInfoDTO> resultInfoDTOs = resultsInfo.getSubsistenceFeesMoaSearchResultInfo()
				.stream()
				.map(SUBSISTENCE_FEES_MAPPER::toSubsistenceFeesMoaSearchResultInfoDTO)
				.collect(Collectors.toList());
		
		return SubsistenceFeesMoaSearchResults.builder()
				.subsistenceFeesMoas(resultInfoDTOs)
				.total(resultsInfo.getTotal())
				.build();
	}

	@Transactional(readOnly = true)
	public SubsistenceFeesMoaDetailsDTO getSubsistenceFeesMoaDetailsById(Long moaId) {
		// Get MoA details
		SubsistenceFeesMoaDetails moaDetails = subsistenceFeesMoaRepository.getMoaDetailsById(moaId)
				.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
		FileInfoDTO fileInfoDTO = fileDocumentService.getFileInfoDTO(moaDetails.getDocumentUuid());
		Long resourceId = moaDetails.getResourceId();
		
		// Get details from sector or account
		if (MoaType.SECTOR_MOA.equals(moaDetails.getMoaType())) {
			SectorAssociationInfoNameDTO sectorInfoDTO = sectorAssociationQueryService.getSectorAssociationInfoNameDTO(resourceId);
			return SUBSISTENCE_FEES_MAPPER.toSubsistenceFeesMoaDetailsDTO(
					moaDetails, sectorInfoDTO.getAcronym(), sectorInfoDTO.getName(), fileInfoDTO);
		} else {
			AccountInfoDTO accountInfoDTO = accountQueryService.getAccountInfoDTOById(resourceId);
			return SUBSISTENCE_FEES_MAPPER.toSubsistenceFeesMoaDetailsDTO(
					moaDetails, accountInfoDTO.getBusinessId(), accountInfoDTO.getName(), fileInfoDTO);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public Pair<String, Long> getSubsistenceFeesMoaResourceIdById(Long moaId) {
		SubsistenceFeesMoa moa = getSubsistenceFeesMoaById(moaId);
		return Pair.of(moa.getMoaType().name(), moa.getResourceId());
	}
	
	SubsistenceFeesMoa getSubsistenceFeesMoaById(Long moaId) {
        return subsistenceFeesMoaRepository.findById(moaId)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

	public SubsistenceFeesMoa getSubsistenceFeesMoaByIdAndFileDocumentUuid(Long moaId, String fileDocumentUuid) {
		return subsistenceFeesMoaRepository.findByIdAndFileDocumentUuid(moaId, fileDocumentUuid)
				.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));	
	}

}
