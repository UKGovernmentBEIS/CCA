package uk.gov.cca.api.web.controller.mireport;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.mireport.dto.CcaMiReportUserDefinedDTO;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.jsonprovider.MiReportSystemParamsTypesProvider;
import uk.gov.netz.api.mireport.jsonprovider.MiReportSystemResultTypesProvider;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedDTO;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedGeneratorDelegator;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedInfoDTO;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedResult;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedResults;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedService;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedUpdateDTO;
import uk.gov.netz.api.mireport.userdefined.custom.CustomMiReportQuery;
import uk.gov.netz.api.mireport.userdefined.custom.ValidSqlQueryValidator;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;

@ExtendWith(MockitoExtension.class)
class MiReportUserDefinedControllerTest {

    private static final String MI_REPORT_QUERY_BASE_CONTROLLER_PATH = "/v1.0/mireports/user-defined";

    private MockMvc mockMvc;

    @InjectMocks
    private MiReportUserDefinedController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;
    
    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private MiReportUserDefinedService miReportQueryService;

    @Mock
    private MiReportUserDefinedGeneratorDelegator miReportUserDefinedGeneratorDelegator;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerSubtypes(new MiReportSystemParamsTypesProvider().getTypes().toArray(NamedType[]::new));
        objectMapper.registerSubtypes(new MiReportSystemResultTypesProvider().getTypes().toArray(NamedType[]::new));

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect
                authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (MiReportUserDefinedController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setProviderClass(org.hibernate.validator.HibernateValidator.class);
        validatorFactoryBean.setConstraintValidatorFactory(new ConstraintValidatorFactory() {
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key == ValidSqlQueryValidator.class) {
                    return (T) new ValidSqlQueryValidator(miReportUserDefinedGeneratorDelegator);
                }
                try {
                    return key.getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            public void releaseInstance(ConstraintValidator<?, ?> instance) {
                // No resources to release.
            }
        });
        validatorFactoryBean.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .addFilters(new FilterChainProxy(Collections.emptyList()))
                .setConversionService(conversionService)
                .setValidator(validatorFactoryBean)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void create() throws Exception {
        AppUser user = buildMockAuthenticatedUser();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        final CcaMiReportUserDefinedDTO ccaMiReportUserDefinedDTO = CcaMiReportUserDefinedDTO.builder()
                .reportName("test report name")
                .queryDefinition("select * from facility_audit")
                .description("bla bla bla")
                .build();
        final MiReportUserDefinedDTO miReportUserDefinedDTO = MiReportUserDefinedDTO.builder()
                .reportName("test report name")
                .queryDefinition("select * from facility_audit")
                .description("bla bla bla")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(MI_REPORT_QUERY_BASE_CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ccaMiReportUserDefinedDTO)))
                .andExpect(status().isNoContent());

