package uk.gov.cca.api.facility.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.FacilityAuthorityInfoProvider;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.repository.FacilityDataRepository;
import uk.gov.cca.api.facility.transform.FacilityDetailsMapper;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;

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

    public boolean isExistingFacilityId(String facilityId) {
        return facilityDataRepository.existsByFacilityId(facilityId);
    }

    public boolean isActiveFacility(String facilityId) {
        return facilityDataRepository.existsByFacilityIdAndClosedDateIsNull(facilityId);
    }

    public List<FacilityData> findActiveFacilitiesByAccountId(Long accountId) {
        return facilityDataRepository.findFacilityDataByAccountIdAndClosedDateIsNull(accountId);
    }


    @Override
    public Long getAccountIdByFacilityId(String facilityId) {
        return facilityDataRepository.findByFacilityId(facilityId).map(FacilityData::getAccountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
}
