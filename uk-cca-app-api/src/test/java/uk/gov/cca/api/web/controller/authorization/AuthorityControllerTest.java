package uk.gov.cca.api.web.controller.authorization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.authorization.orchestrator.UserAuthorityQueryOrchestrator;
import uk.gov.cca.api.web.controller.authorization.orchestrator.dto.LoginStatus;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;

@ExtendWith(MockitoExtension.class)
class AuthorityControllerTest {

	private static final String BASE_PATH = "/v1.0/authorities";
	private static final String CURRENT_USER_STATE_PATH = "current-user-state";

	private MockMvc mockMvc;

	@InjectMocks
	private AuthorityController authorityController;

	@Mock
	private UserAuthorityQueryOrchestrator userAuthorityQueryOrchestrator;

	@Mock
	private AppSecurityComponent appSecurityComponent;

	@BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authorityController)
				.setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            	.setControllerAdvice(new ExceptionControllerAdvice())
            	.build();
    }

	@Test
	void getCurrentUserStatus() throws Exception {
		String userId = "userId";
		String roleType = OPERATOR;
		AppUser currentUser = AppUser.builder().userId(userId).roleType(roleType).build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
		when(userAuthorityQueryOrchestrator.getUserLoginStatusInfo(userId)).thenReturn(LoginStatus.ENABLED);

		mockMvc.perform(MockMvcRequestBuilders
			.get(BASE_PATH + "/" + CURRENT_USER_STATE_PATH)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("userId").value(userId))
			.andExpect(jsonPath("roleType").value(String.valueOf(roleType)))
			.andExpect(jsonPath("status").value(String.valueOf(LoginStatus.ENABLED)));
	}
}
