import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { PatReportStore } from '../../pat-report-store';
import { mockAccountPerformanceState } from '../performance-data/testing/mock-data';
import { PatReportComponent } from './pat-report.component';

describe('PatReportComponent', () => {
  let component: PatReportComponent;
  let fixture: ComponentFixture<PatReportComponent>;
  let store: PatReportStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatReportComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        PatReportStore,
      ],
    }).compileComponents();

    store = TestBed.inject(PatReportStore);
    store.setState(mockAccountPerformanceState);

    fixture = TestBed.createComponent(PatReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
