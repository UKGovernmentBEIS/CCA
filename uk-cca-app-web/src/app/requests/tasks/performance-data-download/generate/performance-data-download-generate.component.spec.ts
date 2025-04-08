import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { TasksService } from 'cca-api';

import { PerformanceDataDownloadPayload } from '../../../common/performance-data/performance-data.types';
import { mockRequestTaskStatePerformanceDataDL, performanceDataDLPayload } from '../testing/mock-data';
import { PerformanceDataDownloadGenerateComponent } from './performance-data-download-generate.component';

describe('PerformanceDataDownloadGenerateComponent', () => {
  let component: PerformanceDataDownloadGenerateComponent;
  let fixture: ComponentFixture<PerformanceDataDownloadGenerateComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const tasksService: Partial<jest.Mocked<TasksService>> = {
    processRequestTaskAction: jest.fn().mockReturnValue(of(mockRequestTaskStatePerformanceDataDL)),
  };

  class Page extends BasePage<PerformanceDataDownloadGenerateComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [
          ...(Array.from(row.querySelectorAll('dt')) ?? []),
          ...(Array.from(row.querySelectorAll('dd')) ?? []),
        ])
        .map((pair) => pair.map((element) => element?.textContent?.trim()));
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerformanceDataDownloadGenerateComponent],
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
    store.setState({
      ...mockRequestTaskStatePerformanceDataDL,
      requestTaskItem: {
        ...mockRequestTaskStatePerformanceDataDL.requestTaskItem,
        requestTask: {
          ...mockRequestTaskStatePerformanceDataDL.requestTaskItem.requestTask,
          payload: {
            ...performanceDataDLPayload,
            targetPeriodType: null,
          } as PerformanceDataDownloadPayload,
        },
      },
    });

    fixture = TestBed.createComponent(PerformanceDataDownloadGenerateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show details', () => {
    expect(page.summaryListValues).toEqual([
      ['Sector ID', 'ADS_2'],
      ['Sector name', 'Aerospace_2'],
      ['Target period', 'Select\n TP6'],
    ]);
  });

  it('should submit', () => {
    const tasksServiceSpy = jest.spyOn(tasksService, 'processRequestTaskAction');

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksServiceSpy).toHaveBeenCalledWith({
      requestTaskActionType: 'PERFORMANCE_DATA_DOWNLOAD_GENERATE',
      requestTaskId: 281,
      requestTaskActionPayload: {
        payloadType: 'PERFORMANCE_DATA_DOWNLOAD_GENERATE_PAYLOAD',
        targetPeriodType: 'TP6',
      },
    });
  });
});
