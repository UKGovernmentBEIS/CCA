package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.tp6.service;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.BaselineData;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.Targets;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestMetadata;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TP6PerformanceDataSpreadsheetGenerateExcelServiceTest {

    @InjectMocks
    private TP6PerformanceDataSpreadsheetGenerateExcelService service;

    @Test
    void testGetTemplateType() {
        assertEquals(TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6,
                service.getTemplateType());
    }

    @Test
    void testGenerate() throws Exception {
        final long accountId = 12345L;
        final PerformanceDataSpreadsheetGenerateRequestMetadata metadata =
                PerformanceDataSpreadsheetGenerateRequestMetadata.builder()
                        .sectorAssociationInfo(SectorAssociationInfo.builder()
                                .acronym("ADS_1")
                                .build())
                        .targetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .template(createDefaultFileDTO())
                        .underlyingAgreement(createMockUnderlyingAgreementDTO())
                        .targetUnitAccountDetails(TargetUnitAccountDetailsDTO.builder()
                                .businessId("ADS_1-T00001")
                                .name("Operator X")
                                .build())
                        .reportVersion(1)
                        .submissionType(PerformanceDataSubmissionType.PRIMARY)
                        .build();

        // Invoke
        FileDTO result = service.generate(metadata, accountId);

        // Verify
        assertNotNull(result);
        assertNotNull(result.getFileContent(), "File content should not be null");
        assertTrue(result.getFileContent().length > 0, "File content should not be empty");
        assertEquals("ADS_1-T00001_TPR_TP6_V1.xlsx", result.getFileName());
    }

    private UnderlyingAgreementDTO createMockUnderlyingAgreementDTO() {
        UnderlyingAgreementContainer container = new UnderlyingAgreementContainer();

        UnderlyingAgreement underlyingAgreement = new UnderlyingAgreement();

        Facility facility = new Facility();
        TargetPeriod6Details period6Details = new TargetPeriod6Details();

        TargetComposition targetComposition = new TargetComposition();
        BaselineData baselineData = new BaselineData();
        Targets targets = new Targets();

        targetComposition.setAgreementCompositionType(AgreementCompositionType.ABSOLUTE);
        targetComposition.setMeasurementType(MeasurementType.ENERGY_KWH);
        targetComposition.setThroughputUnit("10,000 kWh/tonne");
        baselineData.setBaselineDate(LocalDate.parse("2021-01-01"));
        baselineData.setEnergy(BigDecimal.valueOf(100000));
        baselineData.setThroughput(BigDecimal.valueOf(2000));
        targets.setImprovement(BigDecimal.valueOf(15));

        period6Details.setTargetComposition(targetComposition);
        period6Details.setBaselineData(baselineData);
        period6Details.setTargets(targets);

        underlyingAgreement.setFacilities(Set.of(facility));
        underlyingAgreement.setTargetPeriod6Details(period6Details);

        container.setUnderlyingAgreement(underlyingAgreement);

        UnderlyingAgreementDTO dto = new UnderlyingAgreementDTO();

        dto.setUnderlyingAgreementContainer(container);

        return dto;
    }

    private FileDTO createDefaultFileDTO() throws IOException {
        byte[] mockTemplateContent = createMinimalXlsxTemplate();
        FileDTO fileDTO = new FileDTO();
        fileDTO.setFileContent(mockTemplateContent);
        fileDTO.setFileName("original-template.xlsx");
        return fileDTO;
    }

    private static byte[] createMinimalXlsxTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.createSheet("Sheet0");
            workbook.createSheet("Sheet1");
            workbook.write(out);
            return out.toByteArray();
        }
    }
}



