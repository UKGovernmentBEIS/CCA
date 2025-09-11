package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.transform;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountPayload;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountPayloadMapperTest {

    private TargetUnitAccountPayloadMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(TargetUnitAccountPayloadMapper.class);
    }

    @Test
    void toAccountDTO() {
        String accountName = "accountName";
        String userId = "userId";
        long sectorAssociationId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        CcaEmissionTradingScheme ets = CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME;
        TargetUnitAccountPayload accountPayload = createAccountPayload(accountName, competentAuthority, ets);

        TargetUnitAccountDTO accountDTO = mapper.toTargetUnitAccountDTO(accountPayload, sectorAssociationId, userId);

        assertThat(accountDTO.getName()).isEqualTo(accountName);
        assertThat(accountDTO.getCreatedBy()).isEqualTo(userId);
        assertThat(accountDTO.getSectorAssociationId()).isEqualTo(sectorAssociationId);
        assertThat(accountDTO.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(accountDTO.getEmissionTradingScheme()).isEqualTo(ets);
    }

    @Test
    void toTargetUnitAccountCreationRequestPayload() {
        String accountName = "account";
        String businessId = "businessId";
        long sectorAssociationId = 1L;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        CcaEmissionTradingScheme ets = CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME;
        TargetUnitAccountPayload accountPayload = createAccountPayload(accountName, competentAuthority, ets);

        TargetUnitAccountCreationRequestPayload requestPayload =
                mapper.toTargetUnitAccountCreationRequestPayload(accountPayload, businessId, sectorAssociationId);

        assertThat(requestPayload.getPayloadType()).isEqualTo(CcaRequestPayloadType.TARGET_UNIT_ACCOUNT_CREATION_REQUEST_PAYLOAD);
        assertThat((requestPayload.getBusinessId())).isEqualTo(businessId);
        assertThat((requestPayload.getSectorAssociationId())).isEqualTo(sectorAssociationId);

        TargetUnitAccountPayload retrievedAccountPayload = requestPayload.getPayload();

        assertThat(retrievedAccountPayload.getName()).isEqualTo(accountName);
        assertThat(retrievedAccountPayload.getOperatorType()).isEqualTo(TargetUnitAccountOperatorType.SOLE_TRADER);
        AssertionsForInterfaceTypes.assertThat(retrievedAccountPayload.getCompetentAuthority()).isEqualTo(competentAuthority);
        AssertionsForInterfaceTypes.assertThat(retrievedAccountPayload.getEmissionTradingScheme()).isEqualTo(ets);
    }

    private TargetUnitAccountPayload createAccountPayload(String accountName, CompetentAuthorityEnum ca, CcaEmissionTradingScheme ets) {
        return TargetUnitAccountPayload.builder()
                .name(accountName)
                .competentAuthority(ca)
                .emissionTradingScheme(ets)
                .address(AccountAddressDTO.builder().build())
                .operatorType(TargetUnitAccountOperatorType.SOLE_TRADER)
                .administrativeContactDetails(
                    TargetUnitAccountContactDTO.builder().build())
                .responsiblePerson(
                    TargetUnitAccountContactDTO.builder().build())
                .build();
    }
}
