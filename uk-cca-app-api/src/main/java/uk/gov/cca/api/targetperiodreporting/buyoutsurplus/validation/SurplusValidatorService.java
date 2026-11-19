package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.netz.api.common.exception.BusinessException;

@Service
public class SurplusValidatorService {
    
    public void validateNoDuplicateResourceIds(List<SurplusCreate> surplusEntries) {
        Set<Long> resourceIds = new HashSet<>();

        for (SurplusCreate entry : surplusEntries) {
            if (!resourceIds.add(entry.getResourceId())) {
                throw new BusinessException(CcaErrorCode.DUPLICATE_KEY);
            }
        }
    }
}
