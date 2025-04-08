import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { PerformanceReportStore } from '../../performance-report-store';
import { mockAccountPerformanceState } from '../testing/mock-data';
import { PerformanceReportComponent } from './performance-report.component';

describe('PerformanceReportComponent', () => {
  let component: PerformanceReportComponent;
  let fixture: ComponentFixture<PerformanceReportComponent>;
  let store: PerformanceReportStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerformanceReportComponent],
      providers: [
        PerformanceReportStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();
    store = TestBed.inject(PerformanceReportStore);
    store.setState(mockAccountPerformanceState);
    fixture = TestBed.createComponent(PerformanceReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render performance report component', () => {
    const performanceReportElement = fixture.nativeElement.querySelector(
      '[data-testid="performance report component"]',
    );
    expect(performanceReportElement).toBeTruthy();
  });

  it('should have a select dropdown for target period', () => {
    const selectElement = fixture.nativeElement.querySelector('div[formControlName="targetPeriodType"]');
    expect(selectElement).toBeTruthy();
  });
});
