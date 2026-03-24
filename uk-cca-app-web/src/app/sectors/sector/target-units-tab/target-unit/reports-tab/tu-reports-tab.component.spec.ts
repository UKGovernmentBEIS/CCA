import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByRole, getByTestId, getByText } from '@testing';

import { ActiveTargetUnitStore } from '../../active-target-unit.store';
import { PerformanceReportStore } from '../performance-report-store';
import { mockAccountPerformanceState } from './performance-data/testing/mock-data';
import { TuReportsTabComponent } from './tu-reports-tab.component';

describe('TuReportsTabComponent', () => {
  let component: TuReportsTabComponent;
  let fixture: ComponentFixture<TuReportsTabComponent>;
  let activeTargetUnitStore: ActiveTargetUnitStore;
  let performanceReportStore: PerformanceReportStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TuReportsTabComponent],
      providers: [
        ActiveTargetUnitStore,
        PerformanceReportStore,
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            queryParamMap: of(convertToParamMap({ section: 'performance' })),
          }),
        },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    activeTargetUnitStore = TestBed.inject(ActiveTargetUnitStore);
    performanceReportStore = TestBed.inject(PerformanceReportStore);
    activeTargetUnitStore.setState({});
    performanceReportStore.setState(mockAccountPerformanceState);

    fixture = TestBed.createComponent(TuReportsTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the Reports heading', () => {
    expect(getByText('Reports')).toBeTruthy();
  });

  it('should have a navigation with Performance and PAT links', () => {
    expect(getByText('Performance')).toBeTruthy();
    expect(getByText('PAT')).toBeTruthy();
  });

  it('should have Performance link active by default', () => {
    const performanceLink = getByRole('link', { name: 'Performance' });
    expect(performanceLink.getAttribute('aria-current')).toBe('location');
  });

  it('should not have PAT link active by default', () => {
    const patLink = getByRole('link', { name: 'PAT' });
    expect(patLink.getAttribute('aria-current')).toBeNull();
  });

  it('should display the performance report component when section is performance', () => {
    expect(getByTestId('performance-report-component')).toBeTruthy();
  });
});
