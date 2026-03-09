import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { HistoryCategory } from '@shared/types';

import { RequestsService } from 'cca-api';

import { WorkflowHistoryTabFormProvider } from '../../../workflow-history-tab/workflow-history-tab-form.provider';
import { mockRequestDetailsSearchResultsData } from './testing/mock-data';
import { WorkflowHistoryTabComponent } from './workflow-history-tab.component';

describe('WorkflowHistoryTabComponent', () => {
  let component: WorkflowHistoryTabComponent;
  let fixture: ComponentFixture<WorkflowHistoryTabComponent>;
  let requestsService: jest.Mocked<RequestsService>;
  let routerNavigateSpy: jest.SpyInstance;

  const createQueryParamMock = (page = '1', pageSize = '10') => ({
    get: jest.fn().mockImplementation((param) => {
      if (param === 'page') return page;
      if (param === 'pageSize') return pageSize;
      return null;
    }),
    getAll: jest.fn().mockReturnValue([]),
  });

  beforeEach(async () => {
    const requestsServiceMock = {
      getRequestDetailsByResource: jest.fn().mockReturnValue(of(mockRequestDetailsSearchResultsData)),
    };

    const queryParamMock = createQueryParamMock();
    const activatedRouteMock = {
      snapshot: {
        paramMap: { get: jest.fn().mockReturnValue('test-target-unit-id') },
        queryParamMap: queryParamMock,
        fragment: null,
      },
      queryParamMap: of(queryParamMock),
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, WorkflowHistoryTabComponent],
      providers: [
        { provide: RequestsService, useValue: requestsServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        WorkflowHistoryTabFormProvider,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkflowHistoryTabComponent);
    component = fixture.componentInstance;
    requestsService = TestBed.inject(RequestsService) as jest.Mocked<RequestsService>;
    routerNavigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch workflow history on initialization with correct parameters', () => {
    expect(requestsService.getRequestDetailsByResource).toHaveBeenCalledWith({
      resourceType: 'ACCOUNT',
      resourceId: 'test-target-unit-id',
      historyCategory: HistoryCategory.UNA,
      pageNumber: 0,
      pageSize: 10,
      requestTypes: [],
      requestStatuses: [],
    });
  });

  it('should update state with workflow items', () => {
    expect(component.state()).toEqual(
      expect.objectContaining({
        workflowsHistory: mockRequestDetailsSearchResultsData,
        totalItems: mockRequestDetailsSearchResultsData.total,
      }),
    );
  });

  it('should handle form filter changes', fakeAsync(() => {
    component.filtersForm.patchValue({
      requestTypes: ['TARGET_UNIT_APPLICATION'],
      requestStatuses: ['DRAFT'],
    });

    tick();

    expect(routerNavigateSpy).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: {
          requestTypes: ['TARGET_UNIT_APPLICATION'],
          requestStatuses: ['DRAFT'],
        },
        queryParamsHandling: 'merge',
        relativeTo: expect.any(Object),
        fragment: null,
      }),
    );
  }));

  it('should handle page changes', () => {
    component.onPageChange(2);

    expect(routerNavigateSpy).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: expect.objectContaining({ page: 2 }),
        queryParamsHandling: 'merge',
        relativeTo: expect.any(Object),
        fragment: null,
      }),
    );
  });

  it('should handle page size changes', () => {
    component.onPageSizeChange(20);

    expect(routerNavigateSpy).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: expect.objectContaining({
          pageSize: 20,
        }),
        queryParamsHandling: 'merge',
        relativeTo: expect.any(Object),
        fragment: null,
      }),
    );
  });

  it('should not navigate when current page is selected', () => {
    component.onPageChange(1);
    expect(routerNavigateSpy).not.toHaveBeenCalled();
  });

  it('should not navigate when current page size is selected', () => {
    component.onPageSizeChange(10);
    expect(routerNavigateSpy).not.toHaveBeenCalled();
  });
});
