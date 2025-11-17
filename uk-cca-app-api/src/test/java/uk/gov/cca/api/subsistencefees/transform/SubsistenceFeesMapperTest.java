package uk.gov.cca.api.subsistencefees.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.FacilityProcessStatus;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacilityMarkingStatusHistory;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaReceivedAmountHistory;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaReceivedAmountHistoryPayload;
import uk.gov.cca.api.subsistencefees.domain.dto.FacilityProcessStatusCreationDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaReceivedAmountHistoryDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaReceivedAmountInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaDetails;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaTargetUnitSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunMoaDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunSearchResultInfo;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SubsistenceFeesMapperTest {

    private final SubsistenceFeesMapper mapper = Mappers.getMapper(SubsistenceFeesMapper.class);


    @Test
    void toSubsistenceFeesRunSearchResultInfoDTO_CANCELLED() {
        final LocalDateTime date = LocalDateTime.now();
        final BigDecimal amount = BigDecimal.ZERO;
        final SubsistenceFeesRunSearchResultInfo resultInfo = new SubsistenceFeesRunSearchResultInfo(1L, "S2501", date, amount, amount, amount);
        final SubsistenceFeesRunSearchResultInfoDTO expectedResultInfoDTO =
                new SubsistenceFeesRunSearchResultInfoDTO(1L, "S2501", date, PaymentStatus.CANCELLED, FacilityPaymentStatus.CANCELLED, amount, amount);

        // invoke
        SubsistenceFeesRunSearchResultInfoDTO resultInfoDto = mapper.toSubsistenceFeesRunSearchResultInfoDTO(resultInfo);

        assertThat(resultInfoDto).isEqualTo(expectedResultInfoDTO);
    }

    @Test
    void toSubsistenceFeesRunSearchResultInfoDTO_paymentStatus_AWAITING_PAYMENT_facilityStatus_COMPLETED() {
        final LocalDateTime date = LocalDateTime.now();
        final BigDecimal total = BigDecimal.valueOf(1000L);
        final BigDecimal received = BigDecimal.valueOf(700L);
        final BigDecimal facilityOutstanding = BigDecimal.ZERO;
        final SubsistenceFeesRunSearchResultInfo resultInfo = new SubsistenceFeesRunSearchResultInfo(1L, "S2501", date, total, facilityOutstanding, received);
        final SubsistenceFeesRunSearchResultInfoDTO expectedResultInfoDTO =
                new SubsistenceFeesRunSearchResultInfoDTO(1L, "S2501", date, PaymentStatus.AWAITING_PAYMENT, FacilityPaymentStatus.COMPLETED, total, BigDecimal.valueOf(300L));

        // invoke
        SubsistenceFeesRunSearchResultInfoDTO resultInfoDto = mapper.toSubsistenceFeesRunSearchResultInfoDTO(resultInfo);

        assertThat(resultInfoDto).isEqualTo(expectedResultInfoDTO);
    }

    @Test
    void toSubsistenceFeesRunSearchResultInfoDTO_paymentStatus_OVERPAID_outstanding_negative() {
        final LocalDateTime date = LocalDateTime.now();
        final BigDecimal total = BigDecimal.valueOf(1000L);
        final BigDecimal received = BigDecimal.valueOf(2000L);
        final BigDecimal facilityOutstanding = BigDecimal.ZERO;
        final SubsistenceFeesRunSearchResultInfo resultInfo = new SubsistenceFeesRunSearchResultInfo(1L, "S2501", date, total, facilityOutstanding, received);
        final SubsistenceFeesRunSearchResultInfoDTO expectedResultInfoDTO =
                new SubsistenceFeesRunSearchResultInfoDTO(1L, "S2501", date, PaymentStatus.OVERPAID, FacilityPaymentStatus.COMPLETED, total, BigDecimal.ZERO);

        // invoke
        SubsistenceFeesRunSearchResultInfoDTO resultInfoDto = mapper.toSubsistenceFeesRunSearchResultInfoDTO(resultInfo);

        assertThat(resultInfoDto).isEqualTo(expectedResultInfoDTO);
    }

    @Test
    void toSubsistenceFeesRunDetailsDTO() {
        final LocalDateTime date = LocalDateTime.now();
        final BigDecimal initialTotal = BigDecimal.valueOf(1000L);
        final BigDecimal total = BigDecimal.valueOf(1000L);
        final BigDecimal received = BigDecimal.valueOf(1000L);
        final SubsistenceFeesRunDetailsInfo detailsInfo = new SubsistenceFeesRunDetailsInfo(1L, "S2501", date, initialTotal, total);
        final SubsistenceFeesRunMoaDetailsInfo moaDetailsInfo = new SubsistenceFeesRunMoaDetailsInfo(received, 50L, 1L);
        final SubsistenceFeesRunDetailsDTO expectedDTO =
                new SubsistenceFeesRunDetailsDTO(1L, "S2501", date, PaymentStatus.PAID, initialTotal, total, BigDecimal.ZERO, 50L, 1L);

        // invoke
        SubsistenceFeesRunDetailsDTO resultDTO = mapper.toSubsistenceFeesRunDetailsDTO(detailsInfo, moaDetailsInfo);

        assertThat(resultDTO).isEqualTo(expectedDTO);
    }

    @Test
    void toSubsistenceFeesMoaSearchResultInfoDTO() {
        final LocalDateTime date = LocalDateTime.now();
        final String businessId = "businessId";
        final String name = "name";
        final BigDecimal total = BigDecimal.valueOf(1000L);
        final BigDecimal received = BigDecimal.valueOf(1000L);
        final BigDecimal facilityOutstanding = BigDecimal.valueOf(1000L);
        final SubsistenceFeesMoaSearchResultInfo resultInfo = new SubsistenceFeesMoaSearchResultInfo(
                1L, "CCACM1200", businessId, name, total, facilityOutstanding, received, date);
        final SubsistenceFeesMoaSearchResultInfoDTO expectedResultInfoDTO = new SubsistenceFeesMoaSearchResultInfoDTO(
                1L, "CCACM1200", businessId, name, PaymentStatus.PAID, FacilityPaymentStatus.IN_PROGRESS, total, BigDecimal.ZERO, date);

        // invoke
        SubsistenceFeesMoaSearchResultInfoDTO resultInfoDto = mapper.toSubsistenceFeesMoaSearchResultInfoDTO(resultInfo);

        assertThat(resultInfoDto).isEqualTo(expectedResultInfoDTO);
    }

    @Test
    void toSubsistenceFeesMoaTargetUnitSearchResultInfoDTO() {
        final String businessId = "businessId";
        final String name = "name";
        final BigDecimal total = BigDecimal.valueOf(1000L);
        final BigDecimal facilityOutstanding = BigDecimal.valueOf(500L);
        final SubsistenceFeesMoaTargetUnitSearchResultInfo resultInfo = new SubsistenceFeesMoaTargetUnitSearchResultInfo(
                1L, businessId, name, total, facilityOutstanding);
        final SubsistenceFeesMoaTargetUnitSearchResultInfoDTO expectedResultInfoDTO = new SubsistenceFeesMoaTargetUnitSearchResultInfoDTO(
                1L, businessId, name, FacilityPaymentStatus.IN_PROGRESS, total);

        // invoke
        SubsistenceFeesMoaTargetUnitSearchResultInfoDTO resultInfoDto = mapper.toSubsistenceFeesMoaTargetUnitSearchResultInfoDTO(resultInfo);

        assertThat(resultInfoDto).isEqualTo(expectedResultInfoDTO);
    }

    @Test
    void toFacilityProcessStatus() {
        Year chargingYear = Year.of(2025);
        FacilityProcessStatusCreationDTO facilityProcessStatusCreationDTO = FacilityProcessStatusCreationDTO.builder()
                .facilityId(1L)
                .runId(1L)
                .chargingYear(chargingYear)
                .moaType(MoaType.SECTOR_MOA)
                .build();

        FacilityProcessStatus expectedResult = FacilityProcessStatus.builder()
                .facilityId(1L)
                .runId(1L)
                .chargingYear(chargingYear)
                .moaType(MoaType.SECTOR_MOA)
                .build();

        FacilityProcessStatus actualResult = mapper.toFacilityProcessStatus(facilityProcessStatusCreationDTO);

        assertThat(actualResult.getFacilityId()).isEqualTo(expectedResult.getFacilityId());
        assertThat(actualResult.getChargingYear()).isEqualTo(expectedResult.getChargingYear());
        assertThat(actualResult.getMoaType()).isEqualTo(expectedResult.getMoaType());
        assertThat(actualResult.getRunId()).isEqualTo(expectedResult.getRunId());
    }

    @Test
    void toSubsistenceFeesRunMoaDetailsDTO() {
        final LocalDateTime date = LocalDateTime.now();
        final FileInfoDTO fileInfoDTO = FileInfoDTO.builder().build();
        final SubsistenceFeesMoaDetails moaDetails = new SubsistenceFeesMoaDetails(1L, "CCACM1200", MoaType.TARGET_UNIT_MOA, 1L,
                BigDecimal.ZERO, BigDecimal.valueOf(1000L), "uuid", date, BigDecimal.ZERO, BigDecimal.valueOf(1000L), 10L, 1L, 1L);
        final SubsistenceFeesMoaDetailsDTO expectedDTO = new SubsistenceFeesMoaDetailsDTO(1L, "CCACM1200", "name", "businessId", fileInfoDTO,
                date, PaymentStatus.PAID, 10L, 1L, BigDecimal.ZERO, BigDecimal.valueOf(1000L), BigDecimal.valueOf(1000L), BigDecimal.ZERO, 1L);

        // invoke
        SubsistenceFeesMoaDetailsDTO resultDTO = mapper.toSubsistenceFeesMoaDetailsDTO(moaDetails, "name", "businessId", fileInfoDTO);

        assertThat(resultDTO).isEqualTo(expectedDTO);
    }

    @Test
    void toReceivedAmountHistoryDTO() {
        String submitter = "SubmitterFullName";
        LocalDateTime submissionDate = LocalDateTime.of(2020, 1, 1, 1, 1);
        Long moaId = 1L;
        BigDecimal previousReceivedAmount = BigDecimal.valueOf(5000);
        BigDecimal transactionAmount = BigDecimal.valueOf(185);
        String comments = "bla bla bla";
        UUID fileEvidenceUuid1 = UUID.randomUUID();
        String evidenceFileName1 = "EvidenceFile1";
        UUID fileEvidenceUuid2 = UUID.randomUUID();
        String evidenceFileName2 = "EvidenceFile2";
        Map<UUID, String> evidenceFiles = Map.of(fileEvidenceUuid1, evidenceFileName1, fileEvidenceUuid2, evidenceFileName2);
        SubsistenceFeesMoaReceivedAmountHistory receivedAmountHistory = SubsistenceFeesMoaReceivedAmountHistory.builder()
                .submitterId("submitterId")
                .submitter(submitter)
                .subsistenceFeesMoa(SubsistenceFeesMoa.builder().id(moaId).build())
                .submissionDate(submissionDate)
                .payload(SubsistenceFeesMoaReceivedAmountHistoryPayload.builder()
                        .previousReceivedAmount(previousReceivedAmount)
                        .transactionAmount(transactionAmount)
                        .comments(comments)
                        .evidenceFiles(evidenceFiles)
                        .build())
                .build();
        SubsistenceFeesMoaReceivedAmountHistoryDTO expectedDTO = SubsistenceFeesMoaReceivedAmountHistoryDTO.builder()
                .submitter(submitter)
                .submissionDate(submissionDate)
                .transactionAmount(transactionAmount)
                .comments(comments)
                .evidenceFiles(evidenceFiles)
                .build();

        SubsistenceFeesMoaReceivedAmountHistoryDTO receivedAmountHistoryDTO = mapper.toReceivedAmountHistoryDTO(receivedAmountHistory);

        assertThat(receivedAmountHistoryDTO).isEqualTo(expectedDTO);
    }

    @Test
    void toSubsistenceFeesMoaReceivedAmountInfoDTO() {
        String submitter = "SubmitterFullName";
        LocalDateTime submissionDate = LocalDateTime.of(2020, 1, 1, 1, 1);
        BigDecimal transactionAmount = BigDecimal.valueOf(185);
        String comments = "bla bla bla";
        UUID fileEvidenceUuid1 = UUID.randomUUID();
        String evidenceFileName1 = "EvidenceFile1";
        UUID fileEvidenceUuid2 = UUID.randomUUID();
        String evidenceFileName2 = "EvidenceFile2";
        Map<UUID, String> evidenceFiles = Map.of(fileEvidenceUuid1, evidenceFileName1, fileEvidenceUuid2, evidenceFileName2);

        SubsistenceFeesMoaReceivedAmountHistoryDTO moaReceivedAmountHistoryDTO = SubsistenceFeesMoaReceivedAmountHistoryDTO.builder()
                .submitter(submitter)
                .submissionDate(submissionDate)
                .transactionAmount(transactionAmount)
                .comments(comments)
                .evidenceFiles(evidenceFiles)
                .build();

        SubsistenceFeesMoaDetailsDTO sfrMoaDetailsDTO = new SubsistenceFeesMoaDetailsDTO(1L,
                "CCACM1200", "ADS", "name",
                null, LocalDateTime.now(), PaymentStatus.AWAITING_PAYMENT, 10L, 10L,
                BigDecimal.valueOf(1000L), BigDecimal.valueOf(1000L), BigDecimal.valueOf(2000L), BigDecimal.valueOf(185L), 1L);

        SubsistenceFeesMoaReceivedAmountInfoDTO expectedDTO = SubsistenceFeesMoaReceivedAmountInfoDTO.builder()
                .transactionId("CCACM1200")
                .name("name")
                .businessId("ADS")
                .paymentStatus(PaymentStatus.AWAITING_PAYMENT)
                .receivedAmount(BigDecimal.valueOf(2000L))
                .currentTotalAmount(BigDecimal.valueOf(1000L))
                .receivedAmountHistoryList(List.of(moaReceivedAmountHistoryDTO))
                .build();

        // invoke
        SubsistenceFeesMoaReceivedAmountInfoDTO subsistenceFeesMoaReceivedAmountInfoDTO = mapper.toSubsistenceFeesMoaReceivedAmountInfoDTO(sfrMoaDetailsDTO, List.of(moaReceivedAmountHistoryDTO));

        // verify
        assertThat(subsistenceFeesMoaReceivedAmountInfoDTO).isEqualTo(expectedDTO);
    }

    @Test
    void toSubsistenceFeesMoaFacilityMarkingStatusHistoryDTO() {
        String submitter = "SubmitterFullName";
        LocalDateTime submissionDate = LocalDateTime.of(2025, 1, 1, 1, 1);

        SubsistenceFeesMoaFacilityMarkingStatusHistory facilityMarkingStatusHistory = SubsistenceFeesMoaFacilityMarkingStatusHistory.builder()
                .submitterId("submitterId")
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .submitter(submitter)
                .submissionDate(submissionDate)
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO expectedDTO = SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO.builder()
                .submissionDate(submissionDate)
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .submitter(submitter)
                .build();

        // invoke
        SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO subsistenceFeesMoaFacilityMarkingStatusHistoryDTO = mapper.toSubsistenceFeesMoaFacilityMarkingStatusHistoryDTO(facilityMarkingStatusHistory);

        // verify
        assertThat(subsistenceFeesMoaFacilityMarkingStatusHistoryDTO).isEqualTo(expectedDTO);
    }

    @Test
    void toSubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO() {
        String submitter = "SubmitterFullName";
        String facilityBusinessId = "FACILITY_ID";
        String facilityName = "FACILITY_NAME";
        LocalDateTime submissionDate = LocalDateTime.of(2025, 1, 1, 1, 1);

        SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO subsistenceFeesMoaFacilityMarkingStatusHistoryDTO = SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO.builder()
                .submissionDate(submissionDate)
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .submitter(submitter)
                .build();

        FacilityBaseInfoDTO facilityBaseInfo = FacilityBaseInfoDTO.builder()
                .siteName(facilityName)
                .facilityBusinessId(facilityBusinessId)
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO expectedDTO = SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO.builder()
                .markingStatusHistoryList(List.of(subsistenceFeesMoaFacilityMarkingStatusHistoryDTO))
                .siteName(facilityName)
                .facilityBusinessId(facilityBusinessId)
                .build();

        // invoke
        SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO subsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO =
                mapper.toSubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO(facilityBaseInfo, List.of(subsistenceFeesMoaFacilityMarkingStatusHistoryDTO));

        // verify
        assertThat(subsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO).isEqualTo(expectedDTO);
    }
}
