package uk.gov.cca.api.targetperiodreporting.facilitycertification.domain;

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

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@SequenceGenerator(name = "default_history_id_generator", sequenceName = "tpr_facility_certification_history_seq", allocationSize = 1)
@Table(name = "tpr_facility_certification_history")
public class FacilityCertificationHistory extends HistoryEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_certification_id", referencedColumnName = "id")
    private FacilityCertification facilityCertification;

    @Valid
    @NotNull
    @Type(JsonType.class)
    @Column(name = "payload", columnDefinition = "jsonb")
    private FacilityCertificationHistoryPayload payload;
}
