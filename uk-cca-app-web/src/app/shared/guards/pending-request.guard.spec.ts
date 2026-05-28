import { Component, inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { lastValueFrom, timer } from 'rxjs';

import { PendingRequestService } from '@netz/common/services';
import { MockInstance } from 'vitest';

import { PendingRequest, PendingRequestGuard } from './pending-request.guard';

describe('PendingRequestGuard', () => {
  let testComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let router: Router;
  let windowAlert: MockInstance;

  @Component({ selector: 'cca-test-1', template: '', providers: [PendingRequestService] })
  class TestComponent implements PendingRequest {
    readonly pendingRequest = inject(PendingRequestService);
    readonly someRequest = timer(3000).pipe(this.pendingRequest.trackRequest());
  }

  @Component({ selector: 'cca-test-2', template: '' })
  class EmptyTestComponent {}

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestComponent, EmptyTestComponent],
    });

    fixture = TestBed.createComponent(TestComponent);
    testComponent = fixture.componentInstance;
    router = TestBed.inject(Router);
    windowAlert = vi.spyOn(window, 'alert').mockImplementation(() => undefined);
    fixture.detectChanges();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('should alert if deactivating while request is pending', async () => {
    const guard = TestBed.runInInjectionContext(() => PendingRequestGuard(testComponent));
    vi.useFakeTimers();

    testComponent.someRequest.subscribe();

    await expect(lastValueFrom(guard)).resolves.toBeFalsy();
    expect(windowAlert).toHaveBeenCalled();

    vi.advanceTimersByTime(3000);
  });

  it('should allow deactivation if forced navigation', () => {
    vi.spyOn(router, 'currentNavigation').mockReturnValue({ extras: { state: { forceNavigation: true } } } as any);
    const guard = TestBed.runInInjectionContext(() => PendingRequestGuard(testComponent));

    expect(guard).toEqual(true);
  });

  it('should allow deactivation if request is not pending', () => {
    const guard = TestBed.runInInjectionContext(() => PendingRequestGuard(testComponent));

    return expect(lastValueFrom(guard)).resolves.toBeTruthy();
  });

  it('should allow deactivation if there is no globally pending request', async () => {
    vi.useFakeTimers();
    const pendingRequestService = TestBed.inject(PendingRequestService);
    const component = TestBed.createComponent(EmptyTestComponent).componentInstance;
    const guard = TestBed.runInInjectionContext(() => PendingRequestGuard(component));

    timer(3000).pipe(pendingRequestService.trackRequest()).subscribe();

    await expect(lastValueFrom(guard)).resolves.toBeFalsy();

    vi.advanceTimersByTime(3000);

    await expect(lastValueFrom(guard)).resolves.toBeTruthy();
  });

  it('should allow deactivation if there is no globally or locally pending request', async () => {
    vi.useFakeTimers();
    const pendingRequestService = TestBed.inject(PendingRequestService);
    const canDeactivate = TestBed.runInInjectionContext(() => PendingRequestGuard(testComponent));

    timer(2000).pipe(pendingRequestService.trackRequest()).subscribe();
    testComponent.someRequest.subscribe();

    await expect(lastValueFrom(canDeactivate)).resolves.toBeFalsy();

    vi.advanceTimersByTime(2000);

    await expect(lastValueFrom(canDeactivate)).resolves.toBeFalsy();

    vi.advanceTimersByTime(3000);

    await expect(lastValueFrom(canDeactivate)).resolves.toBeTruthy();
  });
});
