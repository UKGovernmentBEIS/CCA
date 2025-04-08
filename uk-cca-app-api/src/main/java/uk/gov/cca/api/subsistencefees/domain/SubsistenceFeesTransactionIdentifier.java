package uk.gov.cca.api.subsistencefees.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "sfr_transaction_identifier")
public class SubsistenceFeesTransactionIdentifier {

    @Id
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "moa_type", unique = true)
    @NotNull
    private MoaType moaType;

    @Column(name = "transaction_id")
    @NotNull
    private Long transactionId;
}
