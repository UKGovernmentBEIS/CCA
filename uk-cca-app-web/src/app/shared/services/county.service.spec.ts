import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { County } from '../types/county';
import { CountyService } from './county.service';

const mockCounties = {
  COUNTIES: [
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
  ],
};

describe('CountyService', () => {
  let service: CountyService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    httpTestingController = TestBed.inject(HttpTestingController);
    service = TestBed.inject(CountyService);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should map counties to valid format', () => {
    service.getUkCounties().subscribe((c: County[]) => {
      expect(c[0].id).toEqual(1);
      expect(c[1].id).toEqual(2);
    });

    const request = httpTestingController.expectOne('/api/v1.0/data?types=COUNTIES');
    expect(request.request.method).toEqual('GET');
    request.flush(mockCounties);
  });
});
