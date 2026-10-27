import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, convertToParamMap, ParamMap, provideRouter, Router } from '@angular/router';

import { BehaviorSubject, of, Subject, throwError } from 'rxjs';

import { Mocked } from 'vitest';

import { AccountSearchResults, TargetUnitAccountInfoViewService } from 'cca-api';

import { mockAccountSearchResults } from '../target-unit-accounts-list/testing/mock-data';
import { TargetUnitAccountSearchComponent } from './target-unit-account-search.component';

describe('TargetUnitAccountSearchComponent', () => {
  let component: TargetUnitAccountSearchComponent;
  let fixture: ComponentFixture<TargetUnitAccountSearchComponent>;
  let targetUnitAccountInfoViewService: Mocked<Partial<TargetUnitAccountInfoViewService>>;
  let routerMock: Router;
  let queryParamMap$: BehaviorSubject<ParamMap>;

  const statusText = () =>
    (fixture.debugElement.query(By.css('p[role="status"]')).nativeElement as HTMLElement).textContent.trim();

  beforeEach(async () => {
    queryParamMap$ = new BehaviorSubject<ParamMap>(convertToParamMap({ page: '1', pageSize: '50' }));
    targetUnitAccountInfoViewService = {
      searchUserAccounts: vi.fn().mockReturnValue(of(mockAccountSearchResults)),
    };

    const activatedRouteMock = {
      snapshot: {
        paramMap: {
          get: vi.fn().mockReturnValue(null),
        },
        queryParamMap: {
          get: vi.fn().mockImplementation((param) => {
            if (param === 'page') return '1';
            if (param === 'pageSize') return '50';
            return null;
          }),
        },
      },
      queryParamMap: queryParamMap$,
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, TargetUnitAccountSearchComponent],
      providers: [
        provideRouter([]),
        { provide: TargetUnitAccountInfoViewService, useValue: targetUnitAccountInfoViewService },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
      ],
    }).compileComponents();

    // Get router from TestBed after configuring module
    routerMock = TestBed.inject(Router);
    vi.spyOn(routerMock, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(TargetUnitAccountSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch accounts on initialization', () => {
    expect(targetUnitAccountInfoViewService.searchUserAccounts).toHaveBeenCalledWith({ page: 0, size: 50, term: null });
  });

  it('should initialize state with default values', () => {
    expect(component.currentPage()).toBe(1);
    expect(component.pageSize()).toBe(50);
    expect(component.count()).toBe(mockAccountSearchResults.total);
    expect(component.accounts()).toEqual(mockAccountSearchResults.accounts);
  });

  it('should render accounts list when data is available', () => {
    const accountsList = fixture.debugElement.query(By.css('[data-testid="target-unit-accounts-component"]'));
    expect(accountsList).toBeTruthy();
  });

  it('should render pagination when accounts are available', () => {
    const pagination = fixture.debugElement.query(By.css('cca-pagination'));
    expect(pagination).toBeTruthy();
  });

  it('should render the results status region when accounts are available', () => {
    const statusRegion = fixture.debugElement.query(By.css('p[role="status"]'));
    expect(statusRegion.nativeElement.textContent.trim()).toBe(`${mockAccountSearchResults.total} results`);
  });

  it('should use singular wording when a single account is returned', () => {
    targetUnitAccountInfoViewService.searchUserAccounts.mockReturnValue(
      of({ accounts: [mockAccountSearchResults.accounts[0]], total: 1 }),
    );

    const newFixture = TestBed.createComponent(TargetUnitAccountSearchComponent);
    newFixture.detectChanges();

    const statusRegion = newFixture.debugElement.query(By.css('p[role="status"]'));
    expect(statusRegion.nativeElement.textContent.trim()).toBe('1 result');
  });

  it('should render no results message when no accounts', () => {
    targetUnitAccountInfoViewService.searchUserAccounts.mockReturnValue(of({ accounts: [], total: 0 }));

    const newFixture = TestBed.createComponent(TargetUnitAccountSearchComponent);
    newFixture.detectChanges();

    const noResultsMessage = newFixture.debugElement.query(By.css('p[role="status"]'));
    expect(noResultsMessage.nativeElement.textContent.trim()).toBe('There are no results to show');
  });

  it('should clear the previous results until the new query responds', () => {
    const inFlight$ = new Subject<AccountSearchResults>();
    targetUnitAccountInfoViewService.searchUserAccounts.mockReturnValue(inFlight$);

    queryParamMap$.next(convertToParamMap({ term: 'target unit 01', page: '1', pageSize: '50' }));
    fixture.detectChanges();

    expect(statusText()).toBe('');
    expect(component.accounts()).toEqual([]);
    expect(fixture.debugElement.query(By.css('[data-testid="target-unit-accounts-component"]'))).toBeNull();

    inFlight$.next(mockAccountSearchResults);
    fixture.detectChanges();

    expect(statusText()).toBe(`${mockAccountSearchResults.total} results`);
    expect(component.accounts()).toEqual(mockAccountSearchResults.accounts);
  });

  it('should announce no results when a new query returns none', () => {
    const inFlight$ = new Subject<AccountSearchResults>();
    targetUnitAccountInfoViewService.searchUserAccounts.mockReturnValue(inFlight$);

    queryParamMap$.next(convertToParamMap({ term: 'nothing matches', page: '1', pageSize: '50' }));
    fixture.detectChanges();

    // Blank first, so the empty result set is announced as a change when it arrives.
    expect(statusText()).toBe('');

    inFlight$.next({ accounts: [], total: 0 });
    fixture.detectChanges();

    expect(statusText()).toBe('There are no results to show');
  });

  it('should perform search when form is submitted with valid data', () => {
    component.searchForm.patchValue({ term: 'test search' });
    component.onSearch();

    expect(routerMock.navigate).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: { term: 'test search', page: 1 },
        queryParamsHandling: 'merge',
      }),
    );
  });

  it('should not perform search when form is invalid', () => {
    vi.mocked(routerMock.navigate).mockClear();

    component.searchForm.patchValue({ term: 'ab' }); // Too short
    component.onSearch();

    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('should handle page change', () => {
    component.onPageChange(2);

    expect(routerMock.navigate).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: { page: 2 },
        queryParamsHandling: 'merge',
      }),
    );
  });

  it('should handle page size change and reset to page 1', () => {
    component.onPageSizeChange(25);

    expect(routerMock.navigate).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: { page: 1, pageSize: 25 },
        queryParamsHandling: 'merge',
      }),
    );
  });

  it('should handle API error gracefully', () => {
    targetUnitAccountInfoViewService.searchUserAccounts.mockReturnValue(throwError(() => new Error('API Error')));

    const newFixture = TestBed.createComponent(TargetUnitAccountSearchComponent);
    newFixture.detectChanges();

    // Component should handle error silently and show empty results
    expect(newFixture.componentInstance.accounts()).toEqual([]);
    expect(newFixture.componentInstance.count()).toBe(0);
  });
});
