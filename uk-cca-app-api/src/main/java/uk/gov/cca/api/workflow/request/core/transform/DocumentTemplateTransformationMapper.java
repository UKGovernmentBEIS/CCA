package uk.gov.cca.api.workflow.request.core.transform;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.netz.api.referencedata.domain.Country;
import uk.gov.netz.api.referencedata.service.CountryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentTemplateTransformationMapper {

    private final CountryService countryService;

    public String constructAccountAddressDTO(AccountAddressDTO address) {
        String countryName = countryService.getReferenceData().stream()
                .filter(country -> address.getCountry().equals(country.getCode()))
                .map(Country::getName)
                .findFirst().orElse("");
        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append(address.getLine1());
        Optional.ofNullable(address.getLine2())
                .ifPresent(line2 -> addressBuilder.append("\n").append(line2));
        addressBuilder.append("\n").append(address.getCity());
        addressBuilder.append("\n").append(address.getPostcode());
        Optional.ofNullable(address.getCounty())
                .ifPresent(county -> addressBuilder.append("\n").append(county));
        addressBuilder.append("\n").append(countryName);
        return addressBuilder.toString();
    }

    public String constructAddressDTO(AddressDTO address) {
        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append(address.getLine1());
        Optional.ofNullable(address.getLine2())
                .ifPresent(line2 -> addressBuilder.append("\n").append(line2));
        addressBuilder.append("\n").append(address.getCity());
        addressBuilder.append("\n").append(address.getPostcode());
        Optional.ofNullable(address.getCounty())
                .ifPresent(county -> addressBuilder.append("\n").append(county));

        return addressBuilder.toString();
    }

    public String formatUmaDate(LocalDate umaDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return umaDate.format(formatter);
    }

    public String formatCurrentDate() {
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.ENGLISH);
        return today.format(formatter);
    }
}
