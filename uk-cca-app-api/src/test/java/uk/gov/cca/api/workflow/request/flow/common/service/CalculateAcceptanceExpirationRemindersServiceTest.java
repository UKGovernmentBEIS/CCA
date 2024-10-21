package uk.gov.cca.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.utils.DateService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateAcceptanceExpirationRemindersServiceTest {
    @InjectMocks
    private CalculateAcceptanceExpirationRemindersService expirationRemindersService;
    @Mock
    private DateService dateService;

    @Test
    void getExpirationDate() {
        final LocalDateTime date = LocalDateTime.now();

        when(dateService.getLocalDateTime())
                .thenReturn(date);

        // Invoke
        LocalDate result = expirationRemindersService.getExpirationDate();

        // Verify
        assertThat(result).isEqualTo(date.toLocalDate().plusDays(29));
        verify(dateService, times(1)).getLocalDateTime();

    }
}