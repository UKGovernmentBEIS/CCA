package uk.gov.cca.api.web.orchestrator.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountHeaderInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.service.ResourceHeaderInfoProvider;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.dto.FacilityHeaderInfoDTO;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ResourceHeaderInfoProviderDelegatorTest {

    @Mock
    private ResourceHeaderInfoProvider targetUnitResourceHeaderInfoProvider;
    @Mock
    private ResourceHeaderInfoProvider facilityResourceHeaderInfoProvider;

    @BeforeEach
    void setUp() {
        targetUnitResourceHeaderInfoProvider = new ResourceHeaderInfoProvider() {
            @Override
            public TargetUnitAccountHeaderInfoDTO getResourceHeaderInfo(String resourceId) {
                return TargetUnitAccountHeaderInfoDTO.builder()
                        .status(TargetUnitAccountStatus.LIVE)
                        .name("Target Unit Name 1")
                        .businessId("OperatorTUBusinessId")
                        .build();
            }

            @Override
            public String getResourceType() {
                return ResourceType.ACCOUNT;
            }
        };

        facilityResourceHeaderInfoProvider = new ResourceHeaderInfoProvider() {
            @Override
            public FacilityHeaderInfoDTO getResourceHeaderInfo(String resourceId) {
                return FacilityHeaderInfoDTO.builder()
                        .status(FacilityDataStatus.LIVE)
                        .name("Facility Name 2")
                        .businessId("ADS_1-F00010")
                        .build();
            }

            @Override
            public String getResourceType() {
                return CcaResourceType.FACILITY;
            }
        };
    }

    @Test
    void getResourceHeaderInfoProvider() {

        final List<ResourceHeaderInfoProvider> resourceHeaderInfoProviders = List.of(targetUnitResourceHeaderInfoProvider, facilityResourceHeaderInfoProvider);

        ResourceHeaderInfoProviderDelegator resourceHeaderInfoProviderDelegator = new ResourceHeaderInfoProviderDelegator(resourceHeaderInfoProviders);

        Optional<ResourceHeaderInfoProvider> result = resourceHeaderInfoProviderDelegator.getResourceHeaderInfoProvider(ResourceType.ACCOUNT);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(targetUnitResourceHeaderInfoProvider);
    }
}
