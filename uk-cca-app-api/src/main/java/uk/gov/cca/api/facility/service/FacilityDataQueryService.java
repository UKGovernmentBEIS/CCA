package uk.gov.cca.api.facility.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.repository.FacilityDataRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityDataQueryService {

	private final FacilityDataRepository facilityDataRepository;

	public boolean isExistingFacilityId(String facilityId) {
		return facilityDataRepository.existsByFacilityId(facilityId);
	}

	public boolean isActiveFacility(String facilityId) {
		return facilityDataRepository.existsByFacilityIdAndClosedDateIsNull(facilityId);
	}

	public List<FacilityData> findActiveFacilitiesByAccountId(Long accountId){
		return facilityDataRepository.findFacilityDataByAccountIdAndClosedDateIsNull(accountId);
	}
}
