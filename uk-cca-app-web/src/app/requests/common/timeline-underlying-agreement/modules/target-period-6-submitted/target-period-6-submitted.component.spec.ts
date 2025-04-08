import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { mockUnderlyingAgreementSubmittedRequestAction } from '../../testing/mock-data';
import { TargetPeriod6SubmittedComponent } from './target-period-6-submitted.component';

describe('TargetPeriod6Component', () => {
  let component: TargetPeriod6SubmittedComponent;
  let fixture: ComponentFixture<TargetPeriod6SubmittedComponent>;
  let store: RequestActionStore;
  let page: Page;

  class Page extends BasePage<TargetPeriod6SubmittedComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [
          ...(Array.from(row.querySelectorAll('dt')) ?? []),
          ...(Array.from(row.querySelectorAll('dd')) ?? []),
        ])
        .map((pair) => pair.map((element) => element?.textContent?.trim()));
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TargetPeriod6SubmittedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setAction(mockUnderlyingAgreementSubmittedRequestAction);

    fixture = TestBed.createComponent(TargetPeriod6SubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.header.textContent.trim()).toEqual('TP6 (2024)');

    expect(page.summaryListValues).toEqual([
      ['Target calculator file', 'calculatorFile.xls'],
      ['Energy or carbon units used by the sector', 'Energy (kWh)'],
      ['Energy or carbon units used by the target unit', 'Energy (kWh)'],
      ['Target type for agreement composition', 'Relative'],
      ['Target unit throughput has a unit of', 'GJ'],
      ['Upload evidence', 'conversionEvidence.xlsx'],
      ['Is at least 12 months of consecutive baseline data available?', 'No'],
      ['Enter the date that 12 months of data will be available.', '12 Dec 2020'],
      ['Explain how the target unit fits the greenfield criteria', 'test'],
      ['Evidence', 'greenfieldEvidence.xlsx'],
      ['Baseline kWh for the target facility', '100'],
      [
        `Have you used the special reporting mechanism to adjust the baseline throughput for any of the facilities in the target unit using combined heat and power (CHP)?`,
        'Yes',
      ],
      ['Baseline throughput (GJ)', '10'],
      ['Performance (kWh/GJ)', '10'],
      ['Baseline energy to carbon factor (kgC/kWh)', '1'],
      ['Improvement (%)', '0.1'],
      ['Target (kWh/GJ)', '9'],
    ]);
  });
});
