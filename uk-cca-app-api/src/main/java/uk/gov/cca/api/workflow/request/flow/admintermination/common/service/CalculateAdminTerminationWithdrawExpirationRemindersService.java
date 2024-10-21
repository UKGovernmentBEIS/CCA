package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.common.utils.DateService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CalculateAdminTerminationWithdrawExpirationRemindersService {

    private final DateService dateService;

    public LocalDate getExpirationDate() {
        final LocalDateTime currentDate = dateService.getLocalDateTime();
        return currentDate.toLocalDate().plusDays(29);
    }
}
