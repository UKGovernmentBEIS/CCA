package uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.common.EmailNotificationAssignedTaskService;
import uk.gov.netz.api.common.config.AppProperties;
import uk.gov.cca.api.notification.mail.domain.EmailData;
import uk.gov.cca.api.notification.mail.service.NotificationEmailService;
import uk.gov.cca.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailNotificationAssignedTaskServiceTest {

    @InjectMocks
    private EmailNotificationAssignedTaskService emailNotificationAssignedTaskService;

    @Mock
    private NotificationEmailService notificationEmailService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private AppProperties appProperties;

    @Mock
    private AppProperties.Web webProperties;

    private static final String USER_ID = "userId";
    private static final String EMAIL = "email@example.com";
    private static final String HOME_PAGE = "https://www.example.com";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(appProperties.getWeb()).thenReturn(webProperties);
        when(webProperties.getUrl()).thenReturn(HOME_PAGE);
    }

    @Test
    public void sendEmailToRecipient_shouldCallNotifyRecipient_whenUserIdNotNull() {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setEmail(EMAIL);
        when(userAuthService.getUserByUserId(USER_ID)).thenReturn(userInfoDTO);

        emailNotificationAssignedTaskService.sendEmailToRecipient(USER_ID);

        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class),
            eq(EMAIL));
    }
}