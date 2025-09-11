import { TestBed } from '@angular/core/testing';

import { lastValueFrom, Observable, of } from 'rxjs';

import { MiReportsService } from 'cca-api';

import { MiReportsStore } from '../mi-reports.store';
import { miReportsListGuard } from './mi-reports-list.guard';

describe('MiReportsListGuard', () => {
  let store: MiReportsStore;
  let miReportsService: Partial<jest.Mocked<MiReportsService>>;

  const miReports = [{ id: 1, miReportType: 'CUSTOM' }];

  beforeEach(() => {
    miReportsService = {
      getCurrentUserMiReports: jest.fn().mockReturnValue(of(miReports)),
    };

    TestBed.configureTestingModule({
      providers: [MiReportsStore, { provide: MiReportsService, useValue: miReportsService }],
    });

    store = TestBed.inject(MiReportsStore);
  });

  function runGuard(): Observable<unknown> {
    return TestBed.runInInjectionContext(() => miReportsListGuard());
  }

  it('should allow access and resolve the contact if the contact is found', async () => {
    await lastValueFrom(runGuard());

    expect(miReportsService.getCurrentUserMiReports).toHaveBeenCalledTimes(1);

    expect(store.state).toEqual(miReports);
  });
});
