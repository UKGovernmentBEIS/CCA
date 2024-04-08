package uk.gov.cca.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestPayloadType;

import java.math.BigDecimal;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestPayload implements Payload {

    private RequestPayloadType payloadType;

    private String operatorAssignee;

    private String regulatorAssignee;
    
    private String verifierAssignee;

    private String supportingOperator;

    private String supportingRegulator;

    private String regulatorReviewer;
    
    private Boolean paymentCompleted;
    
    private BigDecimal paymentAmount;
}
