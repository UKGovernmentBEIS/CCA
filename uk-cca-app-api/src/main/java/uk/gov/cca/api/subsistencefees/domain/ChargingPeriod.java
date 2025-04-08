package uk.gov.cca.api.subsistencefees.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.netz.api.common.config.YearAttributeConverter;

import java.time.LocalDate;
import java.time.Year;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "sfr_charging_period")
public class ChargingPeriod {

    @Id
    @SequenceGenerator(name = "sfr_charging_period_id_generator", sequenceName = "sfr_charging_period_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sfr_charging_period_id_generator")
    private Long id;

    @Column(name = "start_date")
    @NotNull
    private LocalDate startDate;

    @Column(name = "end_date")
    @NotNull
    private LocalDate endDate;

    @Convert(converter = YearAttributeConverter.class)
    @Column(name = "charging_year", unique = true)
    @NotNull
    private Year chargingYear;
}
