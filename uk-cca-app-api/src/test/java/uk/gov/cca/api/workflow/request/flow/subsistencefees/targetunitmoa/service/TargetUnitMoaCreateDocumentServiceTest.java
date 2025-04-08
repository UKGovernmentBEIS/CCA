package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.subsistencefees.config.SubsistenceFeesConfig;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.TargetUnitMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.utils.SubsistenceFeesUtility;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetUnitMoaCreateDocumentServiceTest {

    @InjectMocks
    private TargetUnitMoaCreateDocumentService targetUnitMoaCreateDocumentService;

    @Mock
    private CcaFileDocumentGeneratorService fileDocumentGeneratorService;

    @Mock
    private RequestService requestService;

    @Mock
    private SubsistenceFeesConfig subsistenceFeesConfig;

    @Test
    void create() {
        final long accountId = 1L;
        final String businessId = "businessId";
        final String transactionId = "transactionId";
        final String accountBusinessId = "accountBusinessId";
        final BigDecimal facilityFee = BigDecimal.valueOf(185);
        final List<EligibleFacilityDTO> facilities = List.of(EligibleFacilityDTO.builder()
                .facilityId("fId")
                .targetUnitBusinessId(accountBusinessId)
                .build());
        final String parentRequestId = "parentRequestId";
        final int chargingYear = 2025;
        final String submitterId = UUID.randomUUID().toString();
        final Request parentRequest = Request.builder()
                .id(parentRequestId)
                .metadata(SubsistenceFeesRunRequestMetadata.builder()
                        .chargingYear(Year.of(chargingYear))
                        .build())
                .payload(SubsistenceFeesRunRequestPayload.builder()
                        .submitterId(submitterId)
                        .build())
                .build();
        final Request request = Request.builder()
                .id("requestId")
                .metadata(TargetUnitMoaRequestMetadata.builder()
                        .parentRequestId(parentRequestId)
                        .businessId(businessId)
                        .transactionId(transactionId)
                        .build())
                .build();
        addResourcesToRequest(accountId, request);
        final TemplateParams templateParams = SubsistenceFeesUtility.constructTargetUnitMoaTemplateParams(transactionId, facilities, chargingYear, facilityFee);

        when(subsistenceFeesConfig.getFacilityFee()).thenReturn(facilityFee);
        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);

        // invoke
        targetUnitMoaCreateDocumentService.create(request, facilities);

        // verify
        verify(subsistenceFeesConfig, times(1)).getFacilityFee();
        verify(requestService, times(1)).findRequestById(parentRequestId);
        verify(fileDocumentGeneratorService, times(1)).generateAsync(request, submitterId,
                CcaDocumentTemplateType.TARGET_UNIT_MOA,
                templateParams,
                "2025 Target Unit MoA - businessId - transactionId.pdf");
    }

    private void addResourcesToRequest(Long accountId, Request request) {
        RequestResource sectorResource = RequestResource.builder()
                .resourceType(CcaResourceType.SECTOR_ASSOCIATION)
                .resourceId(accountId.toString())
                .request(request)
                .build();

        request.getRequestResources().add(sectorResource);
    }
}
