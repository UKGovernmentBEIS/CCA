package uk.gov.cca.api.targetperiodreporting.targetperiod.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetPeriodYearsContainer {

    @NotEmpty
    @Valid
    private List<TargetPeriodYear> targetPeriodYears;

    @JsonIgnore
    public Year getFinalTargetPeriodTargetYear() {
        return targetPeriodYears.stream()
                .max(Comparator.comparing(TargetPeriodYear::getTargetYear))
                .map(TargetPeriodYear::getTargetYear)
                .orElse(null);
    }

    @JsonIgnore
    public Optional<TargetPeriodYear> getTargetPeriodYear(Year targetYear) {
        return targetPeriodYears.stream()
                .filter(p -> p.getTargetYear().equals(targetYear))
                .findFirst();
    }

    @JsonIgnore
    public LocalDate getTargetPeriodReportingStartDate() {
        return targetPeriodYears.stream()
                .min(Comparator.comparing(TargetPeriodYear::getTargetYear))
                .map(TargetPeriodYear::getReportingStartDate)
                .orElse(null);
    }

    @JsonIgnore
    public Optional<LocalDate> getTargetPeriodReportingEndDate() {
        return targetPeriodYears.stream()
                .max(Comparator.comparing(TargetPeriodYear::getTargetYear))
                .map(TargetPeriodYear::getReportingEndDate);
    }
    
	@JsonIgnore
	public Optional<BigDecimal> getYearMultiplier(Year targetYear) {
		List<Year> years = targetPeriodYears.stream()
				.map(TargetPeriodYear::getTargetYear)
				.toList();

		if (!years.contains(targetYear)) {
			return Optional.empty();
		}

		return years.stream()
				.min(Comparator.naturalOrder())
				.map(minYear -> BigDecimal.valueOf((long) targetYear.getValue() - minYear.getValue() + 1));

	}
}
