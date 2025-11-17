package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.netz.api.workflow.request.core.domain.RequestSequence;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityAuditRequestIdGeneratorTest {

    @InjectMocks
    private FacilityAuditRequestIdGenerator generator;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private RequestTypeRepository requestTypeRepository;

    @Mock
    private RequestSequenceRepository requestSequenceRepository;

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.FACILITY_AUDIT);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("AUDT");
    }

    @Test
    void generate() {
        Long facilityId = 10000L;
        String facilityBusinessId = "ADS_1-F00059";
        long currentSequence = 500;

        RequestParams params = CcaRequestParams.builder()
                .requestResources(Map.of(CcaResourceType.FACILITY, facilityId.toString()))
                .type(CcaRequestType.FACILITY_AUDIT)
                .build();

        RequestType requestType = RequestType.builder().code(CcaRequestType.FACILITY_AUDIT).build();

        RequestSequence requestSequence = RequestSequence.builder()
                .id(2L)
                .businessIdentifier(String.valueOf(facilityId))
                .sequence(currentSequence)
                .requestType(requestType)
                .build();

        when(requestSequenceRepository.findByBusinessIdentifierAndRequestType(String.valueOf(facilityId), requestType))
                .thenReturn(Optional.of(requestSequence));
        when(facilityDataQueryService.getFacilityBusinessIdById(facilityId))
                .thenReturn(facilityBusinessId);
        when(requestTypeRepository.findByCode(CcaRequestType.FACILITY_AUDIT))
                .thenReturn(Optional.of(requestType));

        String result = generator.generate(params);

        assertThat(result).isEqualTo("ADS_1-F00059" + "-" + "AUDT" + "-" + (currentSequence + 1));
        verify(requestSequenceRepository, times(1)).findByBusinessIdentifierAndRequestType(String.valueOf(facilityId), requestType);
        verify(facilityDataQueryService, times(1)).getFacilityBusinessIdById(facilityId);
        verify(requestTypeRepository, times(1)).findByCode(CcaRequestType.FACILITY_AUDIT);

        ArgumentCaptor<RequestSequence> requestSequenceCaptor = ArgumentCaptor.forClass(RequestSequence.class);
        verify(requestSequenceRepository, times(1)).save(requestSequenceCaptor.capture());
        RequestSequence requestSequenceCaptured = requestSequenceCaptor.getValue();
        assertThat(requestSequenceCaptured.getSequence()).isEqualTo(currentSequence + 1);
    }

}
