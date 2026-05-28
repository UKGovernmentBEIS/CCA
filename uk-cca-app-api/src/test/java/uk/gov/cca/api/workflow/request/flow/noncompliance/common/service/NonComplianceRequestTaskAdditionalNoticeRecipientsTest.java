package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.user.operator.service.OperatorUserAuthorityInfoService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class NonComplianceRequestTaskAdditionalNoticeRecipientsTest {

    @InjectMocks
    private NonComplianceRequestTaskAdditionalNoticeRecipients service;

    @Mock
    private OperatorUserAuthorityInfoService operatorUserAuthorityInfoService;

    @Test
    void getRecipients() {
        final long accountId = 1L;
        final AppUser user = AppUser.builder().userId("userId").roleType(REGULATOR).build();
        final List<NoticeRecipientDTO> additionalNoticeRecipients = new ArrayList<>();
        final RequestTask requestTask = RequestTask.builder()
                .id(1L)
                .request(Request.builder()
                        .requestResources(List.of(RequestResource.builder()
                                .resourceType("ACCOUNT")
                                .resourceId("1").build()))
                        .build())
                .build();

        final List<AdditionalNoticeRecipientDTO> operators = Collections.singletonList(AdditionalNoticeRecipientDTO.builder()
                .userId("id1")
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.OPERATOR)
                .build());


        additionalNoticeRecipients.addAll(operators);

        when(operatorUserAuthorityInfoService.getCandidateOperatorNoticeRecipients(user, accountId))
                .thenReturn(operators);

        List<AdditionalNoticeRecipientDTO> result = service.getRecipients(requestTask, user);

        assertThat(additionalNoticeRecipients).isEqualTo(result);
        verify(operatorUserAuthorityInfoService, times(1)).getCandidateOperatorNoticeRecipients(user, accountId);
    }

    @Test
    void getTypes() {
        assertThat(service.getTypes()).containsExactlyInAnyOrder(CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT,
                CcaRequestTaskType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT,
                CcaRequestTaskType.NON_COMPLIANCE_CONCLUSION_SUBMIT);
    }
}
