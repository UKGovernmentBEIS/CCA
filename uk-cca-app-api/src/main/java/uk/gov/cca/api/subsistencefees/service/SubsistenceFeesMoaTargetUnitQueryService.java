package uk.gov.cca.api.subsistencefees.service;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.SubsistenceFeesMoaTargetUnitAuthorityInfoProvider;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaTargetUnit;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaTargetUnitSearchResultsInfo;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaTargetUnitRepository;
import uk.gov.cca.api.subsistencefees.transform.SubsistenceFeesMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitSearchResults;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesMoaTargetUnitQueryService implements SubsistenceFeesMoaTargetUnitAuthorityInfoProvider {
	
	private final SubsistenceFeesMoaTargetUnitRepository subsistenceFeesMoaTargetUnitRepository;
	private static final SubsistenceFeesMapper SUBSISTENCE_FEES_MAPPER = Mappers.getMapper(SubsistenceFeesMapper.class);
	
	public SubsistenceFeesMoaTargetUnitSearchResults getSubsistenceFeesMoaTargetUnits(Long moaId, SubsistenceFeesSearchCriteria criteria) {
		
		SubsistenceFeesMoaTargetUnitSearchResultsInfo resultsInfo = subsistenceFeesMoaTargetUnitRepository.findBySearchCriteria(moaId, criteria);
		List<SubsistenceFeesMoaTargetUnitSearchResultInfoDTO> resultInfoDTOs = resultsInfo.getSubsistenceFeesMoaTargetUnitSearchResultInfo()
				.stream()
				.map(SUBSISTENCE_FEES_MAPPER::toSubsistenceFeesMoaTargetUnitSearchResultInfoDTO)
				.collect(Collectors.toList());
		
		return SubsistenceFeesMoaTargetUnitSearchResults.builder()
				.subsistenceFeesMoaTargetUnits(resultInfoDTOs)
				.total(resultsInfo.getTotal())
				.build();
	}

	@Transactional(readOnly = true)
	public SubsistenceFeesMoaTargetUnitDetailsDTO getSubsistenceFeesMoaTargetUnitDetailsById(Long moaTargetUnitId) {
		return subsistenceFeesMoaTargetUnitRepository.getMoaTargetUnitDetailsById(moaTargetUnitId)
				.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
	}

	@Override
	@Transactional(readOnly = true)
	public Long getAccountIdByMoaTargetUnitId(Long moaTargetUnitId) {
		SubsistenceFeesMoaTargetUnit moaTargetUnit = getSubsistenceFeesMoaTargetUnitById(moaTargetUnitId);
		return moaTargetUnit.getAccountId();
	}

	SubsistenceFeesMoaTargetUnit getSubsistenceFeesMoaTargetUnitById(Long moaTargetUnitId) {
		return subsistenceFeesMoaTargetUnitRepository.findById(moaTargetUnitId)
	            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
	}

}
