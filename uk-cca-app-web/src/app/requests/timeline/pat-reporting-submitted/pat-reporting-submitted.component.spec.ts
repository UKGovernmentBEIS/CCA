import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { PatReportingSubmittedComponent } from './pat-reporting-submitted.component';
import { mockRequestActionStatePATCSVUpload } from './testing/mock-data';

describe('PatReportingSubmittedComponent', () => {
  let component: PatReportingSubmittedComponent;
  let fixture: ComponentFixture<PatReportingSubmittedComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatReportingSubmittedComponent],
      providers: [RequestActionStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockRequestActionStatePATCSVUpload);

    fixture = TestBed.createComponent(PatReportingSubmittedComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct summary data', () => {
    const summaryValues = getSummaryListData(fixture.nativeElement);

    expect(summaryValues).toHaveLength(2);

    expect(summaryValues[0]).toEqual([
      ['Reporting year', 'Uploaded files'],
      ['2026', 'dummy-fail.csv (opens in a new tab)'],
    ]);

    const [headers, values] = summaryValues[1];
    expect(headers).toEqual([
      'Time submitted',
      'Files uploaded',
      'Facilities successful',
      'Facilities failed',
      'Submission summary file',
    ]);

    expect(values[0]).toContain('1 Jan 2027');
    expect(values[1]).toBe('1');
    expect(values[2]).toBe('0');
    expect(values[3]).toBe('1');
    expect(values[4]).toBe('Upload_Summary.csv (opens in a new tab)');
  });
});