        verify(miReportQueryService, times(1)).create(user, miReportUserDefinedDTO);
    }

    @Test
    void create_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.VERIFIER).build();

        final CcaMiReportUserDefinedDTO ccaMiReportUserDefinedDTO = CcaMiReportUserDefinedDTO.builder()
                .reportName("test report name")
                .queryDefinition("select * from facility_audit")
                .description("bla bla bla")
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
	        .when(appUserAuthorizationService)
	        .authorize(appUser, "createMiReportUserDefined");

        mockMvc.perform(MockMvcRequestBuilders.post(MI_REPORT_QUERY_BASE_CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ccaMiReportUserDefinedDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(miReportQueryService);
        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
    }

    @Test
    void update() throws Exception {
        AppUser user = buildMockAuthenticatedUser();
        final Long queryId = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        final CcaMiReportUserDefinedDTO ccaMiReportUserDefinedUpdateDTO = CcaMiReportUserDefinedDTO.builder()
                .reportName("test report name")
                .queryDefinition("select * from facility_audit")
                .description("bla bla bla")
                .build();
        final MiReportUserDefinedUpdateDTO miReportUserDefinedUpdateDTO = MiReportUserDefinedUpdateDTO.builder()
                .userDefinedDTO(MiReportUserDefinedDTO.builder()
                        .reportName("test report name")
                        .queryDefinition("select * from facility_audit")
                        .description("bla bla bla")
                        .build())
                .reasonForChange("Not provided")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/" + queryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ccaMiReportUserDefinedUpdateDTO)))
                .andExpect(status().isNoContent());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(miReportQueryService, times(1)).update(queryId, user, miReportUserDefinedUpdateDTO);
    }

    @Test
    void delete() throws Exception {
        final Long queryId = 1L;
        final AppUser user = buildMockAuthenticatedUser();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.delete(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/" + queryId))
                .andExpect(status().isNoContent());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(miReportQueryService, times(1)).delete(queryId);
    }

    @Test
    void findById() throws Exception {
        final Long queryId = 1L;
        final AppUser user = buildMockAuthenticatedUser();
        final String queryDefinition = "select * from facility_audit";
        final String reportName = "test report name";
        final String description = "test description";

        final MiReportUserDefinedDTO miReportQueryDTO = MiReportUserDefinedDTO.builder()
                .queryDefinition(queryDefinition)
                .reportName(reportName)
                .description(description)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(miReportQueryService.findById(user, queryId)).thenReturn(miReportQueryDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/" + queryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queryDefinition").value(queryDefinition));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(miReportQueryService, times(1)).findById(user, queryId);
    }
    
    @Test
    void findAll() throws Exception {
        final AppUser user = buildMockAuthenticatedUser();
        final Long queryId = 1L;
        final String reportName = "test report name";
        final String description = "test description";

        final Long queryId2 = 2L;
        final String reportName2 = "test report name 2";
        final String description2 = "test description 2";

        final int pageNumber = 1;
        final int pageSize = 10;

        final MiReportUserDefinedInfoDTO miReportQueryInfoDTO1 = MiReportUserDefinedInfoDTO.builder()
                .id(queryId)
                .reportName(reportName)
                .description(description)
                .build();

        final MiReportUserDefinedInfoDTO miReportQueryInfoDTO2 = MiReportUserDefinedInfoDTO.builder()
                .id(queryId2)
                .reportName(reportName2)
                .description(description2)
                .build();

        final List<MiReportUserDefinedInfoDTO> queries = List.of(miReportQueryInfoDTO1, miReportQueryInfoDTO2);

        MiReportUserDefinedResults expectedResults = MiReportUserDefinedResults.builder()
                .queries(queries)
                .total(2L)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(miReportQueryService.findAllByCA(user.getCompetentAuthority(), pageNumber, pageSize)).thenReturn(expectedResults);

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_QUERY_BASE_CONTROLLER_PATH)
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(miReportQueryService, times(1)).findAllByCA(user.getCompetentAuthority(), pageNumber, pageSize);
    }
    
    @Test
	void generate() throws Exception {
		AppUser appUser = buildMockAuthenticatedUser();
		final Long queryId = 1L;
		MiReportUserDefinedResult result = MiReportUserDefinedResult.builder()
				.columnNames(List.of("col1"))
				.results(List.of(Map.of(
						"entry1", "val1"
						)))
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
		when(miReportQueryService.generateReport(queryId)).thenReturn(result);

		mockMvc.perform(MockMvcRequestBuilders.post(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/" + queryId + "/generate")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.columnNames[0]").value("col1"));

		verify(appSecurityComponent, times(1)).getAuthenticatedUser();
		verify(miReportQueryService, times(1)).generateReport(queryId);
	}
    
	@Test
	void generateCustom() throws Exception {
		AppUser appUser = buildMockAuthenticatedUser();
		CustomMiReportQuery query = CustomMiReportQuery.builder().sqlQuery("sql").build();

		MiReportUserDefinedResult result = MiReportUserDefinedResult.builder()
				.columnNames(List.of("col1"))
				.results(List.of(Map.of(
						"entry1", "val1"
						)))
				.build();
		
		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
		when(miReportQueryService.generateCustomReport(CompetentAuthorityEnum.ENGLAND, query)).thenReturn(result);

		mockMvc.perform(MockMvcRequestBuilders.post(MI_REPORT_QUERY_BASE_CONTROLLER_PATH + "/generate-custom")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(query)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.columnNames[0]").value("col1"));


		verify(appSecurityComponent, times(1)).getAuthenticatedUser();
		verify(miReportQueryService, times(1)).generateCustomReport(appUser.getCompetentAuthority(), query);
	}

    private AppUser buildMockAuthenticatedUser() {
        return AppUser.builder()
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .roleType(RoleTypeConstants.REGULATOR)
                .userId("USER_ID")
                .build();
    }
}
