package uk.gov.cca.api.notification.mail.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class EmailRecipients {

    @Builder.Default
    private List<String> to = new ArrayList<>();

    @Builder.Default
    private List<String> cc = new ArrayList<>();
    
    @Builder.Default
    private List<String> bcc = new ArrayList<>();
    
}
