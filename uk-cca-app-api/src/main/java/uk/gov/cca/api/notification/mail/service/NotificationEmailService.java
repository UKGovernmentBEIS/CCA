package uk.gov.cca.api.notification.mail.service;

import uk.gov.cca.api.notification.mail.domain.EmailData;

import java.util.List;

/**
 * Service for generating mail objects.
 */
public interface NotificationEmailService {

    void notifyRecipient(EmailData emailData, String recipientEmail);

    void notifyRecipients(EmailData emailData, List<String> recipientsEmails);

    void notifyRecipients(EmailData emailData, List<String> recipientsEmails, List<String> ccRecipientsEmails);

    void notifyRecipients(EmailData emailData, List<String> recipientsEmails, List<String> ccRecipientsEmails,
                          List<String> bccRecipientsEmails);
}
