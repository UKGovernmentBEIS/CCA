import { provideHttpClient, withXhr } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';
import { describe, expect, it } from 'vitest';

import {
  FacilityTargetPeriodReportsState,
  FacilityTargetPeriodReportStore,
} from '../../facility-target-period-report.store';
import { mockFacilityTPRStore } from '../testing/mock-data';
import { TargetPeriodReportComponent } from './target-period-report.component';

describe('TargetPeriodReportComponent', () => {
  let component: TargetPeriodReportComponent;
  let fixture: ComponentFixture<TargetPeriodReportComponent>;
  let store: FacilityTargetPeriodReportStore;
  let httpTesting: HttpTestingController;

  async function setup(initialState: FacilityTargetPeriodReportsState = mockFacilityTPRStore) {
    await TestBed.configureTestingModule({
      imports: [TargetPeriodReportComponent],
      providers: [
        FacilityTargetPeriodReportStore,
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(FacilityTargetPeriodReportStore);
    store.setState(initialState);
    httpTesting = TestBed.inject(HttpTestingController);

    fixture = TestBed.createComponent(TargetPeriodReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  }

  it('should create', async () => {
    await setup();

    expect(component).toBeTruthy();
  });

  it('should render Target period report form', async () => {
    await setup();

    const performanceReportElement = fixture.nativeElement.querySelector('[data-testid="target-period-report-form"]');

    expect(performanceReportElement).toBeTruthy();
  });

  it('should have a select dropdown for target period', async () => {
    await setup();

    const selectElement = fixture.nativeElement.querySelector('div[formControlName="targetPeriodType"]');
    expect(selectElement).toBeTruthy();
  });

  it('should have a select dropdown for report type', async () => {
    await setup();

    const selectElement = fixture.nativeElement.querySelector('div[formControlName="reportType"]');
    expect(selectElement).toBeTruthy();
  });

  it('should display the correct period details data', async () => {
    await setup();

    const summaryValues = getSummaryListData(fixture.nativeElement);

    expect(summaryValues.length).toEqual(1);
    expect(summaryValues).toEqual([
      [
        [
          'Reporting period',
          'Variation completed after submission',
          'Locked',
          'Last uploaded version',
          'Date of report submission',
        ],
        ['TP7 (2026)', 'No', 'No', 'Final (Secondary) - v4', '05/06/2026'],
      ],
    ]);
  });

  it('should show the empty state when the API returns an empty status list', async () => {
    await setup({ ...mockFacilityTPRStore, statusInfo: [] });

    httpTesting.expectOne((req) => req.url.includes('/performance-data-report/status')).flush([]);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('There are no reports for the selected criteria.');
    httpTesting.verify();
  });

  it('should not show the empty state when no criteria are selected', async () => {
    await setup({ ...mockFacilityTPRStore, statusInfo: [], reportType: null, targetPeriodType: null });

    expect(fixture.nativeElement.textContent).not.toContain('There are no reports for the selected criteria.');
    httpTesting.verify();
  });

  it('should show the empty state when the API returns null status info', async () => {
    await setup({ ...mockFacilityTPRStore });

    httpTesting.expectOne((req) => req.url.includes('/performance-data-report/status')).flush(null);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('There are no reports for the selected criteria.');
    httpTesting.verify();
  });
});
