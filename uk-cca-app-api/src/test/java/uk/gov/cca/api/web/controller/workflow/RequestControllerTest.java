package uk.gov.cca.api.web.controller.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.cca.api.referencedata.service.County;
import uk.gov.cca.api.web.controller.utils.TestConstrainValidatorFactory;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.referencedata.service.CountyService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.security.AppSecurityComponent;
import uk.gov.cca.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.cca.api.web.security.AuthorizedAspect;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestCreateActionProcessDTO;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestSearchByAccountCriteria;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestHistoryCategory;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.cca.api.workflow.request.core.service.RequestQueryService;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandlerMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestCreateActionType.DUMMY_REQUEST_CREATE_ACTION_TYPE;
import static uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType.DUMMY_REQUEST_TYPE;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    private static final String BASE_PATH = "/v1.0/requests";

    private MockMvc mockMvc;

    @InjectMocks
    private RequestController requestController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RequestCreateActionHandlerMapper requestCreateActionHandlerMapper;
    
    @Mock
    private RequestCreateActionHandler handler;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RequestQueryService requestQueryService;

    private ObjectMapper mapper;
    
    @Mock
    private CountyService countyService;
    
    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(requestController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        requestController = (RequestController) aopProxy.getProxy();
        
        LocalValidatorFactoryBean validatorFactoryBean = mockValidatorFactoryBean();
        
        mockMvc = MockMvcBuilders.standaloneSetup(requestController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setValidator(validatorFactoryBean)
                .build();
    }

    @Test
    void processRequestCreateAction() throws Exception {
        AppUser appUser = AppUser.builder().userId("id").build();
        RequestCreateActionType requestCreateActionType = DUMMY_REQUEST_CREATE_ACTION_TYPE;

        RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder()
            .payloadType(RequestCreateActionPayloadType.EMPTY_PAYLOAD)
            .build();
        RequestCreateActionProcessDTO requestCreateActionProcessDTO = RequestCreateActionProcessDTO.builder()
                .requestCreateActionType(requestCreateActionType)
                .requestCreateActionPayload(payload)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(requestCreateActionHandlerMapper.get(requestCreateActionType)).thenReturn(handler);

        mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_PATH)
                .content(mapper.writeValueAsString(requestCreateActionProcessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestCreateActionHandlerMapper, times(1)).get(requestCreateActionType);
        verify(handler, times(1)).process(null, requestCreateActionType, payload, appUser);
    }

    @Test
    void processRequestCreateAction_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().userId("id").build();
        RequestCreateActionType requestCreateActionType = DUMMY_REQUEST_CREATE_ACTION_TYPE;
        RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder()
                .payloadType(RequestCreateActionPayloadType.EMPTY_PAYLOAD)
                .build();
        RequestCreateActionProcessDTO requestCreateActionProcessDTO = RequestCreateActionProcessDTO.builder()
                .requestCreateActionType(requestCreateActionType)
                .requestCreateActionPayload(payload)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "processRequestCreateAction", null, requestCreateActionType.name());

        mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_PATH)
                .content(mapper.writeValueAsString(requestCreateActionProcessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(requestCreateActionHandlerMapper, handler);
    }

    @Test
    void getRequestDetailsById_forbidden() throws Exception {
        final String requestId = "1";
        AppUser appUser = AppUser.builder().userId("id").build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "getRequestDetailsById", requestId);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + requestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestQueryService, never()).findRequestDetailsById(anyString());
    }

    @Test
    void getRequestDetailsByAccountId() throws Exception {
        Long accountId = 1L;
        final String requestId = "1";
        RequestType requestType = DUMMY_REQUEST_TYPE;
        RequestSearchByAccountCriteria criteriaByAccount = RequestSearchByAccountCriteria.builder().accountId(accountId)
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
        		.category(RequestHistoryCategory.PERMIT).build();
        
        RequestSearchCriteria criteria = RequestSearchCriteria.builder().accountId(accountId)
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
        		.category(RequestHistoryCategory.PERMIT).build();

        RequestDetailsDTO workflowResult1 = new RequestDetailsDTO(requestId, requestType, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);

        RequestDetailsSearchResults results = RequestDetailsSearchResults.builder()
                .requestDetails(List.of(workflowResult1))
                .total(10L)
                .build();

        when(requestQueryService.findRequestDetailsBySearchCriteria(criteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/workflows")
                .content(mapper.writeValueAsString(criteriaByAccount))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(results.getTotal()))
                .andExpect(jsonPath("$.requestDetails[0].id").value(workflowResult1.getId()))
        ;

        verify(requestQueryService, times(1)).findRequestDetailsBySearchCriteria(criteria);
    }

    @Test
    void getRequestDetailsByAccountId_forbidden() throws Exception {
        Long accountId = 1L;
        AppUser user = AppUser.builder().userId("user").build();
        RequestSearchByAccountCriteria criteria = RequestSearchByAccountCriteria.builder().accountId(accountId)
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
        		.category(RequestHistoryCategory.PERMIT).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getRequestDetailsByAccountId", String.valueOf(accountId));

        mockMvc.perform(
                MockMvcRequestBuilders.post(BASE_PATH + "/workflows")
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestQueryService, never()).findRequestDetailsBySearchCriteria(any());
    }
    
    private LocalValidatorFactoryBean mockValidatorFactoryBean() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        MockServletContext servletContext = new MockServletContext();
        GenericWebApplicationContext context = new GenericWebApplicationContext(servletContext);
        ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
        
        beanFactory.registerSingleton(County.CountyValidator.class.getCanonicalName(),
        		new County.CountyValidator(countyService));
        
        context.refresh();
        validatorFactoryBean.setApplicationContext(context);
        TestConstrainValidatorFactory constraintValidatorFactory = new TestConstrainValidatorFactory(context);
        validatorFactoryBean.setConstraintValidatorFactory(constraintValidatorFactory);
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
    }
}
