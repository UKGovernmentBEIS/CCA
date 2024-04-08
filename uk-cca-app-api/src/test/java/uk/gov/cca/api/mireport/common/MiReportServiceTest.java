package uk.gov.cca.api.mireport.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.mireport.common.MiReportGeneratorService;
import uk.gov.cca.api.mireport.common.MiReportRepository;
import uk.gov.cca.api.mireport.common.MiReportService;
import uk.gov.cca.api.mireport.common.MiReportType;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.mireport.common.domain.dto.EmptyMiReportParams;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportResult;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportSearchResult;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MiReportServiceTest {

    @InjectMocks
    private MiReportService service;

    @Mock
    private MiReportRepository miReportRepository;

    @Mock
    private MiReportGeneratorService miReportGeneratorService;

    @Test
    void findByCompetentAuthority() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        MiReportSearchResult expectedMiReportSearchResult = Mockito.mock(MiReportSearchResult.class);

        when(miReportRepository.findByCompetentAuthority(competentAuthority))
            .thenReturn(List.of(expectedMiReportSearchResult));

        List<MiReportSearchResult> actual = service.findByCompetentAuthority(competentAuthority);

        assertThat(actual.get(0)).isEqualTo(expectedMiReportSearchResult);
    }

    @Test
    void generateReport() {
        final MiReportResult miReportResult = mock(MiReportResult.class);
        AppUser appUser = AppUser.builder()
                .userId("userId")
                .roleType(RoleType.REGULATOR)
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(miReportGeneratorService.generateReport(appUser.getCompetentAuthority(), reportParams)).thenReturn(miReportResult);

        MiReportResult actualMiReportResult = service.generateReport(appUser.getCompetentAuthority(), reportParams);

        assertThat(actualMiReportResult).isEqualTo(miReportResult);
    }
}
