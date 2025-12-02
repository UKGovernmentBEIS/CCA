import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestsService } from 'cca-api';

import { mockRequestDetailsSearchResultsData } from '../testing/mock-data';
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
        params: { facilityId: '1' },
        queryParamMap: queryParamMock,
        fragment: null,
      },
      queryParamMap: of(queryParamMock),
    };

    await TestBed.configureTestingModule({
      imports: [WorkflowHistoryTabComponent],
      providers: [
        { provide: RequestsService, useValue: requestsServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
      ],
    }).compileComponents();

    routerNavigateSpy = jest.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    requestsService = TestBed.inject(RequestsService) as jest.Mocked<RequestsService>;

    fixture = TestBed.createComponent(WorkflowHistoryTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    routerNavigateSpy.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch workflow history on initialization with correct parameters', () => {
    expect(requestsService.getRequestDetailsByResource).toHaveBeenCalledWith({
      historyCategory: 'FACILITY',
      pageNumber: 0,
      pageSize: 10,
      requestStatuses: [],
      requestTypes: [],
      resourceId: '1',
      resourceType: 'FACILITY',
    });
  });

  it('should update state and render workflow items', () => {
    expect(component.state().workflowsHistory).toEqual(mockRequestDetailsSearchResultsData);
    expect(component.state().totalItems).toBe(1);

    const listItems = fixture.debugElement.queryAll(By.css('.search-results-list_item'));
    expect(listItems.length).toBe(1);
  });

  it('should navigate when form filters change', () => {
    component.filtersForm.patchValue({
      requestTypes: ['FACILITY'],
      requestStatuses: [],
    });

    expect(routerNavigateSpy).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: expect.objectContaining({
          requestTypes: ['FACILITY'],
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
    component.onPageSizeChange(10);

    expect(routerNavigateSpy).not.toHaveBeenCalled();
  });
});
