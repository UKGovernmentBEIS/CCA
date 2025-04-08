package uk.gov.cca.api.subsistencefees.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.netz.api.common.config.YearAttributeConverter;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Table(name = "sfr_run")
public class SubsistenceFeesRun {

	@Id
    @SequenceGenerator(name = "sfr_run_id_generator", sequenceName = "sfr_run_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sfr_run_id_generator")
    private Long id;

	@EqualsAndHashCode.Include()
	@Column(name = "business_id", unique = true)
    @NotNull
    private String businessId;

    @Convert(converter = YearAttributeConverter.class)
    @Column(name = "charging_year")
    @NotNull
    private Year chargingYear;

	@Column(name = "initial_total_amount")
    @NotNull
    @PositiveOrZero
    private BigDecimal initialTotalAmount;

	@Column(name = "submission_date")
    private LocalDateTime submissionDate;

	@Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    @NotNull
    private CompetentAuthorityEnum competentAuthority;

	@Builder.Default
    @OneToMany(mappedBy = "subsistenceFeesRun", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SubsistenceFeesMoa> subsistenceFeesMoas = new ArrayList<>();
}
