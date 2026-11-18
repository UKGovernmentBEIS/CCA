import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { FacilityTargetPeriodReportStore } from '../../facility-target-period-report.store';
import { mockFacilityTPRStore } from '../testing/mock-data';
import { TargetPeriodReportComponent } from './target-period-report.component';

describe('TargetPeriodReportComponent', () => {
  let component: TargetPeriodReportComponent;
  let fixture: ComponentFixture<TargetPeriodReportComponent>;
  let store: FacilityTargetPeriodReportStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TargetPeriodReportComponent],
      providers: [
        FacilityTargetPeriodReportStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(FacilityTargetPeriodReportStore);
    store.setState(mockFacilityTPRStore);

    fixture = TestBed.createComponent(TargetPeriodReportComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render Target period report form', () => {
    const performanceReportElement = fixture.nativeElement.querySelector('[data-testid="target-period-report-form"]');

    expect(performanceReportElement).toBeTruthy();
  });

  it('should have a select dropdown for target period', () => {
    const selectElement = fixture.nativeElement.querySelector('div[formControlName="targetPeriodType"]');
    expect(selectElement).toBeTruthy();
  });

  it('should have a select dropdown for report type', () => {
    const selectElement = fixture.nativeElement.querySelector('div[formControlName="reportType"]');
    expect(selectElement).toBeTruthy();
  });

  it('should display the correct period details data', () => {
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
        ['TP7 (2026)', 'No', 'No', '4', '05/06/2026'],
      ],
    ]);
  });
});
