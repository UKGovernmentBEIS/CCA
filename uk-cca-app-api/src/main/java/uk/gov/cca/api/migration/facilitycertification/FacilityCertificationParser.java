package uk.gov.cca.api.migration.facilitycertification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FacilityCertificationParser {
    
    private static final DateTimeFormatter FLEXIBLE_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("[dd/MM/yyyy]")
            .appendPattern("[d/M/yyyy]")
            .appendPattern("[dd-MM-yyyy]")
            .appendPattern("[d-M-yyyy]")
            .toFormatter();
    
    public static FacilityCertificationMigrationParseResult parse(String input, AtomicInteger failCounter) {
        List<FacilityCertificationVO> validRecords = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        String[] records = input.split("\n");
        
        Arrays.stream(records)
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .forEach(line -> parseRecord(line, errors, validRecords, failCounter));
        
        return FacilityCertificationMigrationParseResult.builder()
                .parsedfacilityCertificationVOList(validRecords)
                .totalRecords(records.length)
                .parsingErrors(errors)
                .build();
    }
    
    private static void parseRecord(String line, List<String> errors, List<FacilityCertificationVO> validRecords, AtomicInteger failCounter) {
        List<String> lineErrors = new ArrayList<>();
        String[] columns = line.split("[|,]", -1);
        
        if (columns.length != 4) {
            errors.add(String.format("Line '%s' has invalid format. " +
                    "Expecting 4 pipe-separated columns", line));
            failCounter.incrementAndGet();
            return;
        }
        
        String facilityId = parseFacilityId(columns[0], lineErrors);
        FacilityCertificationStatus status = parseStatus(columns[1], lineErrors);
        LocalDate startDate = !Objects.equals(columns[2].trim(), "") ? parseDate(columns[2], lineErrors) : null;
        CertificationPeriodType certificationPeriodType = parseCertificationPeriod(columns[3], lineErrors);
        
        if (lineErrors.isEmpty()) {
            validRecords.add(FacilityCertificationVO.builder()
                    .facilityId(facilityId)
                    .certificationStatus(status)
                    .startDate(startDate)
                    .certificationPeriodType(certificationPeriodType)
                    .build());
        } else {
            errors.addAll(lineErrors.stream()
                    .map(error -> String.format("Parsing Error, '%s': %s", line, error))
                    .toList());
            failCounter.incrementAndGet();
        }
    }
    
    private static String parseFacilityId(String facilityId, List<String> errors) {
        if (StringUtils.isBlank(facilityId)) {
            errors.add("Facility ID cannot be empty");
            return null;
        }
        return facilityId.trim();
    }
    
    private static FacilityCertificationStatus parseStatus(String status, List<String> errors) {
        String statusValue = status.trim().toUpperCase();
        
        return switch (statusValue) {
            case "CERTIFIED" -> FacilityCertificationStatus.CERTIFIED;
            case "DECERTIFIED" -> FacilityCertificationStatus.DECERTIFIED;
            default -> {
                errors.add(String.format("Invalid certification status '%s'",
                        status));
                yield null;
            }
        };
    }
    
    private static LocalDate parseDate(String date, List<String> errors) {
        String dateString = date.trim();
        try {
            return LocalDate.parse(dateString, FLEXIBLE_DATE_FORMATTER);
        } catch (Exception e) {
            errors.add(String.format("Invalid date format '%s'. Expected format is dd/MM/yyyy or dd-MM-yyyy",
                    date));
            return null;
        }
    }
    
    private static CertificationPeriodType parseCertificationPeriod(String certificationPeriod, List<String> errors) {
        String cpValue = certificationPeriod.trim().toUpperCase();
        
        try {
            return CertificationPeriodType.valueOf(cpValue);
        } catch(Exception e) {
            errors.add(String.format("Invalid Certification Period '%s'",
                    certificationPeriod));
        }
        
        return null;
    }
}
