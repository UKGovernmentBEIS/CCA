package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunUpdateService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunSubmittedServiceTest {

    @InjectMocks
    private SubsistenceFeesRunSubmittedService service;

    @Mock
    private RequestService requestService;

    @Mock
    private SubsistenceFeesRunUpdateService subsistenceFeesRunUpdateService;

    @Test
    void subsistenceFeesRunSubmitted() {
        Long runId = 1L;
        String requestId = "requestId";
        String submitterId = "submitterId";
        Year chargingYear = Year.of(2025);
        SubsistenceFeesRunRequestPayload payload = SubsistenceFeesRunRequestPayload.builder().submitterId(submitterId).build();
        SubsistenceFeesRunRequestMetadata metadata = SubsistenceFeesRunRequestMetadata.builder()
                .chargingYear(chargingYear)
                .build();
        Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceId(CompetentAuthorityEnum.ENGLAND.name())
                        .resourceType("CA")
                        .build()))
                .payload(payload)
                .metadata(metadata)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(subsistenceFeesRunUpdateService.createSubsistenceFeesRun(requestId, CompetentAuthorityEnum.ENGLAND, chargingYear)).thenReturn(runId);

        service.subsistenceFeesRunSubmitted(requestId);

        assertThat(request.getSubmissionDate()).isNotNull();
        assertThat(payload.getRunId()).isEqualTo(runId);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
                request, null, CcaRequestActionType.SUBSISTENCE_FEES_RUN_SUBMITTED, submitterId);
        verify(subsistenceFeesRunUpdateService, times(1)).createSubsistenceFeesRun(requestId, CompetentAuthorityEnum.ENGLAND, metadata.getChargingYear());
    }
}
