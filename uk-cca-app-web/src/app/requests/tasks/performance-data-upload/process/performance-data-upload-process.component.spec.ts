import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getByTestId, getByText } from '@testing';
import { Mocked } from 'vitest';

import { TasksService } from 'cca-api';

import { mockRequestTaskStatePerformanceDataUploadState } from '../testing/mock-data';
import { PerformanceDataUploadProcessComponent } from './performance-data-upload-process.component';

describe('PerformanceDataUploadProcessComponent', () => {
  let component: PerformanceDataUploadProcessComponent;
  let fixture: ComponentFixture<PerformanceDataUploadProcessComponent>;
  let store: RequestTaskStore;
  let authStore: AuthStore;

  const tasksService: Partial<Mocked<TasksService>> = {
    processRequestTaskAction: vi.fn().mockReturnValue(of(mockRequestTaskStatePerformanceDataUploadState)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerformanceDataUploadProcessComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: TasksService, useValue: tasksService },
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            queryParamMap: of(convertToParamMap({ term: null, page: '1' })),
          }),
        },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskStatePerformanceDataUploadState);
    store.setRequestTaskItem({ requestTask: { id: 856, assigneeUserId: '7b91199c-4770-4d4b-a0ed-d6d9667de157' } });

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ userId: '7b91199c-4770-4d4b-a0ed-d6d9667de157', roleType: 'SECTOR_USER' });

    fixture = TestBed.createComponent(PerformanceDataUploadProcessComponent);
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

  it('should render a warning message', () => {
    const warning = fixture.debugElement.query(By.css('.govuk-warning-text'));
    expect(warning.nativeElement.textContent).toContain(
      'The uploaded spreadsheet needs to have the same filename as the download',
    );
  });

  it('should render and expand the govuk-details component', async () => {
    const details = getByTestId('performance-upload-details');
    expect(details).toBeTruthy();
    click(details);
    fixture.detectChanges();
    expect(getByText(/To upload your target period reporting spreadsheets/i)).toBeTruthy();
  });
});
