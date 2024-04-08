package uk.gov.cca.api.account.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.AccountVerificationBodyUnappointService;
import uk.gov.cca.api.account.service.event.AccreditationEmissionTradingSchemeNotAvailableEventListener;
import uk.gov.cca.api.common.EmissionTradingScheme;
import uk.gov.cca.api.verificationbody.event.AccreditationEmissionTradingSchemeNotAvailableEvent;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccreditationEmissionTradingSchemeNotAvailableEventListenerTest {
    
    @InjectMocks
    private AccreditationEmissionTradingSchemeNotAvailableEventListener listener;

    @Mock
    private AccountVerificationBodyUnappointService accountVerificationBodyUnappointService;

    @Test
    void onAccreditationEmissionTradingSchemeNotAvailableEvent() {
        Long verificationBodyId = 1L;
        Set<EmissionTradingScheme> notAvailableAccreditationEmissionTradingSchemes = Set.of(mock(EmissionTradingScheme.class));
        AccreditationEmissionTradingSchemeNotAvailableEvent event = 
                new AccreditationEmissionTradingSchemeNotAvailableEvent(verificationBodyId, notAvailableAccreditationEmissionTradingSchemes);

        listener.onAccreditationEmissionTradingSchemeNotAvailableEvent(event);

        verify(accountVerificationBodyUnappointService,times(1))
            .unappointAccountsAppointedToVerificationBodyForEmissionTradingSchemes(verificationBodyId, notAvailableAccreditationEmissionTradingSchemes);
    }

}
