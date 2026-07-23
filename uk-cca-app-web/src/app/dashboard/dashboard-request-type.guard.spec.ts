import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap, Router, UrlTree } from '@angular/router';

import { AuthStore } from '@netz/common/auth';

import { dashboardRequestTypeGuard } from './dashboard-request-type.guard';

describe('dashboardRequestTypeGuard', () => {
  let authStore: AuthStore;
  let router: { createUrlTree: ReturnType<typeof vi.fn> };

  const runGuard = (queryParams: Record<string, string>, fragment: string | null = null) => {
    const route = {
      queryParamMap: convertToParamMap(queryParams),
      queryParams,
      fragment,
    } as ActivatedRouteSnapshot;

    return TestBed.runInInjectionContext(() => dashboardRequestTypeGuard(route, {} as never));
  };

  beforeEach(() => {
    router = { createUrlTree: vi.fn().mockReturnValue(new UrlTree()) };
    TestBed.configureTestingModule({
      providers: [{ provide: Router, useValue: router }],
    });
    authStore = TestBed.inject(AuthStore);
  });

  it('should allow navigation without a request type', () => {
    authStore.setUserState({ roleType: 'OPERATOR' });

    expect(runGuard({ searchTerm: 'steel' })).toBe(true);
  });

  it('should allow a request type available to the current role', () => {
    authStore.setUserState({ roleType: 'REGULATOR' });

    expect(runGuard({ requestType: 'ADMIN_TERMINATION' })).toBe(true);
  });

  it('should remove a request type unavailable to the current role', () => {
    authStore.setUserState({ roleType: 'SECTOR_USER' });

    const result = runGuard(
      { requestType: 'ADMIN_TERMINATION', searchTerm: 'steel', orderBy: 'OLDEST_FIRST' },
      'unassigned',
    );

    expect(result).toBeInstanceOf(UrlTree);
    expect(router.createUrlTree).toHaveBeenCalledWith(['/dashboard'], {
      queryParams: {
        requestType: null,
        searchTerm: 'steel',
        orderBy: 'OLDEST_FIRST',
      },
      fragment: 'unassigned',
    });
  });

  it('should remove request types for unsupported roles', () => {
    authStore.setUserState({ roleType: 'OPERATOR' });

    expect(runGuard({ requestType: 'UNDERLYING_AGREEMENT' })).toBeInstanceOf(UrlTree);
  });
});
