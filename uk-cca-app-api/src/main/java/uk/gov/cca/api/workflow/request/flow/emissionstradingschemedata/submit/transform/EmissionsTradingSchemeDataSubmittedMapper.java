package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmissionDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmittedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {BigDecimal.class, CcaRequestActionPayloadType.class})
public interface EmissionsTradingSchemeDataSubmittedMapper {

    @Mapping(target = "payloadType", expression = "java(CcaRequestActionPayloadType.EMISSIONS_TRADING_SCHEME_DATA_SUBMITTED_PAYLOAD)")
    @Mapping(target = "details", expression = "java(toEmissionsTradingSchemeDataSubmissionDetails(request, requestPayload))")
    EmissionsTradingSchemeDataSubmittedRequestActionPayload toEmissionsTradingSchemeDataSubmittedRequestActionPayload(Request request, EmissionsTradingSchemeDataRequestPayload requestPayload, EmissionsTradingSchemeDataContainer performanceData);

    @Mapping(target = "targetPeriodYear", source = "requestPayload.targetYear")
    EmissionsTradingSchemeDataSubmissionDetails toEmissionsTradingSchemeDataSubmissionDetails(Request request, EmissionsTradingSchemeDataRequestPayload requestPayload);

    default EmissionsTradingSchemeDataSubmittedRequestActionPayload toEmissionsTradingSchemeDataSubmittedRequestActionPayload(Request request, EmissionsTradingSchemeDataContainer performanceData) {
        EmissionsTradingSchemeDataRequestPayload requestPayload =
                (EmissionsTradingSchemeDataRequestPayload) request.getPayload();

        return toEmissionsTradingSchemeDataSubmittedRequestActionPayload(request, requestPayload, performanceData);
    }
}
