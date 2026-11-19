package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmissionsTradingSchemeDataSubmitValidator {

    private final EmissionsTradingSchemeDataInputDataValidator inputDataValidator;
    private final EmissionsTradingSchemeDataCalculatedDataValidator calculatedDataValidator;

    public void validate(final RequestTask requestTask) {
        EmissionsTradingSchemeDataSubmitRequestTaskPayload requestTaskPayload = (EmissionsTradingSchemeDataSubmitRequestTaskPayload) requestTask.getPayload();
        EmissionsTradingSchemeDataInputDetails dataInputDetails = requestTaskPayload.getEmissionsTradingSchemeDataInputDetails();
        EmissionsTradingSchemeDataInputEnergyFuelDetails energyFuelDetails = dataInputDetails.getEnergyFuelDetails();
        MeasurementType measurementType = requestTaskPayload.getMeasurementType();

        // validate data
        BusinessValidationResult businessValidationResult = inputDataValidator.validateInputData(dataInputDetails);
        if (!businessValidationResult.isValid()) {
            throw new BusinessException(CcaErrorCode.INVALID_EMISSIONS_TRADING_SCHEME_DATA_SUBMIT, businessValidationResult.getViolations());
        }

        // validate input calculated data
        List<BusinessValidationResult> validationInputCalculatedResults =
                calculatedDataValidator.validateInputCalculatedData(energyFuelDetails, measurementType);
        boolean isInputCalculationValid = validationInputCalculatedResults.stream().allMatch(BusinessValidationResult::isValid);
        if (!isInputCalculationValid) {
            throw new BusinessException(CcaErrorCode.INVALID_EMISSIONS_TRADING_SCHEME_DATA_SUBMIT, ValidatorHelper.extractViolations(validationInputCalculatedResults));
        }

        // validate calculated results
        List<BusinessValidationResult> validationCalculatedResults =
                calculatedDataValidator.validateCalculatedResults(energyFuelDetails, measurementType);
        boolean isCalculationValid = validationCalculatedResults.stream().allMatch(BusinessValidationResult::isValid);
        if (!isCalculationValid) {
            throw new BusinessException(CcaErrorCode.INVALID_EMISSIONS_TRADING_SCHEME_DATA_CALCULATED_RESULTS, ValidatorHelper.extractViolations(validationCalculatedResults));
        }
    }
}
