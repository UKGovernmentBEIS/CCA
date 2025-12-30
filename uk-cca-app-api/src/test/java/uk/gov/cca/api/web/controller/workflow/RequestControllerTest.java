package uk.gov.cca.api.web.controller.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.controller.utils.TestConstrainValidatorFactory;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.referencedata.service.County;
import uk.gov.netz.api.referencedata.service.CountyService;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestCreateActionPayloadTypes;
import uk.gov.netz.api.workflow.request.core.domain.dto.RequestCreateActionProcessDTO;
import uk.gov.netz.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.netz.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.netz.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestCreateActionResourceTypeDelegator;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestCreateActionResourceTypeHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.jsonprovider.RequestCreateActionPayloadCommonTypesProvider;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    private static final String BASE_PATH = "/v1.0/requests";

    private MockMvc mockMvc;

    @InjectMocks
    private RequestController requestController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RequestCreateActionResourceTypeDelegator requestCreateActionResourceTypeDelegator;
    
    @Mock
    private RequestCreateActionResourceTypeHandler handler;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RequestQueryService requestQueryService;

    private ObjectMapper mapper;
    
    @Mock
    private CountyService countyService;
    
    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mapper.registerSubtypes(new RequestCreateActionPayloadCommonTypesProvider().getTypes().toArray(NamedType[]::new));
        
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setObjectMapper(mapper);

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
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .build();
    }

    @Test
    void processRequestCreateAction() throws Exception {
        AppUser appUser = AppUser.builder().userId("id").build();
        String requestType = "DUMMY_REQUEST_CREATE_ACTION_TYPE";

        RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder()
            .payloadType(RequestCreateActionPayloadTypes.EMPTY_PAYLOAD)
            .build();
        RequestCreateActionProcessDTO requestCreateActionProcessDTO = RequestCreateActionProcessDTO.builder()
                .requestType(requestType)
                .requestCreateActionPayload(payload)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(requestCreateActionResourceTypeDelegator.getResourceTypeHandler(requestType)).thenReturn(handler);

        mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_PATH)
                .param("resourceId", "resourceId")
                .content(mapper.writeValueAsString(requestCreateActionProcessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestCreateActionResourceTypeDelegator, times(1)).getResourceTypeHandler(requestType);
        verify(handler, times(1)).process("resourceId", requestType, payload, appUser);
    }

    @Test
    void processRequestCreateAction_forbidden() throws Exception {
    	mapper.registerSubtypes(new RequestCreateActionPayloadCommonTypesProvider().getTypes().toArray(NamedType[]::new));
    	
        AppUser appUser = AppUser.builder().userId("id").build();
        String requestType = "DUMMY_REQUEST_CREATE_ACTION_TYPE";
        RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder()
                .payloadType(RequestCreateActionPayloadTypes.EMPTY_PAYLOAD)
                .build();
        RequestCreateActionProcessDTO requestCreateActionProcessDTO = RequestCreateActionProcessDTO.builder()
                .requestType(requestType)
                .requestCreateActionPayload(payload)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "processRequestCreateAction", "resourceId", null, requestType);

        mockMvc.perform(MockMvcRequestBuilders
                .post(BASE_PATH)
                .param("resourceId", "resourceId")
                .content(mapper.writeValueAsString(requestCreateActionProcessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(requestCreateActionResourceTypeDelegator, handler);
    }

    @Test
    void getRequestDetailsById_forbidden() throws Exception {
        final String requestId = "1";
        AppUser appUser = AppUser.builder().userId("id").build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "getRequestDetailsById", requestId, null, null);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + requestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestQueryService, never()).findRequestDetailsById(anyString());
    }

    @Test
    void getRequestDetailsByResource() throws Exception {
        Long accountId = 1L;
        final String requestId = "1";
        String requestType = "DUMMY_REQUEST_TYPE";
        AppUser user = AppUser.builder().roleType("REGULATOR").build();
        
        RequestSearchCriteria criteria = RequestSearchCriteria.builder()
        		.resourceId(String.valueOf(accountId))
        		.resourceType(ResourceType.ACCOUNT)
        		.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
        		.historyCategory("cat1").build();

        RequestDetailsDTO workflowResult1 = new RequestDetailsDTO(requestId, requestType, "IN_PROGRESS", LocalDateTime.now(), null);

        RequestDetailsSearchResults results = RequestDetailsSearchResults.builder()
                .requestDetails(List.of(workflowResult1))
                .total(10L)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(requestQueryService.findRequestDetailsBySearchCriteria(criteria, user)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/workflows")
                .content(mapper.writeValueAsString(criteria))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(results.getTotal()))
                .andExpect(jsonPath("$.requestDetails[0].id").value(workflowResult1.getId()))
        ;

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestQueryService, times(1)).findRequestDetailsBySearchCriteria(criteria, user);
    }

    @Test
    void getRequestDetailsByResource_forbidden() throws Exception {
        Long accountId = 1L;
        AppUser user = AppUser.builder().userId("user").build();
        RequestSearchCriteria  criteria = RequestSearchCriteria.builder()
        		.resourceId(String.valueOf(accountId))
        		.resourceType(ResourceType.ACCOUNT)
        		.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
        		.historyCategory("cat1").build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getRequestDetailsByResource", String.valueOf(accountId), ResourceType.ACCOUNT, null);

        mockMvc.perform(
                MockMvcRequestBuilders.post(BASE_PATH + "/workflows")
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestQueryService, never()).findRequestDetailsBySearchCriteria(any(), eq(user));
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
