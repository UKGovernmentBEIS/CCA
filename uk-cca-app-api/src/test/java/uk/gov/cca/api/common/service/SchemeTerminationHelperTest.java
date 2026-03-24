package uk.gov.cca.api.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.config.Cca2TerminationConfig;
import uk.gov.cca.api.common.domain.SchemeVersion;

@ExtendWith(MockitoExtension.class)
class SchemeTerminationHelperTest {

	@InjectMocks
    private SchemeTerminationHelper helper;

    @Mock
    private Cca2TerminationConfig cca2TerminationConfig;
    
    @Test
    void isAfterSchemeTerminationDate_true() {
    	when(cca2TerminationConfig.getTerminationDate()).thenReturn(LocalDate.now().minusDays(1));

    	boolean result = helper.isAfterCca2SchemeTerminationDate();

        assertThat(result).isTrue();
    }
    
    @Test
    void isAfterSchemeTerminationDate_false() {
    	LocalDate date = LocalDate.now().minusDays(1);
    	
    	when(cca2TerminationConfig.getTerminationDate()).thenReturn(LocalDate.now().plusDays(1));

    	boolean result = helper.isAfterCca2TerminationDate(date);

    	assertThat(result).isFalse();
    }
    
    @Test
    void isSchemeTerminated_true() {
    	Set<SchemeVersion> version = Set.of(SchemeVersion.CCA_2);
    	when(cca2TerminationConfig.getTerminationDate()).thenReturn(LocalDate.now().minusDays(1));

    	boolean result = helper.isCca2Terminated(version);

        assertThat(result).isTrue();
    }
    
    @Test
    void isSchemeTerminated_false() {
    	LocalDate date = LocalDate.now().minusDays(1);
    	Set<SchemeVersion> version = Set.of(SchemeVersion.CCA_3);
    	
    	boolean result = helper.isCca2Terminated(version, date);

        assertThat(result).isFalse();
        verify(cca2TerminationConfig, never()).getTerminationDate();
    }
    
    @Test
    void resolveTerminationDate_useSchemeEndDate() {
    	Set<SchemeVersion> version = Set.of(SchemeVersion.CCA_2);
    	LocalDateTime date = LocalDateTime.now();
    	LocalDate schemeEndDate = LocalDate.now().minusDays(1);
    	when(cca2TerminationConfig.getTerminationDate()).thenReturn(schemeEndDate);

    	LocalDateTime result = helper.resolveTerminationDate(version, date);

        assertThat(result).isEqualTo(schemeEndDate.atStartOfDay());
    }
    
    @Test
    void resolveTerminationDate_useProvidedDate() {
    	Set<SchemeVersion> version = Set.of(SchemeVersion.CCA_2);
    	LocalDateTime date = LocalDateTime.now();
    	LocalDate schemeEndDate = LocalDate.now().plusDays(1);
    	when(cca2TerminationConfig.getTerminationDate()).thenReturn(schemeEndDate);

    	LocalDateTime result = helper.resolveTerminationDate(version, date);

        assertThat(result).isEqualTo(date);
    }
}
