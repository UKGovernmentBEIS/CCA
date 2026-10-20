package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateByRequestValidator;

@SuppressWarnings({ "unchecked", "rawtypes" })
@ExtendWith(MockitoExtension.class)
class RequestCreateActionFacilityResourceTypeHandlerTest {

	@Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private RequestCreateByFacilityValidator requestCreateByFacilityValidator;

    @Mock
    private RequestCreateByRequestValidator requestCreateByRequestValidator;

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

        requestCreateByRequestValidator = new RequestCreateByRequestValidator<CcaTestRequestCreateActionPayload>() {
            @Override
            public RequestCreateValidationResult validateAction(Long accountId, CcaTestRequestCreateActionPayload payload) {
                return RequestCreateValidationResult.builder()
                        .valid(true)
                        .build();
            }

            @Override
            public String getRequestType() {
                return "requestType1";
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
        List<RequestCreateByFacilityValidator> facilityValidators = List.of(requestCreateByFacilityValidator);
        List<RequestCreateByRequestValidator<CcaTestRequestCreateActionPayload>> requestValidators = List.of(requestCreateByRequestValidator);

        RequestCreateActionFacilityResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionFacilityResourceTypeHandler<>(facilityValidators, requestValidators, handlers, facilityDataQueryService);

        // Invoke
        String requestId = requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser);

        // Verify
        assertThat(requestId).isEqualTo("requestId");
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

        requestCreateByRequestValidator = new RequestCreateByRequestValidator<CcaTestRequestCreateActionPayload>() {
            @Override
            public RequestCreateValidationResult validateAction(Long accountId, CcaTestRequestCreateActionPayload payload) {
                return RequestCreateValidationResult.builder()
                        .valid(true)
                        .build();
            }

            @Override
            public String getRequestType() {
                return "requestType2";
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
        List<RequestCreateByFacilityValidator> facilityValidators = List.of(requestCreateByFacilityValidator);
        List<RequestCreateByRequestValidator<CcaTestRequestCreateActionPayload>> requestValidators = List.of(requestCreateByRequestValidator);

        RequestCreateActionFacilityResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionFacilityResourceTypeHandler<>(facilityValidators, requestValidators, handlers, facilityDataQueryService);

        // Invoke
        String requestId = requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser);

        // Verify
        assertThat(requestId).isEqualTo("requestId");
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

        requestCreateByRequestValidator = new RequestCreateByRequestValidator<CcaTestRequestCreateActionPayload>() {
            @Override
            public RequestCreateValidationResult validateAction(Long accountId, CcaTestRequestCreateActionPayload payload) {
                return RequestCreateValidationResult.builder()
                        .valid(true)
                        .build();
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

        List<RequestFacilityCreateActionHandler<CcaTestRequestCreateActionPayload>> handlers = List.of();
        List<RequestCreateByFacilityValidator> facilityValidators = List.of(requestCreateByFacilityValidator);
        List<RequestCreateByRequestValidator<CcaTestRequestCreateActionPayload>> requestValidators = List.of(requestCreateByRequestValidator);

        RequestCreateActionFacilityResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionFacilityResourceTypeHandler<>(facilityValidators, requestValidators, handlers, facilityDataQueryService);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED);
        assertThat(businessException.getData()).extracting(List.class::cast)
                .extracting(List::getFirst)
                .contains(RequestCreateValidationResult.builder()
                        .valid(false)
                        .build());
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

        requestCreateByRequestValidator = new RequestCreateByRequestValidator<CcaTestRequestCreateActionPayload>() {
            @Override
            public RequestCreateValidationResult validateAction(Long accountId, CcaTestRequestCreateActionPayload payload) {
                return RequestCreateValidationResult.builder()
                        .valid(true)
                        .build();
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

        List<RequestFacilityCreateActionHandler<CcaTestRequestCreateActionPayload>> handlers = List.of();
        List<RequestCreateByFacilityValidator> facilityValidators = List.of(requestCreateByFacilityValidator);
        List<RequestCreateByRequestValidator<CcaTestRequestCreateActionPayload>> requestValidators = List.of(requestCreateByRequestValidator);

        RequestCreateActionFacilityResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionFacilityResourceTypeHandler<>(facilityValidators, requestValidators, handlers, facilityDataQueryService);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED);
        assertThat(businessException.getData()).extracting(List.class::cast)
                .extracting(List::getFirst)
                .contains(RequestCreateValidationResult.builder()
                        .valid(true)
                        .isAvailable(false)
                        .build());
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

        requestCreateByRequestValidator = new RequestCreateByRequestValidator<CcaTestRequestCreateActionPayload>() {
            @Override
            public RequestCreateValidationResult validateAction(Long accountId, CcaTestRequestCreateActionPayload payload) {
                return RequestCreateValidationResult.builder()
                        .valid(true)
                        .build();
            }

            @Override
            public String getRequestType() {
                return "requestType1";
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
        List<RequestCreateByFacilityValidator> facilityValidators = List.of(requestCreateByFacilityValidator);
        List<RequestCreateByRequestValidator<CcaTestRequestCreateActionPayload>> requestValidators = List.of(requestCreateByRequestValidator);

        RequestCreateActionFacilityResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
        		new RequestCreateActionFacilityResourceTypeHandler<>(facilityValidators, requestValidators, handlers, facilityDataQueryService);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                requestCreateActionResourceTypeHandler.process("1", "requestType1", testRequestCreateActionPayload, appUser));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        assertThat(businessException.getData()).contains("requestType1");
        verify(facilityDataQueryService, times(1)).exclusiveLockFacility(1L);
    }

    @Test
    void process_resourceId_invalid_type() {
    	RequestCreateActionFacilityResourceTypeHandler<CcaTestRequestCreateActionPayload> requestCreateActionResourceTypeHandler = 
    			new RequestCreateActionFacilityResourceTypeHandler<>(List.of(), List.of(), List.of(), facilityDataQueryService);

        // Invoke
        assertThrows(NumberFormatException.class, () ->
                requestCreateActionResourceTypeHandler.process("abc", "requestType1", null, null));
    }
}
