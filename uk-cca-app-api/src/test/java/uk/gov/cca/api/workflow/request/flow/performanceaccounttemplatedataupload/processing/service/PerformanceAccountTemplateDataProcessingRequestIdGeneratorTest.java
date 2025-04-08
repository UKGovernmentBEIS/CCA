package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestSequence;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataProcessingRequestIdGeneratorTest {

	@InjectMocks
    private PerformanceAccountTemplateDataProcessingRequestIdGenerator cut;
	
	@Mock
    private RequestSequenceRepository requestSequenceRepository;
	
	@Mock
	private RequestTypeRepository requestTypeRepository;
	
	@Mock
	private SectorAssociationQueryService sectorAssociationQueryService;
	
	@Test
    void getPrefix() {
        assertThat(cut.getPrefix()).isEqualTo("PATPC");
    }

    @Test
    void getTypes() {
        assertThat(cut.getTypes()).containsExactly(CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING);
    }
    
    @Test
    void generate() {
    	Long sectorId = 1L;
    	String sectorAcronym = "ACR";
    	String requestTypeCode = CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING;
    	CcaRequestParams requestParams = CcaRequestParams.builder()
    			.type(requestTypeCode)
    			.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()))
    			.requestPayload(PerformanceAccountTemplateDataProcessingRequestPayload.builder()
    					.sectorAssociationInfo(SectorAssociationInfo.builder()
    							.acronym(sectorAcronym)
    							.build())
    					.build())
    			.build();
    	
    	RequestType requestType = RequestType.builder().code("PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING").build();
    	RequestSequence requestSequence = RequestSequence.builder()
    			.businessIdentifier("bi")
    			.sequence(1L)
    			.build();
    	
    	when(requestTypeRepository.findByCode(requestTypeCode)).thenReturn(Optional.of(requestType));
    	when(requestSequenceRepository.findByBusinessIdentifierAndRequestType(String.valueOf(sectorId), requestType))
    		.thenReturn(Optional.of(requestSequence));
    	
    	
    	String actualResult = cut.generate(requestParams);
    	
    	String expectedResult = String.format("%s-%s-%d", sectorAcronym, "PATPC", 2);
    	
    	assertThat(actualResult).isEqualTo(expectedResult);
    	verify(requestTypeRepository, times(1)).findByCode(requestTypeCode);
    	verify(requestSequenceRepository, times(1)).findByBusinessIdentifierAndRequestType(String.valueOf(sectorId), requestType);
    	verify(requestSequenceRepository, times(1)).save(requestSequence);
    }
    
}
