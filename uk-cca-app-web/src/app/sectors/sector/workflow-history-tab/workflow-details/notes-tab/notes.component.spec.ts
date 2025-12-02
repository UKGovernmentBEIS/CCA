import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { Router } from '@angular/router';

import { of } from 'rxjs';

import { mockClass } from '@netz/common/testing';

import { RequestNoteDto, RequestNotesService } from 'cca-api';

import { WorkflowNotesComponent } from './notes.component';

describe('WorkflowNotesComponent', () => {
  let component: WorkflowNotesComponent;
  let fixture: ComponentFixture<WorkflowNotesComponent>;
  let requestNotesService: jest.Mocked<RequestNotesService>;
  let router: Router;

  const mockNotes: RequestNoteDto[] = [
    {
      id: 1,
      submitter: 'John Doe',
      lastUpdatedOn: '2024-01-15T10:30:00Z',
      payload: {
        note: 'First test note',
        files: {
          'uuid-1': 'document1.pdf',
          'uuid-2': 'document2.pdf',
        },
      },
    },
    {
      id: 2,
      submitter: 'Jane Smith',
      lastUpdatedOn: '2024-01-16T14:45:00Z',
      payload: {
        note: 'Second test note without files',
        files: null,
      },
    },
  ];

  beforeEach(async () => {
    const mockRequestNotesService = mockClass(RequestNotesService);
    mockRequestNotesService.getNotesByRequestId = jest.fn().mockReturnValue(
      of({
        requestNotes: mockNotes,
        totalItems: 2,
      }),
    );

    await TestBed.configureTestingModule({
      imports: [WorkflowNotesComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: RequestNotesService, useValue: mockRequestNotesService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ workflowId: '456' }),
            },
            queryParamMap: of(convertToParamMap({})),
          },
        },
      ],
    }).compileComponents();

    requestNotesService = TestBed.inject(RequestNotesService) as jest.Mocked<RequestNotesService>;
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(WorkflowNotesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch notes on init with default pagination', () => {
    expect(requestNotesService.getNotesByRequestId).toHaveBeenCalledWith('456', 0, 10);
  });

  it('should display notes', () => {
    expect(component['notes']().length).toBe(2);
    expect(component['hasNotes']()).toBe(true);
  });

  it('should handle empty notes', async () => {
    requestNotesService.getNotesByRequestId.mockReturnValue(
      of({
        requestNotes: [],
        totalItems: 0,
      }),
    );

    const emptyFixture = TestBed.createComponent(WorkflowNotesComponent);
    const emptyComponent = emptyFixture.componentInstance;
    emptyFixture.detectChanges();

    expect(emptyComponent['hasNotes']()).toBe(false);
  });

  it('should navigate with correct query params on page change', () => {
    component.onPageChange(2);

    expect(router.navigate).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: { page: 2 },
        queryParamsHandling: 'merge',
        fragment: 'notes',
      }),
    );
  });

  it('should not navigate if page is the same', () => {
    component.onPageChange(1);

    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should navigate with correct query params on page size change', () => {
    component.onPageSizeChange(20);

    expect(router.navigate).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: { page: 1, pageSize: 20 },
        queryParamsHandling: 'merge',
        fragment: 'notes',
      }),
    );
  });

  it('should not navigate if page size is the same', () => {
    component.onPageSizeChange(10);

    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should have correct workflowId from route', () => {
    expect(component['workflowId']).toBe('456');
  });

  it('should handle pagination from query params', async () => {
    const routeWithParams = {
      snapshot: {
        paramMap: convertToParamMap({ workflowId: '456' }),
      },
      queryParamMap: of(convertToParamMap({ page: '3', pageSize: '20' })),
    };

    await TestBed.resetTestingModule();

    const mockRequestNotesServiceForPagination = mockClass(RequestNotesService);
    mockRequestNotesServiceForPagination.getNotesByRequestId = jest.fn().mockReturnValue(
      of({
        requestNotes: mockNotes,
        totalItems: 2,
      }),
    );

    await TestBed.configureTestingModule({
      imports: [WorkflowNotesComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: RequestNotesService, useValue: mockRequestNotesServiceForPagination },
        { provide: ActivatedRoute, useValue: routeWithParams },
      ],
    }).compileComponents();

    const paginatedFixture = TestBed.createComponent(WorkflowNotesComponent);
    paginatedFixture.detectChanges();

    expect(mockRequestNotesServiceForPagination.getNotesByRequestId).toHaveBeenCalledWith('456', 2, 20);
  });
});
