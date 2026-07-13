import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { FacilityTargetPeriodReportStore } from '../../../facility-target-period-report.store';
import { mockFacilityTPRStore } from '../../testing/mock-data';
import { TprDetailsComponent } from './tpr-details.component';

describe('TprDetailsComponent', () => {
  let component: TprDetailsComponent;
  let fixture: ComponentFixture<TprDetailsComponent>;
  let store: FacilityTargetPeriodReportStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TprDetailsComponent],
      providers: [FacilityTargetPeriodReportStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(FacilityTargetPeriodReportStore);
    store.setState(mockFacilityTPRStore);

    fixture = TestBed.createComponent(TprDetailsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const summaryValues = getSummaryListData(fixture.nativeElement);

    expect(summaryValues).toEqual([
      [
        ['Reporting period', '70% confirmation'],
        ['TP7', 'Yes'],
      ],
      [
        [
          'Actual energy (GJ)',
          'Target energy (GJ)',
          'Energy difference (GJ)',
          'Target period weighted conversion factor (kgCO2e/GJ)',
          'Actual tCO2e emitted (tCO2e)',
          'Target tCO2e emitted (tCO2e)',
          'tCO2e difference (tCO2e)',
          'Improvement target %',
          'Actual improvement % achieved',
        ],
        ['3.1', '109.4750995', '-106.3750995', '18.9037665', '0.0586017', '2.0694917', '-2.01089', '20%', '97.659%'],
      ],
      [
        ['Target period result', 'Total surplus gained (tCO2e)'],
        ['Target met', '2'],
      ],
      [
        [
          'Is at least 12 months of consecutive baseline data available?',
          'Start date of baseline',
          'Must the Special Reporting Methodology (SRM) be applied for this facility?',
          'Baseline energy to carbon dioxide factor (kgCO2e/kWh)',
        ],
        ['Yes', '1 Jan 2022', 'Yes', '123'],
      ],
      [
        [
          'Fixed baseline energy for the facility (GJ)',
          'Is there a variable energy amount?',
          'Indicate how you want to account for the portion of variable energy used (or carbon dioxide emitted) for your facility',
          'Products submitted',
          'Variable baseline energy for the facility (GJ)',
          'Total baseline energy for the facility (GJ)',
          'Other years - variable baseline energy (GJ)',
        ],
        ['100', 'Yes', 'Split by product', '2 Products', '', '100', '468'],
      ],
      [
        ['TP7 (2026) improvement (%)', 'TP8 (2027 to 2028) improvement (%)', 'TP9 (2029 to 2030) improvement (%)'],
        ['20', '30', '40'],
      ],
    ]);
  });
});
