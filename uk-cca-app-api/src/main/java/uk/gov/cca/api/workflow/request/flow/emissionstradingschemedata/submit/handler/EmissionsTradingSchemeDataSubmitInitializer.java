package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.service.EmissionsTradingSchemeDataService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.transform.EmissionsTradingSchemeDataContainerMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.time.Year;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmissionsTradingSchemeDataSubmitInitializer implements InitializeRequestTaskHandler {

    private final EmissionsTradingSchemeDataService emissionsTradingSchemeDataService;

    private static final EmissionsTradingSchemeDataContainerMapper CONTAINER_MAPPER = Mappers.getMapper(EmissionsTradingSchemeDataContainerMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        EmissionsTradingSchemeDataRequestPayload requestPayload =
                (EmissionsTradingSchemeDataRequestPayload) request.getPayload();
        MeasurementType measurementType = requestPayload.getMeasurementType();
        Year targetYear = requestPayload.getTargetYear();

        // load energy fuel details if present and the measurement type has not been changed
        Optional<EmissionsTradingSchemeDataContainer> existingData =
                emissionsTradingSchemeDataService.getEmissionsTradingSchemeData(requestPayload.getFacilityId(), targetYear);
        EmissionsTradingSchemeDataInputDetails emissionsTradingSchemeDataInputDetails =
                existingData.isPresent() && existingData.get().getMeasurementType() == measurementType
                        ? CONTAINER_MAPPER.toEmissionsTradingSchemeDataInputDetails(existingData.get())
                        : null;

        return EmissionsTradingSchemeDataSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_PAYLOAD)
                .targetYear(targetYear)
                .uketsId(requestPayload.getUketsId())
                .measurementType(measurementType)
                .emissionsTradingSchemeDataInputDetails(emissionsTradingSchemeDataInputDetails)
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.EMISSIONS_TRADING_SCHEME_DATA_SUBMIT);
    }
}
