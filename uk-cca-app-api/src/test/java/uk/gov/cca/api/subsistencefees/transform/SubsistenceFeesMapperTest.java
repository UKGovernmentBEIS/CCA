package uk.gov.cca.api.subsistencefees.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.FacilityProcessStatus;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.dto.FacilityProcessStatusCreationDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaTargetUnitSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaDetails;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunMoaDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunSearchResultInfo;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

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
}
