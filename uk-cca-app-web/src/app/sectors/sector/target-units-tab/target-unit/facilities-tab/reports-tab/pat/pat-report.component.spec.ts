import { provideHttpClient, withXhr } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { FacilityPATReportsState, FacilityPATReportStore } from '../../facility-pat-report.store';
import { mockFacilityPATStore } from '../testing/mock-data';
import { PatReportComponent } from './pat-report.component';

describe('PatReportComponent', () => {
  let component: PatReportComponent;
  let fixture: ComponentFixture<PatReportComponent>;
  let store: FacilityPATReportStore;
  let httpTesting: HttpTestingController;

  async function setup(initialState: FacilityPATReportsState = mockFacilityPATStore) {
    await TestBed.configureTestingModule({
      imports: [PatReportComponent],
      providers: [
        FacilityPATReportStore,
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(FacilityPATReportStore);
    store.setState(initialState);
    httpTesting = TestBed.inject(HttpTestingController);

    fixture = TestBed.createComponent(PatReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  }

  it('should create', async () => {
    await setup();

    expect(component).toBeTruthy();
  });

  it('should render PAT report form', async () => {
    await setup();

    const performanceReportElement = fixture.nativeElement.querySelector('[data-testid="pat-report-form"]');

    expect(performanceReportElement).toBeTruthy();
  });

  it('should have a select dropdown for reporting year', async () => {
    await setup();

    const selectElement = fixture.nativeElement.querySelector('div[formControlName="reportingYear"]');
    expect(selectElement).toBeTruthy();
  });

  it('should display the correct report details data', async () => {
    await setup();

    const summaryValues = getSummaryListData(fixture.nativeElement);

    expect(summaryValues.length).toEqual(1);
    expect(summaryValues).toEqual([
      [
        ['Reporting year', 'Last uploaded version', 'Date of report submission'],
        ['2026', '2', '27/08/2026'],
      ],
    ]);
  });

  it('should not show the empty state when no year is selected', async () => {
    await setup({ reportInfo: null, reportingYear: null, details: null });

    httpTesting.expectOne((req) => req.url.endsWith('/facilities/0')).flush(null);

    expect(fixture.nativeElement.textContent).not.toContain('There are no reports for the selected criteria.');
    httpTesting.verify();
  });

  it('should not request details and should clear details when the API returns no report for the selected year', async () => {
    await setup({ ...mockFacilityPATStore });

    httpTesting.expectOne((req) => req.url.endsWith('/facilities/0')).flush(null);
    httpTesting
      .expectOne((req) => req.url.includes('/performance-account-template-data-report/info'))
      .flush(mockFacilityPATStore.reportInfo);
    httpTesting
      .expectOne((req) => req.url.includes('/performance-account-template-data-report/details'))
      .flush(mockFacilityPATStore.details);
    fixture.detectChanges();

    component.form.controls.reportingYear.setValue('2027');
    await fixture.whenStable();

    httpTesting.expectOne((req) => req.url.includes('/performance-account-template-data-report/info')).flush(null);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('There are no reports for the selected criteria.');
    expect(store.state.details).toBeNull();
    httpTesting.expectNone((req) => req.url.includes('/performance-account-template-data-report/details'));
    httpTesting.verify();
  });
});
