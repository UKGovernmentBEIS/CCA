package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementVariationDetails {

    @NotNull
    @Size(max = 10000)
    private String reason;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<@NotNull UnderlyingAgreementVariationModificationType> modifications = new ArrayList<>();

}
