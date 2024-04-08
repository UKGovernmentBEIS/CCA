package uk.gov.cca.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.payment.domain.PaymentCancelledRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.payment.domain.PaymentProcessedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.rde.domain.RdeDecisionForcedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.rde.domain.RdeRejectedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.rde.domain.RdeSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RfiResponseSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RfiSubmittedRequestActionPayload;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Schema(discriminatorMapping = {
        @DiscriminatorMapping(schema = RfiResponseSubmittedRequestActionPayload.class, value = "RFI_RESPONSE_SUBMITTED_PAYLOAD"),
        @DiscriminatorMapping(schema = RfiSubmittedRequestActionPayload.class, value = "RFI_SUBMITTED_PAYLOAD"),

        @DiscriminatorMapping(schema = RdeDecisionForcedRequestActionPayload.class, value = "RDE_DECISION_FORCED_PAYLOAD"),
        @DiscriminatorMapping(schema = RdeRejectedRequestActionPayload.class, value = "RDE_REJECTED_PAYLOAD"),
        @DiscriminatorMapping(schema = RdeSubmittedRequestActionPayload.class, value = "RDE_SUBMITTED_PAYLOAD"),

        @DiscriminatorMapping(schema = PaymentProcessedRequestActionPayload.class, value = "PAYMENT_MARKED_AS_PAID_PAYLOAD"),
        @DiscriminatorMapping(schema = PaymentProcessedRequestActionPayload.class, value = "PAYMENT_MARKED_AS_RECEIVED_PAYLOAD"),
        @DiscriminatorMapping(schema = PaymentProcessedRequestActionPayload.class, value = "PAYMENT_COMPLETED_PAYLOAD"),
        @DiscriminatorMapping(schema = PaymentCancelledRequestActionPayload.class, value = "PAYMENT_CANCELLED_PAYLOAD")
},
        discriminatorProperty = "payloadType")

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RfiResponseSubmittedRequestActionPayload.class, name = "RFI_RESPONSE_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = RfiSubmittedRequestActionPayload.class, name = "RFI_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = RdeDecisionForcedRequestActionPayload.class, name = "RDE_DECISION_FORCED_PAYLOAD"),
        @JsonSubTypes.Type(value = RdeRejectedRequestActionPayload.class, name = "RDE_REJECTED_PAYLOAD"),

        @JsonSubTypes.Type(value = RdeSubmittedRequestActionPayload.class, name = "RDE_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PaymentProcessedRequestActionPayload.class, name = "PAYMENT_MARKED_AS_PAID_PAYLOAD"),
        @JsonSubTypes.Type(value = PaymentProcessedRequestActionPayload.class, name = "PAYMENT_MARKED_AS_RECEIVED_PAYLOAD"),
        @JsonSubTypes.Type(value = PaymentProcessedRequestActionPayload.class, name = "PAYMENT_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = PaymentCancelledRequestActionPayload.class, name = "PAYMENT_CANCELLED_PAYLOAD"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestActionPayload {

    private RequestActionPayloadType payloadType;

    @JsonIgnore
    public Map<UUID, String> getAttachments() {
        return Collections.emptyMap();
    }

    @JsonIgnore
    public Map<UUID, String> getFileDocuments() {
        return Collections.emptyMap();
    }

}
