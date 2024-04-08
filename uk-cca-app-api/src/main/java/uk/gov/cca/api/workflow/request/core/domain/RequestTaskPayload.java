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
import org.springframework.util.CollectionUtils;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.payment.domain.PaymentConfirmRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.payment.domain.PaymentMakeRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.payment.domain.PaymentTrackRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.rde.domain.RdeResponseRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskPayload;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Schema(discriminatorMapping = {
        @DiscriminatorMapping(schema = RfiResponseSubmitRequestTaskPayload.class, value = "RFI_RESPONSE_SUBMIT_PAYLOAD"),

        @DiscriminatorMapping(schema = RdeForceDecisionRequestTaskPayload.class, value = "RDE_WAIT_FOR_RESPONSE_PAYLOAD"),
        @DiscriminatorMapping(schema = RdeResponseRequestTaskPayload.class, value = "RDE_RESPONSE_SUBMIT_PAYLOAD"),

        @DiscriminatorMapping(schema = PaymentMakeRequestTaskPayload.class, value = "PAYMENT_MAKE_PAYLOAD"),
        @DiscriminatorMapping(schema = PaymentTrackRequestTaskPayload.class, value = "PAYMENT_TRACK_PAYLOAD"),
        @DiscriminatorMapping(schema = PaymentConfirmRequestTaskPayload.class, value = "PAYMENT_CONFIRM_PAYLOAD"),
},
        discriminatorProperty = "payloadType"
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RfiResponseSubmitRequestTaskPayload.class, name = "RFI_RESPONSE_SUBMIT_PAYLOAD"),

        @JsonSubTypes.Type(value = RdeForceDecisionRequestTaskPayload.class, name = "RDE_WAIT_FOR_RESPONSE_PAYLOAD"),
        @JsonSubTypes.Type(value = RdeResponseRequestTaskPayload.class, name = "RDE_RESPONSE_SUBMIT_PAYLOAD"),

        @JsonSubTypes.Type(value = PaymentMakeRequestTaskPayload.class, name = "PAYMENT_MAKE_PAYLOAD"),
        @JsonSubTypes.Type(value = PaymentTrackRequestTaskPayload.class, name = "PAYMENT_TRACK_PAYLOAD"),
        @JsonSubTypes.Type(value = PaymentConfirmRequestTaskPayload.class, name = "PAYMENT_CONFIRM_PAYLOAD"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestTaskPayload {

    private RequestTaskPayloadType payloadType;

    @JsonIgnore
    public Map<UUID, String> getAttachments() {
        return Collections.emptyMap();
    }

    @JsonIgnore
    public Set<UUID> getReferencedAttachmentIds() {
        return Collections.emptySet();
    }

    @JsonIgnore
    public void removeAttachments(final Collection<UUID> uuids) {

        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        this.getAttachments().keySet().removeIf(uuids::contains);
    }
}
