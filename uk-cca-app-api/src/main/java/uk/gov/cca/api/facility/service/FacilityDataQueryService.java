package uk.gov.cca.api.facility.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.FacilityAuthorityInfoProvider;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.repository.FacilityDataRepository;
import uk.gov.cca.api.facility.transform.FacilityDetailsMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FacilityDataQueryService implements FacilityAuthorityInfoProvider {

    private final FacilityDataRepository facilityDataRepository; 

    private static final FacilityDetailsMapper FACILITY_DETAILS_MAPPER = Mappers.getMapper(FacilityDetailsMapper.class);

    public FacilityDataDetailsDTO getFacilityData(Long facilityId) {
        return facilityDataRepository.findById(facilityId)
                .map(FACILITY_DETAILS_MAPPER::toFacilityDetailsResult)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public FacilityData getFacilityDataById(Long facilityId) {
        return facilityDataRepository.findById(facilityId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
    
    public String getFacilityBusinessIdById(Long facilityId) {
        return facilityDataRepository.findById(facilityId)
        		.map(FacilityData::getFacilityBusinessId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public FacilityBaseInfoDTO getFacilityBaseInfo(Long facilityId) {
        return facilityDataRepository.findById(facilityId)
                .map(FACILITY_DETAILS_MAPPER::toFacilityBaseInfo)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
    
    public List<FacilityBaseInfoDTO> getFacilityBaseInfoByIds(List<Long> facilityIds) {
    	return facilityDataRepository.findAllByIdIn(facilityIds)
                .stream()
                .map(FACILITY_DETAILS_MAPPER::toFacilityBaseInfo)
                .toList();
    }

    public List<FacilityBaseInfoDTO> getFacilityBaseInfoListByFacilityBusinessIds(Set<String> facilityBusinessIds) {
        return facilityDataRepository.findAllByFacilityBusinessIdIn(facilityBusinessIds)
                .stream()
                .map(FACILITY_DETAILS_MAPPER::toFacilityBaseInfo)
                .toList();
    }

    public boolean isExistingFacilityBusinessId(String facilityBusinessId) {
        return facilityDataRepository.existsByFacilityBusinessId(facilityBusinessId);
    }

    public boolean isActiveFacility(String facilityBusinessId) {
        return facilityDataRepository.existsByFacilityBusinessIdAndClosedDateIsNull(facilityBusinessId);
    }

    public Set<SchemeVersion> getActiveFacilityParticipatingSchemeVersions(String facilityBusinessId) {

        return facilityDataRepository.findByFacilityBusinessIdAndClosedDateIsNull(facilityBusinessId)
		        .map(FacilityData::getParticipatingSchemeVersions)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public List<FacilityData> findActiveFacilitiesByAccountId(Long accountId) {
        return facilityDataRepository.findFacilityDataByAccountIdAndClosedDateIsNull(accountId);
    }

    public List<Long> getAllActiveFacilityIdsByAccount(Long accountId) {
        return facilityDataRepository.findFacilityIdsByAccountIdAndClosedDateIsNull(accountId);
    }
    
    @Override
    public Long getAccountIdByFacilityId(Long facilityId) {
        return facilityDataRepository.findById(facilityId).map(FacilityData::getAccountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
    
    public Long getIdByFacilityBusinessId(String facilityBusinessId) {
        return facilityDataRepository.findIdByFacilityBusinessId(facilityBusinessId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public Set<SchemeVersion> getParticipatingFacilitySchemeVersions(String facilityBusinessId) {
        return facilityDataRepository.findByFacilityBusinessId(facilityBusinessId)
                .map(FacilityData::getParticipatingSchemeVersions)
                .orElse(Collections.emptySet());
    }

	public FacilityData exclusiveLockFacility(Long facilityId) {
		return facilityDataRepository.findByIdForUpdate(facilityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));	
	}
}
