package uk.gov.cca.api.workflow.request.flow.common.jsonprovider;

import java.util.List;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.domain.BuyOutSurplusRunCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationSubmitApplicationCreateActionPayload;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestCreateActionPayloadType.BUY_OUT_SURPLUS_RUN_CREATE_ACTION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestCreateActionPayloadType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CREATE_ACTION_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestCreateActionPayloadType.TARGET_UNIT_ACCOUNT_CREATION_SUBMIT_PAYLOAD;

@Component
public class RequestCreateActionPayloadTypesProvider implements JsonSubTypesProvider {

	@Override
	public List<NamedType> getTypes() {
		return List.of(
				// Target Unit Account Creation
				new NamedType(TargetUnitAccountCreationSubmitApplicationCreateActionPayload.class, TARGET_UNIT_ACCOUNT_CREATION_SUBMIT_PAYLOAD),
				// Buy Out Surplus
				new NamedType(BuyOutSurplusRunCreateActionPayload.class, BUY_OUT_SURPLUS_RUN_CREATE_ACTION_PAYLOAD),
				// Performance Data Facility Digital Form
				new NamedType(PerformanceDataFacilityDigitalFormRequestCreateActionPayload.class, PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CREATE_ACTION_PAYLOAD)
		);
	}

}
