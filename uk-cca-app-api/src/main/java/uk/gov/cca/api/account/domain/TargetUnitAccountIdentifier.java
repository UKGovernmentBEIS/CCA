package uk.gov.cca.api.account.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "account_target_unit_identifier")
@NamedQuery(
        name = TargetUnitAccountIdentifier.NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNT_IDENTIFIER,
        query = "select aci "
                + "from TargetUnitAccountIdentifier aci "
                + "where aci.sectorAssociationId = :sectorAssociationId")
public class TargetUnitAccountIdentifier {

    public static final String NAMED_QUERY_FIND_TARGET_UNIT_ACCOUNT_IDENTIFIER = "TargetUnitAccountIdentifier.findTargetUnitAccountIdentifier";

    @Id
    @SequenceGenerator(name = "account_target_unit_identifier_id_generator", sequenceName = "account_target_unit_identifier_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_target_unit_identifier_id_generator")
    private Integer id;

    @Column(name = "account_id")
    @NotNull
    private Long accountId;

    @Column(name = "sector_association_id")
    @NotNull
    private Long sectorAssociationId;
}
