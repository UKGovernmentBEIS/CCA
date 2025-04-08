package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubsistenceFeesRunRequestMetadata extends RequestMetadata {

    private Year chargingYear;

    @Builder.Default
    private Map<Long, MoaReport> sectorsReports = new HashMap<>();

    @Builder.Default
    private Map<Long, MoaReport> accountsReports = new HashMap<>();

    @JsonIgnore
    public List<MoaReport> getAllReports() {
        List<MoaReport> allReports = new ArrayList<>();
        allReports.addAll(sectorsReports.values());
        allReports.addAll(accountsReports.values());
        return allReports;
    }

    @JsonProperty(access = Access.READ_ONLY)
    public Long getSentInvoices() {
        return getAllReports().stream().filter(report -> Boolean.TRUE.equals(report.getSucceeded())).collect(Collectors.counting());
    }

    @JsonProperty(access = Access.READ_ONLY)
    public Long getFailedInvoices() {
        return getAllReports().stream().filter(report -> Boolean.FALSE.equals(report.getSucceeded())).collect(Collectors.counting());
    }
}
