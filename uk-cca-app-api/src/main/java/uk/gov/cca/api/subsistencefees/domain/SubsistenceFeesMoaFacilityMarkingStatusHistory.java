package uk.gov.cca.api.subsistencefees.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.common.domain.HistoryEntity;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@SequenceGenerator(name = "default_history_id_generator", sequenceName = "sfr_moa_facility_marking_status_history_seq", allocationSize = 1)
@Table(name = "sfr_moa_facility_marking_status_history")
public class SubsistenceFeesMoaFacilityMarkingStatusHistory extends HistoryEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moa_facility_id")
    @NotNull
    private SubsistenceFeesMoaFacility subsistenceFeesMoaFacility;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @NotNull
    private FacilityPaymentStatus paymentStatus;
}
