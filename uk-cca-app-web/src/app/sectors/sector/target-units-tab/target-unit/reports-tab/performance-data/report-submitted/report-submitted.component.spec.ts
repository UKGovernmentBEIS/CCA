import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { PerformanceReportStore } from '../../../performance-report-store';
import { mockAccountPerformanceState } from '../testing/mock-data';
import { ReportSubmittedComponent } from './report-submitted.component';

describe('ReportSubmittedComponent', () => {
  let component: ReportSubmittedComponent;
  let fixture: ComponentFixture<ReportSubmittedComponent>;
  let store: PerformanceReportStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportSubmittedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        PerformanceReportStore,
      ],
    }).compileComponents();

    store = TestBed.inject(PerformanceReportStore);
    store.setState(mockAccountPerformanceState);

    fixture = TestBed.createComponent(ReportSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
