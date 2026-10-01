import { HttpErrorResponse, provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { of, throwError } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getByTestId, getByText } from '@testing';
import { Mocked } from 'vitest';

import { RequestTaskItemDTO, RequestTaskPayload, TasksService } from 'cca-api';

import { PatCsvUploadProcessComponent } from './pat-csv-upload-process.component';

describe('PatCsvUploadProcessComponent', () => {
  let component: PatCsvUploadProcessComponent;
  let fixture: ComponentFixture<PatCsvUploadProcessComponent>;
  let store: RequestTaskStore;
  let authStore: AuthStore;

  const tasksService: Partial<Mocked<TasksService>> = {
    processRequestTaskAction: vi.fn().mockReturnValue(of(null)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatCsvUploadProcessComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
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
          performanceAccountTemplateDataUpload: {
            targetYear: 2026,
            files: ['5a773a53-01ad-4c8e-ba9b-bca0560e926d'],
          },
          processingStatus: 'NOT_STARTED_YET',
        },
      },
    } as RequestTaskItemDTO);
    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ userId: '7b91199c-4770-4d4b-a0ed-d6d9667de157', roleType: 'SECTOR_USER' });

    fixture = TestBed.createComponent(PatCsvUploadProcessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the reporting year', () => {
    expect(fixture.nativeElement.textContent).toContain('2026');
  });

  it('should render the file upload component', () => {
    const fileInput = fixture.debugElement.query(By.css('cca-multiple-file-input'));
    expect(fileInput).not.toBeNull();
  });

  it('should render and expand the govuk-details component', async () => {
    const details = getByTestId('pat-file-details');
    expect(details).toBeTruthy();
    click(details);
    fixture.detectChanges();
    expect(getByText(/predefined structure/i)).toBeTruthy();
  });

  it('should render the loading spinner when processingStatus is IN_PROGRESS', () => {
    store.setPayload({
      performanceAccountTemplateDataUpload: { targetYear: 2026, files: [] },
      processingStatus: 'IN_PROGRESS',
    } as RequestTaskPayload);

    fixture.detectChanges();

    const spinner = fixture.debugElement.query(By.css('cca-loading-spinner'));
    expect(spinner).not.toBeNull();
    expect(spinner.nativeElement.textContent).toContain('Your files are being uploaded');
  });

  it('should render the return to dashboard link during IN_PROGRESS', () => {
    store.setPayload({
      performanceAccountTemplateDataUpload: { targetYear: 2026, files: [] },
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

  it('should display the expired reporting period error for FPAT1004 on the upload field', () => {
    vi.mocked(tasksService.processRequestTaskAction).mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'FPAT1004' } })),
    );

    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(component.form.controls.uploadedFiles.errors).toEqual({
      responseError: 'The reporting period for the selected reporting year has expired - the workflow must be closed',
    });
    expect(component.form.invalid).toBe(true);
  });

  it('should display the generic upload error for unexpected errors', () => {
    vi.mocked(tasksService.processRequestTaskAction).mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 500, error: { code: 'UNKNOWN' } })),
    );

    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(component.form.controls.uploadedFiles.errors).toEqual({
      responseError:
        'There was a problem with the upload. Please try again or contact cca-help@environment-agency.gov.uk',
    });
  });

  it('should display the generic upload error when the error response has an empty body', () => {
    vi.mocked(tasksService.processRequestTaskAction).mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 500 })),
    );

    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(component.form.controls.uploadedFiles.errors).toEqual({
      responseError:
        'There was a problem with the upload. Please try again or contact cca-help@environment-agency.gov.uk',
    });
  });

  it('should render a single error summary when submitting again after an upload error', () => {
    vi.mocked(tasksService.processRequestTaskAction).mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'FPAT1004' } })),
    );

    // first submit: form is valid, the API rejects it and flags the control
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    // second submit: form is invalid, the wizard shows its built-in summary
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    const summaries = fixture.nativeElement.querySelectorAll('.govuk-error-summary');

    expect(summaries.length).toBe(1);
    expect(summaries[0].textContent).toContain(
      'The reporting period for the selected reporting year has expired - the workflow must be closed',
    );
  });
});
