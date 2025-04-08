package uk.gov.cca.api.subsistencefees.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.subsistencefees.config.SubsistenceFeesConfig;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacility;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaTargetUnit;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesRunRepository;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunUpdateService {

    private final SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;
    private final SubsistenceFeesRunRepository subsistenceFeesRunRepository;
    private final SubsistenceFeesMoaRepository subsistenceFeesMoaRepository;
    private final SubsistenceFeesConfig subsistenceFeesConfig;

    @Transactional
    public long createSubsistenceFeesRun(String businessId, CompetentAuthorityEnum competentAuthority, Year chargingYear) {
        final SubsistenceFeesRun subsistenceFeesRun = SubsistenceFeesRun.builder()
                .businessId(businessId)
                .competentAuthority(competentAuthority)
                .chargingYear(chargingYear)
                .initialTotalAmount(BigDecimal.ZERO)
                .build();
        return subsistenceFeesRunRepository.save(subsistenceFeesRun).getId();
    }

    @Transactional
    public void finalizeSubsistenceFeesRun(long runId) {
        final SubsistenceFeesRun subsistenceFeesRun = subsistenceFeesRunQueryService.getSubsistenceFeesRunById(runId);
        subsistenceFeesRun.setSubmissionDate(LocalDateTime.now());
        subsistenceFeesRun.setInitialTotalAmount(subsistenceFeesRun.getSubsistenceFeesMoas()
                .stream()
                .map(SubsistenceFeesMoa::getInitialTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        subsistenceFeesRunRepository.save(subsistenceFeesRun);
    }

    @Transactional
    public void deleteSubsistenceFeesRun(Long id) {
        subsistenceFeesRunRepository.deleteById(id);
    }

    @Transactional
    public void persistMoa(Long sectorAssociationId, String transactionId, Long runId, MoaType moaType, List<EligibleFacilityDTO> facilities, String documentUuid) {

        final SubsistenceFeesRun subsistenceFeesRun = subsistenceFeesRunQueryService.getSubsistenceFeesRunById(runId);

        final SubsistenceFeesMoa subsistenceFeesMoa = SubsistenceFeesMoa.builder()
                .moaType(moaType)
                .subsistenceFeesRun(subsistenceFeesRun)
                .fileDocumentUuid(documentUuid)
                .resourceId(sectorAssociationId)
                .transactionId(transactionId)
                .initialTotalAmount(subsistenceFeesConfig.getFacilityFee().multiply(BigDecimal.valueOf(facilities.size())))
                .regulatorReceivedAmount(BigDecimal.ZERO)
                .submissionDate(LocalDateTime.now())
                .build();

        setSubsistenceFeesMoaTargetUnits(subsistenceFeesMoa, facilities);

        subsistenceFeesMoaRepository.save(subsistenceFeesMoa);
    }

    private void setSubsistenceFeesMoaTargetUnits(SubsistenceFeesMoa subsistenceFeesMoa, List<EligibleFacilityDTO> facilities) {
        final Map<Long, List<EligibleFacilityDTO>> facilitiesByAccount =
                facilities.stream().collect(groupingBy(EligibleFacilityDTO::getAccountId));

        final BigDecimal facilityFee = subsistenceFeesConfig.getFacilityFee();

        facilitiesByAccount.forEach((accountId, accountFacilities) -> {
            SubsistenceFeesMoaTargetUnit subsistenceFeesMoaTargetUnit = SubsistenceFeesMoaTargetUnit.builder()
                    .accountId(accountId)
                    .initialTotalAmount(facilityFee.multiply(BigDecimal.valueOf(accountFacilities.size())))
                    .build();

            accountFacilities.forEach(facility -> {
                SubsistenceFeesMoaFacility subsistenceFeesMoaFacility = SubsistenceFeesMoaFacility.builder()
                        .facilityId(facility.getId())
                        .initialAmount(facilityFee)
                        .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                        .build();
                subsistenceFeesMoaTargetUnit.addSubsistenceFeesMoaFacility(subsistenceFeesMoaFacility);
            });

            subsistenceFeesMoa.addSubsistenceFeesMoaTargetUnit(subsistenceFeesMoaTargetUnit);
        });
    }
}
