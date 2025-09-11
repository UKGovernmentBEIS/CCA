import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { PatReportStore } from '../../../pat-report-store';
import { mockPatReportState } from '../testing/mock-data';
import { ReportSubmittedComponent } from './report-submitted.component';

describe('ReportSubmittedComponent', () => {
  let component: ReportSubmittedComponent;
  let fixture: ComponentFixture<ReportSubmittedComponent>;
  let store: PatReportStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportSubmittedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        PatReportStore,
      ],
    }).compileComponents();

    store = TestBed.inject(PatReportStore);
    store.setState(mockPatReportState);

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
