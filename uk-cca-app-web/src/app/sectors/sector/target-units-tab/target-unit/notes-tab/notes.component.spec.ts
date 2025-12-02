import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { Router } from '@angular/router';

import { of } from 'rxjs';

import { mockClass } from '@netz/common/testing';

import { AccountNoteDto, AccountNotesService } from 'cca-api';

import { NotesComponent } from './notes.component';

describe('NotesComponent', () => {
  let component: NotesComponent;
  let fixture: ComponentFixture<NotesComponent>;
  let accountNotesService: jest.Mocked<AccountNotesService>;
  let router: Router;

  const mockNotes: AccountNoteDto[] = [
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
    const mockAccountNotesService = mockClass(AccountNotesService);
    mockAccountNotesService.getNotesByAccountId = jest.fn().mockReturnValue(
      of({
        accountNotes: mockNotes,
        totalItems: 2,
      }),
    );

    await TestBed.configureTestingModule({
      imports: [NotesComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: AccountNotesService, useValue: mockAccountNotesService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ targetUnitId: '123' }),
            },
            queryParamMap: of(convertToParamMap({})),
          },
        },
      ],
    }).compileComponents();

    accountNotesService = TestBed.inject(AccountNotesService) as jest.Mocked<AccountNotesService>;
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(NotesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch notes on init with default pagination', () => {
    expect(accountNotesService.getNotesByAccountId).toHaveBeenCalledWith(123, 0, 10);
  });

  it('should display notes', () => {
    expect(component['notes']().length).toBe(2);
    expect(component['hasNotes']()).toBe(true);
  });

  it('should handle empty notes', async () => {
    accountNotesService.getNotesByAccountId.mockReturnValue(
      of({
        accountNotes: [],
        totalItems: 0,
      }),
    );

    const emptyFixture = TestBed.createComponent(NotesComponent);
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

  it('should have correct targetUnitId from route', () => {
    expect(component['targetUnitId']).toBe(123);
  });

  it('should handle pagination from query params', async () => {
    const routeWithParams = {
      snapshot: {
        paramMap: convertToParamMap({ targetUnitId: '123' }),
      },
      queryParamMap: of(convertToParamMap({ page: '3', pageSize: '20' })),
    };

    await TestBed.resetTestingModule();

    const mockAccountNotesServiceForPagination = mockClass(AccountNotesService);
    mockAccountNotesServiceForPagination.getNotesByAccountId = jest.fn().mockReturnValue(
      of({
        accountNotes: mockNotes,
        totalItems: 2,
      }),
    );

    await TestBed.configureTestingModule({
      imports: [NotesComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: AccountNotesService, useValue: mockAccountNotesServiceForPagination },
        { provide: ActivatedRoute, useValue: routeWithParams },
      ],
    }).compileComponents();

    const paginatedFixture = TestBed.createComponent(NotesComponent);
    paginatedFixture.detectChanges();

    expect(mockAccountNotesServiceForPagination.getNotesByAccountId).toHaveBeenCalledWith(123, 2, 20);
  });
});
