package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacility;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmissionDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilitySubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {BigDecimal.class, CcaRequestActionPayloadType.class})
public interface PerformanceDataFacilitySubmittedMapper {

    @Mapping(target = "data", expression = "java(toPerformanceDataFacilityContainer(requestPayload.getReferenceData().getBaselineAndTargets(), requestPayload.getPerformanceData()))")
    @Mapping(target = "facilityId", source = "requestPayload.facility.id")
    PerformanceDataFacility toPerformanceDataFacility(PerformanceDataFacilityRequestPayload requestPayload);

    @Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.PERFORMANCE_DATA_FACILITY_SUBMITTED_PAYLOAD)")
    @Mapping(target = "details", expression = "java(toPerformanceDataFacilitySubmissionDetails(request, requestPayload))")
    @Mapping(target = "performanceData", expression = "java(toPerformanceDataFacilityContainer(requestPayload.getReferenceData().getBaselineAndTargets(), requestPayload.getPerformanceData()))")
    PerformanceDataFacilitySubmittedRequestActionPayload toPerformanceDataFacilitySubmittedRequestActionPayload(Request request, PerformanceDataFacilityRequestPayload requestPayload);

    PerformanceDataFacilitySubmissionDetails toPerformanceDataFacilitySubmissionDetails(Request request, PerformanceDataFacilityRequestPayload requestPayload);

    default PerformanceDataFacilitySubmittedRequestActionPayload toPerformanceDataFacilitySubmittedRequestActionPayload(Request request) {
        PerformanceDataFacilityRequestPayload requestPayload =
                (PerformanceDataFacilityRequestPayload) request.getPayload();

        return toPerformanceDataFacilitySubmittedRequestActionPayload(request, requestPayload);
    }

    PerformanceDataFacilityContainer toPerformanceDataFacilityContainer(PerformanceDataFacilityBaselineAndTargets baselineAndTargets, PerformanceDataFacilityInputData data);

    @AfterMapping
    default void setFuels(@MappingTarget PerformanceDataFacilityContainer container, PerformanceDataFacilityBaselineAndTargets baselineTargets,
                          PerformanceDataFacilityInputData data) {
        List<PerformanceDataFacilityFuel> fuels = new ArrayList<>();

        // Transform standards fuels
        data.getEnergyFuelDetails().getStandardFuels().entrySet().stream()
                .map(entry ->
                        toPerformanceDataFacilityFuel(entry.getKey(), entry.getValue(), baselineTargets.getMeasurementType()))
                .forEach(fuels::add);

        // Transform non standards fuels
        data.getEnergyFuelDetails().getNonStandardFuels().stream()
                .map(this::toPerformanceDataFacilityFuel)
                .forEach(fuels::add);

        container.getEnergyFuelDetails().setFuels(fuels);
    }

    @Mapping(target = "primaryConversionFactor", expression = "java(BigDecimal.ONE)")
    PerformanceDataFacilityFuel toPerformanceDataFacilityFuel(PerformanceDataFacilityNonStandardFuel nonStandardFuel);

    @Mapping(target = "name", expression = "java(fixedConversionFactorCode.getDescription())")
    @Mapping(target = "conversionFactor", expression = "java(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(fixedConversionFactorCode, measurementType))")
    @Mapping(target = "primaryConversionFactor", expression = "java(fixedConversionFactorCode.getPrimaryFactor())")
    PerformanceDataFacilityFuel toPerformanceDataFacilityFuel(PerformanceDataFacilityFixedConversionFactor fixedConversionFactorCode,
                                                              PerformanceDataFacilityFuelEnergyConsumption standardFuel,
                                                              MeasurementType measurementType);
}
