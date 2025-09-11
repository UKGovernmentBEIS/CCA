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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FacilityDataQueryService implements FacilityAuthorityInfoProvider {

    private final FacilityDataRepository facilityDataRepository;

    private static final FacilityDetailsMapper FACILITY_DETAILS_MAPPER = Mappers.getMapper(FacilityDetailsMapper.class);

    public FacilityDataDetailsDTO getFacilityData(String facilityId) {
        return facilityDataRepository.findByFacilityId(facilityId)
                .map(FACILITY_DETAILS_MAPPER::toFacilityDetailsResult)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public FacilityData getFacilityDataById(String facilityId) {
        return facilityDataRepository.findByFacilityId(facilityId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public FacilityBaseInfoDTO getFacilityBaseInfo(Long id) {
        return facilityDataRepository.findById(id)
                .map(FACILITY_DETAILS_MAPPER::toFacilityBaseInfo)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public List<FacilityBaseInfoDTO> getFacilityBaseInfoList(Set<String> facilityIds) {
        return facilityDataRepository.findAllByFacilityIdIn(facilityIds)
                .stream()
                .map(FACILITY_DETAILS_MAPPER::toFacilityBaseInfo)
                .toList();
    }

    public boolean isExistingFacilityId(String facilityId) {
        return facilityDataRepository.existsByFacilityId(facilityId);
    }

    public boolean isActiveFacility(String facilityId) {
        return facilityDataRepository.existsByFacilityIdAndClosedDateIsNull(facilityId);
    }

    public Set<SchemeVersion> getActiveFacilityParticipatingSchemeVersions(String facilityId) {

        return facilityDataRepository.findByFacilityIdAndClosedDateIsNull(facilityId)
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
    public Long getAccountIdByFacilityId(String facilityId) {
        return facilityDataRepository.findByFacilityId(facilityId).map(FacilityData::getAccountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
    
    public Long getIdByFacilityId(String facilityId) {
        return facilityDataRepository.findIdByFacilityId(facilityId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public Set<SchemeVersion> getParticipatingFacilitySchemeVersions(String facilityId) {
        return facilityDataRepository.findByFacilityId(facilityId)
                .map(FacilityData::getParticipatingSchemeVersions)
                .orElse(Collections.emptySet());
    }
}
