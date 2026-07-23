/* eslint-disable @typescript-eslint/no-explicit-any */
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, UrlSegment, UrlTree } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { RequestTaskStore } from '@netz/common/store';

import { RequestTaskItemDTO, UserStateDTO } from 'cca-api';

import { userIsAssigneeGuard } from './user-is-assignee.guard';

/**
 * Minimal ActivatedRouteSnapshot mock that satisfies createUrlTreeFromSnapshot.
 * pathFromRoot must contain [root, current] where root.children includes current.
 */
function createMockSnapshot(): ActivatedRouteSnapshot {
  const current: ActivatedRouteSnapshot = {
    url: [new UrlSegment('tasks', {})],
    children: [],
    pathFromRoot: [] as ActivatedRouteSnapshot[],
    paramMap: {
      keys: [],
      get: () => null,
      getAll: () => [],
      has: () => false,
    } as unknown as import('@angular/router').ParamMap,
    queryParamMap: {
      keys: [],
      get: () => null,
      getAll: () => [],
      has: () => false,
    } as unknown as import('@angular/router').ParamMap,
    data: {},
    params: {},
    queryParams: {},
    fragment: null,
    outlet: 'primary',
    component: null,
    routeConfig: null,
    root: undefined,
    parent: null,
    firstChild: null,
    title: '',
  } as ActivatedRouteSnapshot;

  const root: ActivatedRouteSnapshot = {
    url: [],
    children: [current],
    pathFromRoot: [] as ActivatedRouteSnapshot[],
    paramMap: {
      keys: [],
      get: () => null,
      getAll: () => [],
      has: () => false,
    } as unknown as import('@angular/router').ParamMap,
    queryParamMap: {
      keys: [],
      get: () => null,
      getAll: () => [],
      has: () => false,
    } as unknown as import('@angular/router').ParamMap,
    data: {},
    params: {},
    queryParams: {},
    fragment: null,
    outlet: 'primary',
    component: null,
    routeConfig: null,
    root: undefined,
    parent: null,
    firstChild: current,
    title: '',
  } as ActivatedRouteSnapshot;

  (root as any).root = root;
  (current as any).parent = root;
  (current as any).root = root;
  (current as any).pathFromRoot = [root, current];

  return current;
}

describe('userIsAssigneeGuard', () => {
  let authStore: AuthStore;
  let requestTaskStore: RequestTaskStore;

  function runGuard() {
    return TestBed.runInInjectionContext(() => userIsAssigneeGuard(createMockSnapshot()));
  }

  beforeEach(() => {
    TestBed.configureTestingModule({});

    authStore = TestBed.inject(AuthStore);
    requestTaskStore = TestBed.inject(RequestTaskStore);
  });

  it('should redirect when both userId and assigneeUserId are undefined', () => {
    const result = runGuard();

    expect(result).toBeInstanceOf(UrlTree);
  });

  it('should redirect when userId is undefined and assigneeUserId is set', () => {
    requestTaskStore.setRequestTaskItem({
      requestTask: { assigneeUserId: 'user-abc' },
    } as RequestTaskItemDTO);

    const result = runGuard();

    expect(result).toBeInstanceOf(UrlTree);
  });

  it('should redirect when userId is set and assigneeUserId is undefined', () => {
    authStore.setUserState({ userId: 'user-abc' } as UserStateDTO);

    const result = runGuard();

    expect(result).toBeInstanceOf(UrlTree);
  });

  it('should allow access when userId matches assigneeUserId', () => {
    authStore.setUserState({ userId: 'user-abc' } as UserStateDTO);
    requestTaskStore.setRequestTaskItem({
      requestTask: { assigneeUserId: 'user-abc' },
    } as RequestTaskItemDTO);

    const result = runGuard();

    expect(result).toEqual(true);
  });

  it('should redirect when userId differs from assigneeUserId', () => {
    authStore.setUserState({ userId: 'user-abc' } as UserStateDTO);
    requestTaskStore.setRequestTaskItem({
      requestTask: { assigneeUserId: 'user-xyz' },
    } as RequestTaskItemDTO);

    const result = runGuard();

    expect(result).toBeInstanceOf(UrlTree);
  });
});
