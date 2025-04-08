package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.subsistencefees.config.SubsistenceFeesConfig;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunCreateValidatorTest {

	@InjectMocks
    private SubsistenceFeesRunCreateValidator validator;
    
    @Mock
    private RequestQueryService requestQueryService;
    
    @Mock
    private SubsistenceFeesConfig subsistenceFeesConfig;
    
    @Test
    void getType() {
    	assertThat(validator.getRequestType()).isEqualTo(CcaRequestType.SUBSISTENCE_FEES_RUN);
    }
    
    @Test
    void validateAction_true() {
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
    	
    	when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.SUBSISTENCE_FEES_RUN, RequestStatuses.IN_PROGRESS, competentAuthority))
    		.thenReturn(false);
    	when(subsistenceFeesConfig.getTriggerDate()).thenReturn(LocalDate.of(2025, 1, 1));
    	
    	RequestCreateValidationResult result = validator.validateAction(competentAuthority, payload);
    	
    	assertThat(result.isValid()).isTrue();
    	verify(subsistenceFeesConfig, times(1)).getTriggerDate();
    	verify(requestQueryService, times(1)).existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.SUBSISTENCE_FEES_RUN, RequestStatuses.IN_PROGRESS, competentAuthority);
    }
    
    @Test
    void validateAction_false_chargingPeriod() {
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
    	
    	when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.SUBSISTENCE_FEES_RUN, RequestStatuses.IN_PROGRESS, competentAuthority))
    		.thenReturn(false);
    	when(subsistenceFeesConfig.getTriggerDate()).thenReturn(LocalDate.of(2050, 1, 1));
    	
    	RequestCreateValidationResult result = validator.validateAction(competentAuthority, payload);
    	
    	assertThat(result.isValid()).isFalse();
    	verify(subsistenceFeesConfig, times(1)).getTriggerDate();
    	verify(requestQueryService, times(1)).existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.SUBSISTENCE_FEES_RUN, RequestStatuses.IN_PROGRESS, competentAuthority);
    }
    
    @Test
    void validateAction_false_requestInProgress() {
    	CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
    	RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
    	
    	when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.SUBSISTENCE_FEES_RUN, RequestStatuses.IN_PROGRESS, competentAuthority))
    		.thenReturn(true);
    	
    	RequestCreateValidationResult result = validator.validateAction(competentAuthority, payload);
    	
    	assertThat(result.isValid()).isFalse();
    	assertThat(result.getReportedRequestTypes()).isEqualTo(Set.of(CcaRequestType.SUBSISTENCE_FEES_RUN));
    	verify(subsistenceFeesConfig, never()).getTriggerDate();
    	verify(requestQueryService, times(1)).existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.SUBSISTENCE_FEES_RUN, RequestStatuses.IN_PROGRESS, competentAuthority);
    }
}
