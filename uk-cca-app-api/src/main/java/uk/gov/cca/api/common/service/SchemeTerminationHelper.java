package uk.gov.cca.api.common.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.config.Cca2TerminationConfig;
import uk.gov.cca.api.common.domain.SchemeVersion;

@Component
@RequiredArgsConstructor
public class SchemeTerminationHelper {

	private final Cca2TerminationConfig cca2TerminationConfig;
	
	public boolean isAfterCca2SchemeTerminationDate() {
	    return isAfterCca2TerminationDate(LocalDate.now());
	}
	
	public boolean isAfterCca2TerminationDate(LocalDate date) {
	    return date.isAfter(getCca2TerminationDate());
	}
	
	public boolean isCca2Terminated(Set<SchemeVersion> schemeVersions) {
	    return isCca2Terminated(schemeVersions, LocalDate.now());
	}

	public boolean isCca2Terminated(Set<SchemeVersion> schemeVersions, LocalDate date) {
	    return Set.of(SchemeVersion.CCA_2).equals(schemeVersions)
	        && isAfterCca2TerminationDate(date);
	}
	
	public LocalDateTime resolveTerminationDate(Set<SchemeVersion> schemeVersions, LocalDateTime terminationDateTime) {
    	return isCca2Terminated(schemeVersions) 
    			? getCca2TerminationDate().atStartOfDay() 
    					:terminationDateTime;
	}

	public LocalDate getCca2TerminationDate() {
		return cca2TerminationConfig.getTerminationDate();
	}
}
