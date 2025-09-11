import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { HistoryCategory } from '@shared/types';

import { RequestsService } from 'cca-api';

import { mockRequestDetailsSearchResultsData } from './testing/mock-data';
import { WorkflowHistoryTabComponent } from './workflow-history-tab.component';
import { WorkflowHistoryTabFormProvider } from './workflow-history-tab-form.provider';

describe('WorkflowHistoryTabComponent', () => {
  let component: WorkflowHistoryTabComponent;
  let fixture: ComponentFixture<WorkflowHistoryTabComponent>;
  let requestsService: jest.Mocked<RequestsService>;
  let routerNavigateSpy: jest.SpyInstance;

  const createQueryParamMock = (page = '1', pageSize = '30') => ({
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
        paramMap: { get: jest.fn().mockReturnValue('test-sector-id') },
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

    routerNavigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    requestsService = TestBed.inject(RequestsService) as jest.Mocked<RequestsService>;

    fixture.detectChanges();
  });

  afterEach(() => {
    routerNavigateSpy.mockClear();
  });

  it('should fetch workflow history on initialization with correct parameters', () => {
    expect(requestsService.getRequestDetailsByResource).toHaveBeenCalledWith({
      resourceType: 'SECTOR_ASSOCIATION',
      resourceId: 'test-sector-id',
      historyCategory: HistoryCategory.SECTOR,
      pageNumber: 0,
      pageSize: 30,
      requestTypes: [],
      requestStatuses: [],
    });
  });

  it('should update state and render workflow items', () => {
    expect(component.state().workflowsHistory).toEqual(mockRequestDetailsSearchResultsData);
    expect(component.state().totalItems).toBe(2);

    const listItems = fixture.debugElement.queryAll(By.css('.search-results-list_item'));
    expect(listItems.length).toBe(2);
  });

  it('should navigate when form filters change', () => {
    component.filtersForm.patchValue({
      requestTypes: ['SECTOR_MOA'],
      requestStatuses: [],
    });

    expect(routerNavigateSpy).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: expect.objectContaining({
          requestTypes: ['SECTOR_MOA'],
        }),
      }),
    );
  });

  it('should navigate when page changes', () => {
    component.onPageChange(3);

    expect(routerNavigateSpy).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: expect.objectContaining({ page: 3 }),
      }),
    );
  });

  it('should not navigate when page or page size values remain the same', () => {
    component.onPageChange(1);
    component.onPageSizeChange(30);

    expect(routerNavigateSpy).not.toHaveBeenCalled();
  });
});
