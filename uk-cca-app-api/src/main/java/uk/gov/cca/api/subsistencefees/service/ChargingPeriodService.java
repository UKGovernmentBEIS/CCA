package uk.gov.cca.api.subsistencefees.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.subsistencefees.repository.ChargingPeriodRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.time.LocalDate;
import java.time.Year;

@Service
@AllArgsConstructor
public class ChargingPeriodService {

    private final ChargingPeriodRepository chargingPeriodRepository;

    public Year getChargingYear(LocalDate currentDate) {
        return chargingPeriodRepository.findChargingYear(currentDate)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
