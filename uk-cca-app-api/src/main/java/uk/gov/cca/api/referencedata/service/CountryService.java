package uk.gov.cca.api.referencedata.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.referencedata.domain.Country;
import uk.gov.cca.api.referencedata.repository.CountryRepository;

import java.util.List;

@Service("countryService")
public class CountryService implements ReferenceDataService<uk.gov.cca.api.referencedata.domain.Country> {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public List<Country> getReferenceData() {
        return countryRepository.findAll();
    }
}
