import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { PerformanceDataUploadSubmitRequestTaskPayload, TasksService } from 'cca-api';

import { mockRequestTaskStatePerformanceDataUploadState, performanceDataUploadPayload } from '../../testing/mock-data';
import { PerformanceDataUploadProcessedComponent } from './performance-data-upload-processed.component';

describe('PerformanceDataUploadProcessedComponent', () => {
  let component: PerformanceDataUploadProcessedComponent;
  let fixture: ComponentFixture<PerformanceDataUploadProcessedComponent>;
  let store: RequestTaskStore;

  const tasksService: Partial<jest.Mocked<TasksService>> = {
    processRequestTaskAction: jest.fn().mockReturnValue(of(mockRequestTaskStatePerformanceDataUploadState)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerformanceDataUploadProcessedComponent],
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
      ...mockRequestTaskStatePerformanceDataUploadState,
      requestTaskItem: {
        ...mockRequestTaskStatePerformanceDataUploadState.requestTaskItem,
        requestTask: {
          ...mockRequestTaskStatePerformanceDataUploadState.requestTaskItem.requestTask,
          payload: {
            ...performanceDataUploadPayload,
            performanceDataUpload: {
              performanceDataTargetPeriodType: 'TP6',
              reportPackages: ['mock-report-1'],
            },
            accountReports: {
              48: {
                accountId: 48,
                accountBusinessId: 'ADS_2-T00029',
                file: {
                  name: 'ADS_2-T00029_TPR_TP6_V1.xlsx',
                  uuid: 'mock-uuid-1',
                },
                succeeded: true,
                errors: [],
              },
              49: {
                accountId: 49,
                accountBusinessId: 'ADS_2-T00030',
                file: {
                  name: 'ADS_2-T00030_TPR_TP6_V1.xlsx',
                  uuid: 'mock-uuid-2',
                },
                succeeded: true,
                errors: [],
              },
              50: {
                accountId: 50,
                accountBusinessId: 'ADS_2-T00031',
                file: {
                  name: 'ADS_2-T00031_TPR_TP6_V1.xlsx',
                  uuid: 'mock-uuid-3',
                },
                succeeded: false,
                errors: ['Invalid file format'],
              },
            },
            csvFile: { name: 'csv-file-name', uuid: 'uuid' },
            processCompleted: true,
          } as PerformanceDataUploadSubmitRequestTaskPayload,
        },
      },
    });

    fixture = TestBed.createComponent(PerformanceDataUploadProcessedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display an error message when errorMessage exists', () => {
    store.setState({
      ...mockRequestTaskStatePerformanceDataUploadState,
      requestTaskItem: {
        ...mockRequestTaskStatePerformanceDataUploadState.requestTaskItem,
        requestTask: {
          ...mockRequestTaskStatePerformanceDataUploadState.requestTaskItem.requestTask,
          payload: {
            ...performanceDataUploadPayload,
            errorMessage: 'GENERATE_CSV_FAILED',
          } as PerformanceDataUploadSubmitRequestTaskPayload,
        },
      },
    });

    fixture.detectChanges();

    const errorSummary = fixture.debugElement.query(By.css('.govuk-error-summary'));
    expect(errorSummary).not.toBeNull();
    expect(errorSummary.nativeElement.textContent).toContain('There is a problem');
    expect(errorSummary.nativeElement.textContent).toContain(
      'Failed to generate error csv file There was a problem with the upload, contact cca-help@environment-agency.gov.uk.',
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
