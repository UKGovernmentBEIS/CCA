package uk.gov.cca.api.facility.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.facility.domain.FacilityIdentifier;
import uk.gov.cca.api.facility.repository.FacilityIdentifierRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FacilityIdentifierService {

    private final FacilityIdentifierRepository facilityIdentifierRepository;

    @Transactional
    public Long incrementAndGet(Long sectorAssociationId) {
        FacilityIdentifier identifier = facilityIdentifierRepository.findFacilityIdentifierBySectorAssociationId(sectorAssociationId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        identifier.setFacilityId(identifier.getFacilityId() + 1);

        return identifier.getFacilityId();
    }
}
