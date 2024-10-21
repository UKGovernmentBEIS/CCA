package uk.gov.cca.api.workflow.request.application.item.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.application.item.domain.dto.ItemDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ItemTargetUnitDTO extends ItemDTO {

	@JsonUnwrapped
    private ItemTargetUnitAccountDTO account;
}
