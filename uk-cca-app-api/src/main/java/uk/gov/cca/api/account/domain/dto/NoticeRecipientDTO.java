package uk.gov.cca.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.account.domain.TargetUnitAccountContactType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NoticeRecipientDTO {

    private String firstName;
    private String lastName;
    private String email;
    private NoticeRecipientType type;
	
    
    public NoticeRecipientDTO(String firstName, String lastName, String email, TargetUnitAccountContactType type) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.type = NoticeRecipientType.fromTargetUnitAccountContactType(type);
	}
    
    
    
}
