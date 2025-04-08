package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunRequestIdGeneratorTest {

	@InjectMocks
    private SubsistenceFeesRunRequestIdGenerator generator;
    
    @Mock
    private RequestTypeRepository requestTypeRepository;
    
    @Mock
    private RequestSequenceRepository requestSequenceRepository;

	@Test
	void getTypes() {
		assertThat(generator.getTypes()).containsExactly(CcaRequestType.SUBSISTENCE_FEES_RUN);
	}
    
    @Test
    void getPrefix() {
    	assertThat(generator.getPrefix()).isEqualTo("S");
    }
    
    @Test
    void resolveRequestSequence() {
    	SubsistenceFeesRunRequestMetadata metadata = SubsistenceFeesRunRequestMetadata.builder()
    			.chargingYear(Year.of(2025))
    			.type(CcaRequestType.SUBSISTENCE_FEES_RUN)
    			.build();
    	RequestType type = RequestType.builder().code(CcaRequestType.SUBSISTENCE_FEES_RUN).build();
    	RequestParams params = RequestParams.builder()
    			.requestMetadata(metadata)
    			.type(CcaRequestType.SUBSISTENCE_FEES_RUN)
    			.build();
    	
    	when(requestTypeRepository.findByCode(CcaRequestType.SUBSISTENCE_FEES_RUN)).thenReturn(Optional.of(type));
    	
    	//invoke
    	generator.resolveRequestSequence(params);
    	
    	verify(requestSequenceRepository, times(1)).findByBusinessIdentifierAndRequestType("25", type);
    	verify(requestTypeRepository, times(1)).findByCode(CcaRequestType.SUBSISTENCE_FEES_RUN);
    }
    
    @Test
    void generateRequestId() {
    	SubsistenceFeesRunRequestMetadata metadata = SubsistenceFeesRunRequestMetadata.builder()
    			.chargingYear(Year.of(2025))
    			.type(CcaRequestType.SUBSISTENCE_FEES_RUN)
    			.build();
    	RequestParams params = RequestParams.builder()
    			.requestMetadata(metadata)
    			.type(CcaRequestType.SUBSISTENCE_FEES_RUN)
    			.build();
    	    	
    	//invoke
    	String result = generator.generateRequestId(10L, params);
    	
    	assertThat(result).isEqualTo("S2510");
    }
}
