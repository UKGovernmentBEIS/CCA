package uk.gov.cca.api.mireport.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportParams;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportResult;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MiReportGeneratorService implements InitializingBean {

    @PersistenceContext(unitName = "reportEa")
    private EntityManager reportEaEntityManager;

    @PersistenceContext(unitName = "reportSepa")
    private EntityManager reportSepaEntityManager;

    @PersistenceContext(unitName = "reportNiea")
    private EntityManager reportNieaEntityManager;

    @PersistenceContext(unitName = "reportOpred")
    private EntityManager reportOpredEntityManager;

    @PersistenceContext(unitName = "reportNrw")
    private EntityManager reportNrwEntityManager;

    private final MiReportRepository miReportRepository;

    private final List<MiReportGeneratorHandler> installationMiReportGeneratorHandlers;

    private Map<CompetentAuthorityEnum, EntityManager> caToEntityManagerMap = new EnumMap<>(CompetentAuthorityEnum.class);

    public MiReportResult generateReport(CompetentAuthorityEnum competentAuthority, MiReportParams reportParams) {
        return installationMiReportGeneratorHandlers.stream()
                .filter(generator -> isReportTypeFound(reportParams.getReportType(), generator))
                .findFirst()
                .filter(generator -> miReportRepository.findByCompetentAuthority(competentAuthority)
                        .stream()
                        .anyMatch(miReportSearchResult -> miReportSearchResult.getMiReportType() == reportParams.getReportType()))
                .map(generator -> generator.generateMiReport(caToEntityManagerMap.get(competentAuthority), reportParams))
                .orElseThrow(() -> new BusinessException(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED));
    }

    private boolean isReportTypeFound(MiReportType miReportType, MiReportGeneratorHandler<MiReportParams> generator) {
        return generator.getReportType() == miReportType;
    }

    @Override
    public void afterPropertiesSet() {
        caToEntityManagerMap.put(CompetentAuthorityEnum.ENGLAND, reportEaEntityManager);
        caToEntityManagerMap.put(CompetentAuthorityEnum.SCOTLAND, reportSepaEntityManager);
        caToEntityManagerMap.put(CompetentAuthorityEnum.NORTHERN_IRELAND, reportNieaEntityManager);
        caToEntityManagerMap.put(CompetentAuthorityEnum.OPRED, reportOpredEntityManager);
        caToEntityManagerMap.put(CompetentAuthorityEnum.WALES, reportNrwEntityManager);
    }
}
