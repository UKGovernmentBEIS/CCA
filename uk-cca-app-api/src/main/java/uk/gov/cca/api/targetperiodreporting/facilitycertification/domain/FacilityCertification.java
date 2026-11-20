package uk.gov.cca.api.targetperiodreporting.facilitycertification.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Entity
@Table(name = "tpr_facility_certification", uniqueConstraints = @UniqueConstraint(columnNames = {"facility_id", "certification_period_id"}))
public class FacilityCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tpr_facility_certification_seq")
    @SequenceGenerator(name = "tpr_facility_certification_seq", sequenceName = "tpr_facility_certification_seq", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "facility_id")
    @NotNull
    @EqualsAndHashCode.Include()
    private Long facilityId;

    @Column(name = "certification_period_id")
    @NotNull
    @EqualsAndHashCode.Include
    private Long certificationPeriodId;

    @Enumerated(EnumType.STRING)
    @Column(name = "certification_status", length = 64)
    @NotNull
    private FacilityCertificationStatus certificationStatus;

    @Column(name = "start_date")
    @PastOrPresent
    private LocalDate startDate;

    @OneToMany(mappedBy = "facilityCertification", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("submissionDate desc")
    @Builder.Default
    private List<FacilityCertificationHistory> certificationHistoryList = new ArrayList<>();

    public void addHistory(FacilityCertificationHistory history) {
        history.setFacilityCertification(this);
        this.certificationHistoryList.add(history);
    }
}
