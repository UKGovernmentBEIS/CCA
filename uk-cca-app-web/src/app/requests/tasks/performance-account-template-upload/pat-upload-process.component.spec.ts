import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getByTestId, getByText } from '@testing';

import { TasksService } from 'cca-api';

import { PATUploadProcessComponent } from './pat-upload-process.component';
import { mockRequestTaskPATState } from './testing/mock-data';

describe('PatAccountTemplateUploadComponent', () => {
  let component: PATUploadProcessComponent;
  let fixture: ComponentFixture<PATUploadProcessComponent>;
  let store: RequestTaskStore;

  const tasksService: Partial<jest.Mocked<TasksService>> = {
    processRequestTaskAction: jest.fn().mockReturnValue(of(mockRequestTaskPATState)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PATUploadProcessComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            queryParamMap: of(convertToParamMap({ taskId: '1' })),
          }),
        },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskPATState);

    fixture = TestBed.createComponent(PATUploadProcessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the file upload component', () => {
    const fileInput = fixture.debugElement.query(By.css('cca-multiple-file-input'));
    expect(fileInput).not.toBeNull();
  });

  it('should render and expand the govuk-details component', async () => {
    const details = getByTestId('pat-upload-details');
    expect(details).toBeTruthy();

    click(details);
    fixture.detectChanges();

    expect(getByText(/To upload your Performance Account Template \(PAT\) spreadsheets:/i)).toBeTruthy();
  });

  it('should render the processing component when processingStatus is IN_PROGRESS', () => {
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
            processingStatus: 'IN_PROGRESS',
          },
        },
      },
    });

    fixture.detectChanges();

    const banner = getByTestId('upload-processing-banner');
    expect(banner).toBeTruthy();
  });
});
