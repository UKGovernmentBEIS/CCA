package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SectorMoaRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class MoaRequestIdGeneratorTest {

	@InjectMocks
    private MoaRequestIdGenerator generator;

	@Test
	void getTypes() {
		assertThat(generator.getTypes()).containsExactlyInAnyOrder(CcaRequestType.SECTOR_MOA, CcaRequestType.TARGET_UNIT_MOA);
	}
    
    @Test
    void getPrefix() {
    	assertThat(generator.getPrefix()).isNull();
    }
    
    @Test
    void generate() {
    	Long sectorId = 50L;
    	String parentId = "id";
    	String acronym = "ADS";
    	RequestParams params = RequestParams.builder()
    			.requestResources(Map.of(CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString()))
    			.requestMetadata(SectorMoaRequestMetadata.builder().sectorAcronym(acronym).parentRequestId(parentId).build())
    			.type(CcaRequestType.SECTOR_MOA)
    			.build();
    	
    	String result = generator.generate(params);
    	
    	assertThat(result).isEqualTo(acronym + "-" + parentId);
    }
}
