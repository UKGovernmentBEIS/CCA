import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getByTestId, getByText } from '@testing';
import { Mocked } from 'vitest';

import { PerformanceDataReportingViewInfoService, RequestTaskItemDTO, RequestTaskPayload, TasksService } from 'cca-api';

import { TprCsvUploadProcessComponent } from './tpr-csv-upload-process.component';

describe('TprCsvUploadProcessComponent', () => {
  let component: TprCsvUploadProcessComponent;
  let fixture: ComponentFixture<TprCsvUploadProcessComponent>;
  let store: RequestTaskStore;
  let authStore: AuthStore;

  const tasksService: Partial<Mocked<TasksService>> = {
    processRequestTaskAction: vi.fn().mockReturnValue(of(null)),
  };

  const performanceDataReportingViewInfoService: Partial<Mocked<PerformanceDataReportingViewInfoService>> = {
    getAvailableTargetPeriodsForPerformanceDataReporting: vi
      .fn()
      .mockReturnValue(of([{ targetPeriodType: 'TP7', reportType: 'FINAL' }])),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TprCsvUploadProcessComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: PerformanceDataReportingViewInfoService, useValue: performanceDataReportingViewInfoService },
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ taskId: '856' }),
        },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    store.setRequestTaskItem({
      requestTask: {
        id: 856,
        assigneeUserId: '7b91199c-4770-4d4b-a0ed-d6d9667de157',
        payload: {
          performanceDataUpload: {
            targetPeriodType: 'TP7',
            reportType: 'FINAL',
            files: ['5a773a53-01ad-4c8e-ba9b-bca0560e926d'],
          },
          processingStatus: 'NOT_STARTED_YET',
        },
      },
    } as RequestTaskItemDTO);
    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ userId: '7b91199c-4770-4d4b-a0ed-d6d9667de157', roleType: 'SECTOR_USER' });

    fixture = TestBed.createComponent(TprCsvUploadProcessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show only the CSV constraints and describe both upload controls with them', () => {
    const upload = fixture.nativeElement.querySelector('cca-multiple-file-input');
    expect(upload).not.toBeNull();
    expect(upload.querySelector('[id="uploadedFiles-hint-file-size"]')).toBeNull();

    const hint = upload.querySelector('[id="uploadedFiles-hint"]');
    expect(hint.textContent).toContain('CSV');
    expect(hint.textContent).toContain('20MB');
    expect(upload.querySelector('input[type="file"]').getAttribute('accept')).toBe('.csv');

    const uploadControls = upload.querySelector('.cca-multi-file-upload__dropzone').querySelectorAll('input, button');
    expect(uploadControls).toHaveLength(2);
    for (const control of uploadControls) {
      expect(control.getAttribute('aria-describedby')).toBe(hint.id);
    }
  });

  it('should render and expand both govuk-details components', async () => {
    const details = getByTestId('target-period-details');
    expect(details).toBeTruthy();
    click(details);
    fixture.detectChanges();
    expect(
      getByText(/Additional target periods will become available once their respective reporting periods start/i),
    ).toBeTruthy();

    const filesDetails = getByTestId('target-period-files-details');
    expect(filesDetails).toBeTruthy();
    click(filesDetails);
    fixture.detectChanges();
    expect(getByText(/you need to fill in a CSV file with your data using a specific/i)).toBeTruthy();
  });

  it('should render the loading spinner when processingStatus is IN_PROGRESS', () => {
    store.setPayload({
      performanceDataUpload: { targetPeriodType: 'TP7', reportType: 'FINAL', files: [] },
      processingStatus: 'IN_PROGRESS',
    } as RequestTaskPayload);

    fixture.detectChanges();

    const spinner = fixture.debugElement.query(By.css('cca-loading-spinner'));
    expect(spinner).not.toBeNull();
    expect(spinner.nativeElement.textContent).toContain('Your files are being uploaded');
  });

  it('should render the return to dashboard link during IN_PROGRESS', () => {
    store.setPayload({
      performanceDataUpload: { targetPeriodType: 'TP7', reportType: 'FINAL', files: [] },
      processingStatus: 'IN_PROGRESS',
    } as RequestTaskPayload);

    fixture.detectChanges();

    const returnLink = fixture.debugElement.query(By.css('a.govuk-link'));
    expect(returnLink).not.toBeNull();
    expect(returnLink.nativeElement.textContent).toContain('Return to: Dashboard');
  });

  it('should not render the return to dashboard link during NOT_STARTED_YET', () => {
    const returnLink = fixture.debugElement.query(By.css('a[routerlink="/dashboard"]'));
    expect(returnLink).toBeNull();
  });
});
