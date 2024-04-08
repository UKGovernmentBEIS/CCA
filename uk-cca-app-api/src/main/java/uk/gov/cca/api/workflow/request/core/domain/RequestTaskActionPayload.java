package uk.gov.cca.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.cca.api.workflow.request.flow.payment.domain.PaymentCancelRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.payment.domain.PaymentMarkAsReceivedRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.rde.domain.RdeResponseSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.rde.domain.RdeSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RfiSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = RfiSubmitRequestTaskActionPayload.class, name = "RFI_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = RfiResponseSubmitRequestTaskActionPayload.class, name = "RFI_RESPONSE_SUBMIT_PAYLOAD"),

    @JsonSubTypes.Type(value = RdeSubmitRequestTaskActionPayload.class, name = "RDE_SUBMIT_PAYLOAD"),
    @JsonSubTypes.Type(value = RdeForceDecisionRequestTaskActionPayload.class, name = "RDE_FORCE_DECISION_PAYLOAD"),
    @JsonSubTypes.Type(value = RdeResponseSubmitRequestTaskActionPayload.class, name = "RDE_RESPONSE_SUBMIT_PAYLOAD"),
    
    @JsonSubTypes.Type(value = PaymentMarkAsReceivedRequestTaskActionPayload.class, name = "PAYMENT_MARK_AS_RECEIVED_PAYLOAD"),
    @JsonSubTypes.Type(value = PaymentCancelRequestTaskActionPayload.class, name = "PAYMENT_CANCEL_PAYLOAD"),

    @JsonSubTypes.Type(value = RequestTaskActionEmptyPayload.class, name = "EMPTY_PAYLOAD"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestTaskActionPayload {

    private RequestTaskActionPayloadType payloadType;
}
