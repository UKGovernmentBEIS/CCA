package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.CcaTestRequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateBySectorAssociationValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class RequestCreateActionSectorResourceTypeHandlerTest {

	@Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Mock
    private RequestCreateBySectorAssociationValidator requestCreateBySectorAssociationValidator;

    @Mock
    private RequestSectorCreateActionHandler<CcaTestRequestCreateActionPayload> requestSectorCreateActionHandler;

    @Test
    void process() {
    	requestCreateBySectorAssociationValidator = new RequestCreateBySectorAssociationValidator() {

            @Override
            public String getRequestType() {
                return "requestType1";
            }

            @Override
            public RequestCreateValidationResult validateAction(Long sectorId) {
                return RequestCreateValidationResult.builder()
                        .valid(true)
                        .build();
            }
        };

        requestSectorCreateActionHandler = new RequestSectorCreateActionHandler<>() {

            @Override
            public String process(Long sectorId, CcaTestRequestCreateActionPayload payload, AppUser appUser) {
                return "requestId";
            }

            @Override
            public String getRequestType() {
                return "requestType1";
            }
        };

        CcaTestRequestCreateActionPayload testRequestCreateActionPayload = CcaTestRequestCreateActionPayload.builder()
                .payloadType("PAYLOAD_TYPE")
                .build();

        AppUser appUser = AppUser.builder().build();

        List<RequestSectorCreateActionHandler<CcaTestRequestCreateActionPayload>> handlers = List.of(requestSectorCreateActionHandler);
        List<RequestCreateBySectorAssociationValidator> validators = List.of(requestCreateBySectorAssociationValidator);

        RequestCreateActionSectorResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionSectorResourceTypeHandler<>(validators, handlers, sectorAssociationQueryService);
        String requestId = requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser);

        assertEquals(requestId, "requestId");

        verify(sectorAssociationQueryService, times(1)).exclusiveLockSectorAssociation(1L);
    }

    @Test
    void process_validator_not_found() {
    	requestCreateBySectorAssociationValidator = new RequestCreateBySectorAssociationValidator() {

            @Override
            public String getRequestType() {
                return "requestType2";
            }

            @Override
            public RequestCreateValidationResult validateAction(Long sectorId) {
                return RequestCreateValidationResult.builder()
                        .valid(true)
                        .build();
            }
        };

        requestSectorCreateActionHandler = new RequestSectorCreateActionHandler<>() {

            @Override
            public String process(Long sectorId, CcaTestRequestCreateActionPayload payload, AppUser appUser) {
                return "requestId";
            }

            @Override
            public String getRequestType() {
                return "requestType1";
            }
        };

        CcaTestRequestCreateActionPayload testRequestCreateActionPayload = CcaTestRequestCreateActionPayload.builder()
                .payloadType("PAYLOAD_TYPE")
                .build();

        AppUser appUser = AppUser.builder().build();

        List<RequestSectorCreateActionHandler<CcaTestRequestCreateActionPayload>> handlers = List.of(requestSectorCreateActionHandler);
        List<RequestCreateBySectorAssociationValidator> validators = List.of(requestCreateBySectorAssociationValidator);

        RequestCreateActionSectorResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionSectorResourceTypeHandler<>(validators, handlers, sectorAssociationQueryService);
        String requestId = requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser);

        assertEquals(requestId, "requestId");

        verify(sectorAssociationQueryService, times(1)).exclusiveLockSectorAssociation(1L);
    }

    @Test
    void process_validator_invalid() {
    	requestCreateBySectorAssociationValidator = new RequestCreateBySectorAssociationValidator() {

            @Override
            public String getRequestType() {
                return "requestType1";
            }

            @Override
            public RequestCreateValidationResult validateAction(Long sectorId) {
                return RequestCreateValidationResult.builder()
                        .valid(false)
                        .build();
            }
        };

        CcaTestRequestCreateActionPayload testRequestCreateActionPayload = CcaTestRequestCreateActionPayload.builder()
                .payloadType("PAYLOAD_TYPE")
                .build();

        AppUser appUser = AppUser.builder().build();

        List<RequestSectorCreateActionHandler<CcaTestRequestCreateActionPayload>> handlers = List.of();
        List<RequestCreateBySectorAssociationValidator> validators = List.of(requestCreateBySectorAssociationValidator);

        RequestCreateActionSectorResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionSectorResourceTypeHandler<>(validators, handlers, sectorAssociationQueryService);

        BusinessException businessException = assertThrows(BusinessException.class, () -> requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser));

        assertEquals(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, businessException.getErrorCode());
        assertTrue(Arrays.asList(businessException.getData()).contains(RequestCreateValidationResult.builder()
                .valid(false)
                .build()));
        verify(sectorAssociationQueryService, times(1)).exclusiveLockSectorAssociation(1L);
    }

    @Test
    void process_validator_not_available() {
    	requestCreateBySectorAssociationValidator = new RequestCreateBySectorAssociationValidator() {

            @Override
            public String getRequestType() {
                return "requestType1";
            }

            @Override
            public RequestCreateValidationResult validateAction(Long sectorId) {
                return RequestCreateValidationResult.builder()
                        .valid(true)
                        .isAvailable(false)
                        .build();
            }
        };

        CcaTestRequestCreateActionPayload testRequestCreateActionPayload = CcaTestRequestCreateActionPayload.builder()
                .payloadType("PAYLOAD_TYPE")
                .build();

        AppUser appUser = AppUser.builder().build();

        List<RequestSectorCreateActionHandler<CcaTestRequestCreateActionPayload>> handlers = List.of();
        List<RequestCreateBySectorAssociationValidator> validators = List.of(requestCreateBySectorAssociationValidator);

        RequestCreateActionSectorResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionSectorResourceTypeHandler<>(validators, handlers, sectorAssociationQueryService);

        BusinessException businessException = assertThrows(BusinessException.class, () -> requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser));

        assertEquals(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, businessException.getErrorCode());
        assertTrue(Arrays.asList(businessException.getData()).contains(RequestCreateValidationResult.builder()
                .valid(true)
                .isAvailable(false)
                .build()));
        verify(sectorAssociationQueryService, times(1)).exclusiveLockSectorAssociation(1L);
    }

    @Test
    void process_handler_not_found() {
    	requestCreateBySectorAssociationValidator = new RequestCreateBySectorAssociationValidator() {

            @Override
            public String getRequestType() {
                return "requestType1";
            }

            @Override
            public RequestCreateValidationResult validateAction(Long sectorId) {
                return RequestCreateValidationResult.builder()
                        .valid(true)
                        .build();
            }
        };

        requestSectorCreateActionHandler = new RequestSectorCreateActionHandler<>() {

            @Override
            public String process(Long sectorId, CcaTestRequestCreateActionPayload payload, AppUser appUser) {
                return "requestId";
            }

            @Override
            public String getRequestType() {
                return "requestType2";
            }
        };

        CcaTestRequestCreateActionPayload testRequestCreateActionPayload = CcaTestRequestCreateActionPayload.builder()
                .payloadType("PAYLOAD_TYPE")
                .build();

        AppUser appUser = AppUser.builder().build();

        List<RequestSectorCreateActionHandler<CcaTestRequestCreateActionPayload>> handlers = List.of(requestSectorCreateActionHandler);
        List<RequestCreateBySectorAssociationValidator> validators = List.of(requestCreateBySectorAssociationValidator);

        RequestCreateActionSectorResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionSectorResourceTypeHandler<>(validators, handlers, sectorAssociationQueryService);
        BusinessException businessException = assertThrows(BusinessException.class, () -> requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
        assertTrue(Arrays.asList(businessException.getData()).contains("requestType1"));

        verify(sectorAssociationQueryService, times(1)).exclusiveLockSectorAssociation(1L);
    }

    @Test
    void process_resourceId_invalid_type() {
    	RequestCreateActionSectorResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
    			new RequestCreateActionSectorResourceTypeHandler<>(List.of(), List.of(), sectorAssociationQueryService);
        assertThrows(NumberFormatException.class, () -> requestCreateActionResourceTypeHandler.process("abc", "requestType1", null, null));
    }
}
