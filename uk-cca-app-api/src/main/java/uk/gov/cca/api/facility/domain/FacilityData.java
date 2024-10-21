package uk.gov.cca.api.facility.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "facility_data",
indexes = {
        @Index(name = "idx_facility_data_facility_id", columnList = "facility_id", unique = true)
})
public class FacilityData {
	
	@Id
    @SequenceGenerator(name = "facility_data_id_generator", sequenceName = "facility_data_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "facility_data_id_generator")
    private Long id;

	@EqualsAndHashCode.Include()
    @Column(name = "facility_id")
    @NotNull
    private String facilityId;

    @Column(name = "account_id")
    @NotNull
    private Long accountId;
    
    @Column(name = "created_date")
    @NotNull
	private LocalDateTime createdDate;
    
    @Column(name = "closed_date")
	private LocalDateTime closedDate;

    @Column(name = "charge_start_date")
    private LocalDate chargeStartDate;
}
