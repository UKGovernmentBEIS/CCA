package uk.gov.cca.api.workflow.request.core.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountReferenceData {
    private TargetUnitAccountDetails targetUnitAccountDetails;
    private SectorAssociationDetails sectorAssociationDetails;
}
