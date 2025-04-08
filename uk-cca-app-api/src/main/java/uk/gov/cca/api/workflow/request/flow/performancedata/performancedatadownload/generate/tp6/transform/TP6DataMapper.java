package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.tp6.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.tp6.domain.TP6Data;
import uk.gov.netz.api.common.config.MapperConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface TP6DataMapper {

    @Mapping(target = "sector", source = "sectorAcronym")
    @Mapping(target = "targetType", source = "underlyingAgreement.underlyingAgreementContainer.underlyingAgreement.targetPeriod6Details.targetComposition.agreementCompositionType.description")
    @Mapping(target = "measurementUnit", source = "underlyingAgreement.underlyingAgreementContainer.underlyingAgreement.targetPeriod6Details.targetComposition.measurementType.unit")
    @Mapping(target = "throughputUnit", source = "underlyingAgreement", qualifiedByName = "getThroughputUnit")
    @Mapping(target = "baselineDate", source = "underlyingAgreement.underlyingAgreementContainer.underlyingAgreement.targetPeriod6Details.baselineData.baselineDate")
    @Mapping(target = "baselineEnergy", source = "underlyingAgreement.underlyingAgreementContainer.underlyingAgreement.targetPeriod6Details.baselineData.energy")
    @Mapping(target = "baselineThroughput", source = "underlyingAgreement.underlyingAgreementContainer.underlyingAgreement.targetPeriod6Details.baselineData.throughput")
    @Mapping(target = "improvement", source = "underlyingAgreement.underlyingAgreementContainer.underlyingAgreement.targetPeriod6Details.targets.improvement", qualifiedByName = "percentageToDecimal")
    @Mapping(target = "targetUnitId", source = "targetUnitDetails.businessId")
    @Mapping(target = "operatorName", source = "targetUnitDetails.name")
    @Mapping(target = "numOfFacilities", expression = "java(underlyingAgreement.getUnderlyingAgreementContainer().getUnderlyingAgreement().getFacilities().size())")
    @Mapping(target = "targetPeriod", constant = "TP6")
    @Mapping(target = "reportVersion", source = "reportVersion")
    @Mapping(target = "bankedSurplusFromPreviousTP", constant = "0")
    TP6Data toTP6Data(String sectorAcronym,
                      UnderlyingAgreementDTO underlyingAgreement,
                      TargetUnitAccountDetailsDTO targetUnitDetails,
                      int reportVersion,
                      @Context PerformanceDataSubmissionType currentSubmissionType,
                      @Context PerformanceDataContainer lastUploaded);

    @AfterMapping
    default void setSecondaryData(@MappingTarget TP6Data tp6Data,
                                  @Context PerformanceDataContainer lastUploadedReport,
                                  @Context PerformanceDataSubmissionType currentSubmissionType) {

        if (currentSubmissionType.equals(PerformanceDataSubmissionType.SECONDARY)) {
            Optional.ofNullable(lastUploadedReport)
                    .ifPresentOrElse(
                            data -> {
                                tp6Data.setPreviousBuyOutAfterSurplus(data.getSurplusBuyOutDetermination().getPriBuyOutCarbon());
                                tp6Data.setPreviousSurplusUsed(data.getSurplusBuyOutDetermination().getSurplusUsed());
                                tp6Data.setSurplusGainedInTP(data.getSurplusBuyOutDetermination().getSurplusGained());
                            },
                            () -> {
                                tp6Data.setPreviousBuyOutAfterSurplus(BigDecimal.ZERO);
                                tp6Data.setPreviousSurplusUsed(BigDecimal.ZERO);
                                tp6Data.setSurplusGainedInTP(BigDecimal.ZERO);
                            }
                    );
        }
    }

    @Named("percentageToDecimal")
    default BigDecimal percentageToDecimal(BigDecimal improvement) {
        if (improvement == null) {
            return BigDecimal.ZERO;
        }
        return improvement
                .movePointLeft(2)
                .setScale(9, RoundingMode.HALF_UP);
    }

    @Named("getThroughputUnit")
    default String getThroughputUnit(UnderlyingAgreementDTO underlyingAgreement) {
        TargetComposition targetComposition = underlyingAgreement.getUnderlyingAgreementContainer()
                .getUnderlyingAgreement()
                .getTargetPeriod6Details()
                .getTargetComposition();

        if (ObjectUtils.isEmpty(targetComposition.getThroughputUnit())
                && !AgreementCompositionType.NOVEM.equals(targetComposition.getAgreementCompositionType())) {
            return underlyingAgreement.getUnderlyingAgreementContainer().getSectorThroughputUnit();
        }

        return targetComposition.getThroughputUnit();
    }

}


