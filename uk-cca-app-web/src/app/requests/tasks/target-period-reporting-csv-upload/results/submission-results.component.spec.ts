import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';
import { Mocked } from 'vitest';

import { RequestTaskItemDTO, RequestTaskPayload, TasksService } from 'cca-api';

import { SubmissionResultsComponent } from './submission-results.component';

describe('SubmissionResultsComponent', () => {
  let component: SubmissionResultsComponent;
  let fixture: ComponentFixture<SubmissionResultsComponent>;
  let store: RequestTaskStore;
  let authStore: AuthStore;

  const tasksService: Partial<Mocked<TasksService>> = {
    processRequestTaskAction: vi.fn().mockReturnValue(of(null)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubmissionResultsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    store.setRequestTaskItem({
      requestTask: {
        id: 856,
        assigneeUserId: '7b91199c-4770-4d4b-a0ed-d6d9667de157',
        payload: {
          payloadType: 'PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_SUBMIT_PAYLOAD',
          performanceDataUpload: {
            targetPeriodType: 'TP7',
            reportType: 'FINAL',
            files: ['5a773a53-01ad-4c8e-ba9b-bca0560e926d'],
          },
          processingStatus: 'COMPLETED',
          results: {
            totalFilesUploaded: 1,
            facilitiesSucceeded: 0,
            facilitiesFailed: 1,
            uploadSummaryFile: 'b0939e73-6c89-4663-9ba6-3a3ae104f8bc',
            submittedDate: '2026-06-30T12:49:27.727602514Z',
          },
          uploadAttachments: {
            '5a773a53-01ad-4c8e-ba9b-bca0560e926d': 'dummy.csv',
            'b0939e73-6c89-4663-9ba6-3a3ae104f8bc': 'Upload_Summary.csv',
          },
        },
      },
    } as RequestTaskItemDTO);
    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ userId: '7b91199c-4770-4d4b-a0ed-d6d9667de157', roleType: 'SECTOR_USER' });

    fixture = TestBed.createComponent(SubmissionResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct summary data', () => {
    const summaryValues = getSummaryListData(fixture.nativeElement);
    const [keys, values] = summaryValues[0];

    expect(keys).toEqual([
      'Time submitted',
      'Files uploaded',
      'Facilities successful',
      'Facilities failed',
      'Submission summary file',
    ]);

    // Date is timezone-dependent, just verify it matches the expected pattern
    expect(values[0]).toMatch(/\d{1,2} \w{3} \d{4} - \d{2}:\d{2}:\d{2}/);
    expect(values[1]).toBe('1');
    expect(values[2]).toBe('0');
    expect(values[3]).toBe('1');
    expect(values[4]).toBe('Upload_Summary.csv');
  });

  it('should display an error message when errorMessage exists', async () => {
    store.setPayload({
      errorMessage: 'CSV_FAILED',
    } as RequestTaskPayload);

    fixture.detectChanges();

    const errorSummary = fixture.debugElement.query(By.css('.govuk-error-summary'));
    expect(errorSummary).not.toBeNull();
    expect(errorSummary.nativeElement.textContent).toContain('There is a problem');
    expect(errorSummary.nativeElement.textContent).toContain('Files have been uploaded');
  });

  it('should render the Complete button', () => {
    const completeButton = fixture.debugElement.query(By.css('button'));
    expect(completeButton).not.toBeNull();
    expect(completeButton.nativeElement.textContent.trim()).toBe('Finish task');
  });

  it('should render the warning message', () => {
    const warning = fixture.debugElement.query(By.css('.govuk-warning-text'));
    expect(warning).not.toBeNull();
    expect(warning.nativeElement.textContent).toContain(
      "You must select 'Finish task' before you can start another reporting task for this target period",
    );
  });
});
