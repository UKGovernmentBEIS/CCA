package uk.gov.cca.api.subsistencefees.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import uk.gov.cca.api.common.domain.HistoryEntity;

@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@SequenceGenerator(name = "default_history_id_generator", sequenceName = "sfr_moa_received_amount_history_seq", allocationSize = 1)
@Table(name = "sfr_moa_received_amount_history")
public class SubsistenceFeesMoaReceivedAmountHistory extends HistoryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moa_id")
    @NotNull
    private SubsistenceFeesMoa subsistenceFeesMoa;

    @Type(JsonType.class)
    @Column(name = "payload", columnDefinition = "jsonb")
    @Valid
    @NotNull
    private SubsistenceFeesMoaReceivedAmountHistoryPayload payload;
}
