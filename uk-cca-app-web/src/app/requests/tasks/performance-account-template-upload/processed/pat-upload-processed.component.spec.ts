import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { TasksService } from 'cca-api';

import { mockRequestTaskPATState } from '../testing/mock-data';
import { PatUploadProcessedComponent } from './pat-upload-processed.component';

describe('PatProcessedComponent', () => {
  let component: PatUploadProcessedComponent;
  let fixture: ComponentFixture<PatUploadProcessedComponent>;
  let store: RequestTaskStore;

  const tasksService: Partial<jest.Mocked<TasksService>> = {
    processRequestTaskAction: jest.fn().mockReturnValue(of(mockRequestTaskPATState)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatUploadProcessedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState({
      ...mockRequestTaskPATState,
      requestTaskItem: {
        ...mockRequestTaskPATState.requestTaskItem,
        requestTask: {
          ...mockRequestTaskPATState.requestTaskItem.requestTask,
          payload: {
            payloadType: 'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT_PAYLOAD',
            sendEmailNotification: true,
            targetPeriodType: 'TP6',
            processingStatus: 'COMPLETED',
            reportPackages: ['b78c03fa-70bd-4c63-8ddb-b1ffd08310b0'],
            fileReports: {
              accountFileReports: {
                1: {
                  accountId: 1,
                  accountBusinessId: 'ADS_1-T00001',
                  succeeded: true,
                  file: {
                    name: 'ADS_1-T00001_PAT_TP6.xlsx',
                    uuid: 'cba157d4-6bab-4310-b12f-1503ad960aa4',
                  },
                  errorFilenames: [],
                  errors: [],
                },
              },
              notAccountRelatedFileErrors: [],
              numberOfFilesSucceeded: 1,
              numberOfFilesFailed: 0,
            },
            csvReportFile: {
              name: 'ADS_1-PATUL-7_Summary.csv',
              uuid: '1a61fee7-4aa0-4f67-9e23-d45da20842e7',
            },
            uploadAttachments: {
              'b78c03fa-70bd-4c63-8ddb-b1ffd08310b0': 'ADS_1-T00001_PAT_TP6.zip',
            },
          },
        },
      },
    });
    fixture = TestBed.createComponent(PatUploadProcessedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display an error message when errorMessage exists', () => {
    store.setState({
      ...mockRequestTaskPATState,
      requestTaskItem: {
        ...mockRequestTaskPATState.requestTaskItem,
        requestTask: {
          ...mockRequestTaskPATState.requestTaskItem.requestTask,
          payload: {
            payloadType: 'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT_PAYLOAD',
            sendEmailNotification: true,
            targetPeriodType: 'TP6',
            processingStatus: 'COMPLETED',
            errorType: 'CSV_GENERATION_FAILED',
          },
        },
      },
    });

    fixture.detectChanges();

    const errorSummary = fixture.debugElement.query(By.css('.govuk-error-summary'));
    expect(errorSummary).not.toBeNull();
    expect(errorSummary.nativeElement.textContent).toContain('There is a problem');
    expect(errorSummary.nativeElement.textContent).toContain(
      'Files could not be uploaded There was a problem with the upload, contact cca-help@environment-agency.gov.uk.',
    );
  });

  it('should display success notification banner when there is no errorMessage', () => {
    const notificationBanner = fixture.debugElement.query(By.css('govuk-notification-banner'));
    expect(notificationBanner).not.toBeNull();
    expect(notificationBanner.nativeElement.textContent).toContain('Files have been uploaded');
  });

  it('should render the summary component', () => {
    const summaryComponent = fixture.debugElement.query(By.css('cca-summary'));
    expect(summaryComponent).not.toBeNull();
  });
});
