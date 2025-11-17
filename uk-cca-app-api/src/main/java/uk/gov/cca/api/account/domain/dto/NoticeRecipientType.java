package uk.gov.cca.api.account.domain.dto;

import uk.gov.cca.api.account.domain.TargetUnitAccountContactType;

public enum NoticeRecipientType {
	
	RESPONSIBLE_PERSON, 
	ADMINISTRATIVE_CONTACT, 
	SECTOR_CONTACT, 
	SECTOR_USER,
	SECTOR_CONSULTANT,
	OPERATOR;

	public static NoticeRecipientType fromTargetUnitAccountContactType(TargetUnitAccountContactType tuaContactType) {

		return switch (tuaContactType) {
		case RESPONSIBLE_PERSON -> RESPONSIBLE_PERSON;
		case ADMINISTRATIVE_CONTACT_DETAILS -> ADMINISTRATIVE_CONTACT;
		};
	}

}
