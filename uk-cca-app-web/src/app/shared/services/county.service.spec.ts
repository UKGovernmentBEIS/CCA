import { TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { ReferenceDataService } from 'cca-api';

import { County } from '../types/county';
import { CountyService } from './county.service';

const mockCounties = [
  {
    id: 1,
    name: 'Portugal',
  },
  {
    id: 2,
    name: 'Palau',
  },
  {
    id: 3,
    name: 'United Kingdom',
  },
];

describe('CountyService', () => {
  let service: CountyService;

  const mockReferenceDataService = {
    getReferenceData: jest.fn().mockReturnValue(of({ COUNTIES: mockCounties })),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CountyService, { provide: ReferenceDataService, useValue: mockReferenceDataService }],
    });

    service = TestBed.inject(CountyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should map counties to valid format', () => {
    of(mockCounties).subscribe((c: County[]) => {
      expect(c[0].id).toEqual(1);
      expect(c[1].id).toEqual(2);
    });
  });
});
