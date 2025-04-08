package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.utils;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.FacilitiesTemplateData;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaTemplateData;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@UtilityClass
public class SubsistenceFeesUtility {

    public MoaTemplateData constructMoaTemplateData(final String transactionId, final int numOfFacilities, final BigDecimal facilityFee) {
        return MoaTemplateData.builder()
                .transactionId(transactionId)
                .moaAmount(facilityFee.multiply(BigDecimal.valueOf(numOfFacilities)).setScale(2, RoundingMode.UNNECESSARY))
                .build();
    }

    public List<FacilitiesTemplateData> constructSectorMoaTemplateLineItems(final List<EligibleFacilityDTO> facilities, int chargingYear, final BigDecimal facilityFee) {

        final Map<String, List<EligibleFacilityDTO>> facilitiesByAccount =
                facilities.stream().collect(groupingBy(EligibleFacilityDTO::getTargetUnitBusinessId, TreeMap::new,
                        Collectors.collectingAndThen(
                                toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(EligibleFacilityDTO::getFacilityId))
                                        .toList())));

        final List<FacilitiesTemplateData> lineItems = new ArrayList<>();

        AtomicInteger accountLineIdx = new AtomicInteger(1);
        facilitiesByAccount.forEach((businessId, accountFacilities) -> {
            EligibleFacilityDTO accountFacility = accountFacilities.getFirst();
            String operatorName = accountFacility.getOperatorName();
            BigDecimal amount = facilityFee.multiply(BigDecimal.valueOf(accountFacilities.size()));

            lineItems.add(FacilitiesTemplateData.builder()
                    .groupId(String.format("%03d", accountLineIdx.get()))
                    .id(businessId)
                    .name(operatorName)
                    .amount(amount.setScale(2, RoundingMode.UNNECESSARY))
                    .build());

            accountFacilities.forEach(facility -> lineItems.add(
                    FacilitiesTemplateData.builder()
                            .id(facility.getFacilityId())
                            .name(facility.getSiteName())
                            .period(String.valueOf(chargingYear))
                            .build()));

            accountLineIdx.getAndIncrement();
        });

        return lineItems;
    }

    public List<FacilitiesTemplateData> constructTargetUnitMoATemplateLineItems(final List<EligibleFacilityDTO> facilities, int chargingYear, final BigDecimal facilityFee) {

        final List<FacilitiesTemplateData> lineItems = new ArrayList<>();
        AtomicInteger accountLineIdx = new AtomicInteger(1);
        facilities.stream()
                .sorted(Comparator.comparing(EligibleFacilityDTO::getFacilityId))
                .forEach(facility -> {
                    lineItems.add(FacilitiesTemplateData.builder()
                            .groupId(String.format("%03d", accountLineIdx.get()))
                            .id(facility.getFacilityId())
                            .name(facility.getSiteName())
                            .amount(facilityFee.setScale(2, RoundingMode.UNNECESSARY))
                            .period(String.valueOf(chargingYear))
                            .build());
                    accountLineIdx.getAndIncrement();
                });

        return lineItems;
    }

    public TemplateParams constructSectorMoaTemplateParams(final String transactionId, final List<EligibleFacilityDTO> facilities, final int chargingYear, final BigDecimal facilityFee) {
        final TemplateParams templateParams = new TemplateParams();
        templateParams.getParams().put("moaData", constructMoaTemplateData(transactionId, facilities.size(), facilityFee));
        templateParams.getParams().put("lineItems", constructSectorMoaTemplateLineItems(facilities, chargingYear, facilityFee));
        return templateParams;
    }

    public TemplateParams constructTargetUnitMoaTemplateParams(final String transactionId, final List<EligibleFacilityDTO> facilities, final int chargingYear, final BigDecimal facilityFee) {
        final TemplateParams templateParams = new TemplateParams();
        templateParams.getParams().put("moaData", constructMoaTemplateData(transactionId, facilities.size(), facilityFee));
        templateParams.getParams().put("lineItems", constructTargetUnitMoATemplateLineItems(facilities, chargingYear, facilityFee));
        return templateParams;
    }
}
