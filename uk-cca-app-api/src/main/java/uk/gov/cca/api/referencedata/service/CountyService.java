package uk.gov.cca.api.referencedata.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.referencedata.domain.County;
import uk.gov.cca.api.referencedata.repository.CountyRepository;

import java.util.List;

@Service("countyService")
public class CountyService implements ReferenceDataService<uk.gov.cca.api.referencedata.domain.County> {

    private final CountyRepository countyRepository;

    public CountyService(CountyRepository countyRepository) {
        this.countyRepository = countyRepository;
    }

    @Override
    public List<uk.gov.cca.api.referencedata.domain.County> getReferenceData() {
        return this.getAllCounties();
    }

    public List<County> getAllCounties(){
        return this.countyRepository.findAll();
    }
}
