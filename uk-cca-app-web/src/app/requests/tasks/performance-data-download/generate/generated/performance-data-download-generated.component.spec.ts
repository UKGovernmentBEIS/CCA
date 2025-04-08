import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { TasksService } from 'cca-api';

import { PerformanceDataDownloadPayload } from '../../../../common/performance-data/performance-data.types';
import { mockRequestTaskStatePerformanceDataDL, performanceDataDLPayload } from '../../testing/mock-data';
import { PerformanceDataDownloadGeneratedComponent } from './performance-data-download-generated.component';

describe('FileHasBeenGeneratedComponent', () => {
  let component: PerformanceDataDownloadGeneratedComponent;
  let fixture: ComponentFixture<PerformanceDataDownloadGeneratedComponent>;
  let router: Router;
  let store: RequestTaskStore;
  let page: Page;

  const tasksService: Partial<jest.Mocked<TasksService>> = {
    processRequestTaskAction: jest.fn().mockReturnValue(of(mockRequestTaskStatePerformanceDataDL)),
  };

  class Page extends BasePage<PerformanceDataDownloadGeneratedComponent> {
    get links() {
      return this.queryAll<HTMLLinkElement>('a').map((el) => el.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerformanceDataDownloadGeneratedComponent],
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
      ...mockRequestTaskStatePerformanceDataDL,
      requestTaskItem: {
        ...mockRequestTaskStatePerformanceDataDL.requestTaskItem,
        requestTask: {
          ...mockRequestTaskStatePerformanceDataDL.requestTaskItem.requestTask,
          payload: {
            ...performanceDataDLPayload,
            zipFile: {
              uuid: 'zip',
              name: 'ADS_2_TP6_reporting_templates.zip',
            },
            errorsFile: {
              uuid: 'csv',
              name: 'ADS_2_TP6_download_errors.csv',
            },
          } as PerformanceDataDownloadPayload,
        },
      },
    });

    fixture = TestBed.createComponent(PerformanceDataDownloadGeneratedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show files', () => {
    expect(page.links).toEqual([
      'cca-help@environment-agency.gov.uk',
      'ADS_2_TP6_reporting_templates.zip',
      'ADS_2_TP6_download_errors.csv',
    ]);
  });

  it('should submit', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const tasksServiceSpy = jest.spyOn(tasksService, 'processRequestTaskAction');

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksServiceSpy).toHaveBeenCalledWith({
      requestTaskActionType: 'PERFORMANCE_DATA_DOWNLOAD_COMPLETE',
      requestTaskId: 281,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['performance-data-download/confirmation'], expect.anything());
  });
});
