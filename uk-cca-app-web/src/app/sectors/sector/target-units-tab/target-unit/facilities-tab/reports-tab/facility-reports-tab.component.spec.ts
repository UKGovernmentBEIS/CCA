import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByRole, getByTestId, getByText } from '@testing';

import { FacilityTargetPeriodReportStore } from '../facility-target-period-report.store';
import { FacilityReportsTabComponent } from './facility-reports-tab.component';
import { mockFacilityTPRStore } from './testing/mock-data';

describe('FacilityReportsTabComponent', () => {
  let component: FacilityReportsTabComponent;
  let fixture: ComponentFixture<FacilityReportsTabComponent>;
  let store: FacilityTargetPeriodReportStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacilityReportsTabComponent],
      providers: [
        FacilityTargetPeriodReportStore,
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

    store = TestBed.inject(FacilityTargetPeriodReportStore);
    store.setState(mockFacilityTPRStore);

    fixture = TestBed.createComponent(FacilityReportsTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the Reports heading', () => {
    expect(getByText('Reports')).toBeTruthy();
  });

  it('should have a navigation with Target period and PAT links', () => {
    expect(getByText('Target period')).toBeTruthy();
    expect(getByText('PAT')).toBeTruthy();
  });

  it('should have Target period link active by default', () => {
    const targetPeriodLink = getByRole('link', { name: 'Target period' });
    expect(targetPeriodLink.getAttribute('aria-current')).toBe('location');
  });

  it('should not have PAT link active by default', () => {
    const patLink = getByRole('link', { name: 'PAT' });
    expect(patLink.getAttribute('aria-current')).toBeNull();
  });

  it('should display the target period report component when section is target period', () => {
    expect(getByTestId('target-period-report-form')).toBeTruthy();
  });
});
