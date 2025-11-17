import { TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { ReferenceDataService } from 'cca-api';

import { Country } from '../types/country';
import { CountryService } from './country.service';

const mockCountries = [
  {
    code: 'PT',
    name: 'Portugal',
    officialName: 'The Portuguese Republic',
  },
  {
    code: 'PW',
    name: 'Palau',
    officialName: 'The Republic of Palau',
  },
  {
    code: 'GB',
    name: 'United Kingdom',
    officialName: 'United Kingdom',
  },
];

describe('CountryService', () => {
  let service: CountryService;

  const mockReferenceDataService = {
    getReferenceData: jest.fn().mockReturnValue(of({ COUNTRIES: mockCountries })),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CountryService, { provide: ReferenceDataService, useValue: mockReferenceDataService }],
    });

    service = TestBed.inject(CountryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should map countries to valid format', () => {
    of(mockCountries).subscribe((c: Country[]) => {
      expect(c[0].code).toEqual('PT');
      expect(c[1].code).toEqual('PW');
    });
  });

  it('should return country by code', () => {
    const country = mockCountries.find((c) => c.code === 'GB');
    expect(country.name).toEqual('United Kingdom');
  });
});
