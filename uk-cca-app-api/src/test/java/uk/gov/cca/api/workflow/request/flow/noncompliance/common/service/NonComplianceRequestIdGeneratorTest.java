package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.RequestSequence;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceRequestIdGeneratorTest {

    @InjectMocks
    private NonComplianceRequestIdGenerator generator;

    @Mock
    private TargetUnitAccountRepository targetUnitAccountRepository;

    @Mock
    private RequestSequenceRepository requestSequenceRepository;

    @Mock
    private RequestTypeRepository requestTypeRepository;

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.NON_COMPLIANCE);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("NCOM");
    }

    @Test
    void generate() {
        Long accountId = 10000L;
        long currentSequence = 500;
        RequestParams params = RequestParams.builder()
                .requestResources(Map.of(ResourceType.ACCOUNT, accountId.toString()))
                .type(CcaRequestType.NON_COMPLIANCE)
                .build();

        RequestType requestType = RequestType.builder().code(CcaRequestType.NON_COMPLIANCE).build();

        RequestSequence requestSequence = RequestSequence.builder()
                .id(2L)
                .businessIdentifier(String.valueOf(accountId))
                .sequence(currentSequence)
                .requestType(requestType)
                .build();

        TargetUnitAccount account = TargetUnitAccount.builder()
                .status(TargetUnitAccountStatus.LIVE)
                .name("name")
                .businessId("AIC-T00041")
                .build();

        when(requestSequenceRepository.findByBusinessIdentifierAndRequestType(String.valueOf(accountId), requestType))
                .thenReturn(Optional.of(requestSequence));
        when(targetUnitAccountRepository.findTargetUnitAccountById(accountId))
                .thenReturn(Optional.of(account));
        when(requestTypeRepository.findByCode(CcaRequestType.NON_COMPLIANCE))
                .thenReturn(Optional.of(requestType));

        String result = generator.generate(params);

        assertThat(result).isEqualTo("AIC-T00041" + "-" + "NCOM" + "-" + (currentSequence + 1));
        verify(requestSequenceRepository, times(1)).findByBusinessIdentifierAndRequestType(String.valueOf(accountId), requestType);
        verify(targetUnitAccountRepository, times(1)).findTargetUnitAccountById(accountId);
        verify(requestTypeRepository, times(1)).findByCode(CcaRequestType.NON_COMPLIANCE);
        ArgumentCaptor<RequestSequence> requestSequenceCaptor = ArgumentCaptor.forClass(RequestSequence.class);
        verify(requestSequenceRepository, times(1)).save(requestSequenceCaptor.capture());
        RequestSequence requestSequenceCaptured = requestSequenceCaptor.getValue();
        assertThat(requestSequenceCaptured.getSequence()).isEqualTo(currentSequence + 1);
    }
}
