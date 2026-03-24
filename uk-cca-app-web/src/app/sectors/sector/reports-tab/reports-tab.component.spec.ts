import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { getByLabelText, getByText, queryByRole } from '@testing';

import { ReportsTabComponent } from './reports-tab.component';

describe('PerformanceDataReportsComponent', () => {
  let component: ReportsTabComponent;
  let fixture: ComponentFixture<ReportsTabComponent>;

  beforeEach(async () => {
    const mockActivatedRoute = {
      snapshot: {
        queryParams: { reportType: 'Performance' },
        paramMap: convertToParamMap({ page: '1', sectorId: '1' }),
        queryParamMap: convertToParamMap({
          targetUnitBusinessId: '1',
          targetPeriodType: 'TP6',
          performanceOutcome: 'TARGET_MET',
          submissionType: 'PRIMARY',
        }),
      },
      queryParams: of({ reportType: 'Performance', page: '1' }),
      queryParamMap: of(convertToParamMap({ reportType: 'Performance' })),
    };

    await TestBed.configureTestingModule({
      imports: [ReportsTabComponent, ReactiveFormsModule],
      providers: [
        FormBuilder,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ReportsTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should correctly initialize sectorId', () => {
    expect(component.sectorId).toEqual(1);
  });

  it('should render the heading "Reports"', () => {
    const heading = getByText('Reports');
    expect(heading).toBeTruthy();
  });

  it('should have a select dropdown for "Report type"', () => {
    const reportTypeSelect = getByLabelText('Report type');
    expect(reportTypeSelect).toBeTruthy();
  });

  it('should have a download button with "Download to spreadsheet"', () => {
    const downloadButton = queryByRole('button', { name: /Download to spreadsheet/i });
    expect(downloadButton).toBeTruthy();
  });
});
