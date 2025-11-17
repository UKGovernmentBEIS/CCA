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

import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.CcaTestRequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateByFacilityValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class RequestCreateActionFacilityResourceTypeHandlerTest {

	@Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private RequestCreateByFacilityValidator requestCreateByFacilityValidator;

    @Mock
    private RequestFacilityCreateActionHandler<CcaTestRequestCreateActionPayload> requestFacilityCreateActionHandler;

    @Test
    void process() {
    	requestCreateByFacilityValidator = new RequestCreateByFacilityValidator() {

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

        requestFacilityCreateActionHandler = new RequestFacilityCreateActionHandler<>() {

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

        List<RequestFacilityCreateActionHandler<CcaTestRequestCreateActionPayload>> handlers = List.of(requestFacilityCreateActionHandler);
        List<RequestCreateByFacilityValidator> validators = List.of(requestCreateByFacilityValidator);

        RequestCreateActionFacilityResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionFacilityResourceTypeHandler<>(validators, handlers, facilityDataQueryService);
        String requestId = requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser);

        assertEquals(requestId, "requestId");

        verify(facilityDataQueryService, times(1)).exclusiveLockFacility(1L);
    }

    @Test
    void process_validator_not_found() {
    	requestCreateByFacilityValidator = new RequestCreateByFacilityValidator() {

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

        requestFacilityCreateActionHandler = new RequestFacilityCreateActionHandler<>() {

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

        List<RequestFacilityCreateActionHandler<CcaTestRequestCreateActionPayload>> handlers = List.of(requestFacilityCreateActionHandler);
        List<RequestCreateByFacilityValidator> validators = List.of(requestCreateByFacilityValidator);

        RequestCreateActionFacilityResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionFacilityResourceTypeHandler<>(validators, handlers, facilityDataQueryService);
        String requestId = requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser);

        assertEquals(requestId, "requestId");

        verify(facilityDataQueryService, times(1)).exclusiveLockFacility(1L);
    }

    @Test
    void process_validator_invalid() {
    	requestCreateByFacilityValidator = new RequestCreateByFacilityValidator() {

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

        List<RequestFacilityCreateActionHandler<CcaTestRequestCreateActionPayload>> handlers = List.of();
        List<RequestCreateByFacilityValidator> validators = List.of(requestCreateByFacilityValidator);

        RequestCreateActionFacilityResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionFacilityResourceTypeHandler<>(validators, handlers, facilityDataQueryService);

        BusinessException businessException = assertThrows(BusinessException.class, () -> requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser));

        assertEquals(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, businessException.getErrorCode());
        assertTrue(Arrays.asList(businessException.getData()).contains(RequestCreateValidationResult.builder()
                .valid(false)
                .build()));
        verify(facilityDataQueryService, times(1)).exclusiveLockFacility(1L);
    }

    @Test
    void process_validator_not_available() {
    	requestCreateByFacilityValidator = new RequestCreateByFacilityValidator() {

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

        List<RequestFacilityCreateActionHandler<CcaTestRequestCreateActionPayload>> handlers = List.of();
        List<RequestCreateByFacilityValidator> validators = List.of(requestCreateByFacilityValidator);

        RequestCreateActionFacilityResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionFacilityResourceTypeHandler<>(validators, handlers, facilityDataQueryService);

        BusinessException businessException = assertThrows(BusinessException.class, () -> requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser));

        assertEquals(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, businessException.getErrorCode());
        assertTrue(Arrays.asList(businessException.getData()).contains(RequestCreateValidationResult.builder()
                .valid(true)
                .isAvailable(false)
                .build()));
        verify(facilityDataQueryService, times(1)).exclusiveLockFacility(1L);
    }

    @Test
    void process_handler_not_found() {
    	requestCreateByFacilityValidator = new RequestCreateByFacilityValidator() {

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

        requestFacilityCreateActionHandler = new RequestFacilityCreateActionHandler<>() {

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

        List<RequestFacilityCreateActionHandler<CcaTestRequestCreateActionPayload>> handlers = List.of(requestFacilityCreateActionHandler);
        List<RequestCreateByFacilityValidator> validators = List.of(requestCreateByFacilityValidator);

        RequestCreateActionFacilityResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionFacilityResourceTypeHandler<>(validators, handlers, facilityDataQueryService);
        BusinessException businessException = assertThrows(BusinessException.class, () -> requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
        assertTrue(Arrays.asList(businessException.getData()).contains("requestType1"));

        verify(facilityDataQueryService, times(1)).exclusiveLockFacility(1L);
    }

    @Test
    void process_resourceId_invalid_type() {
    	RequestCreateActionFacilityResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
    			new RequestCreateActionFacilityResourceTypeHandler<>(List.of(), List.of(), facilityDataQueryService);
        assertThrows(NumberFormatException.class, () -> requestCreateActionResourceTypeHandler.process("abc", "requestType1", null, null));
    }
}
