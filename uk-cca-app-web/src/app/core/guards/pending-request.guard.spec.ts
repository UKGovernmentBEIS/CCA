import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { lastValueFrom, timer } from 'rxjs';

import { PendingRequestService } from '@netz/common/services';

import { PendingRequest, PendingRequestGuard } from './pending-request.guard';

describe('PendingRequestGuard', () => {
  let testComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let router: Router;
  let windowAlert: jest.SpyInstance;

  @Component({ standalone: true, selector: 'cca-test-1', template: '', providers: [PendingRequestService] })
  class TestComponent implements PendingRequest {
    someRequest = timer(3000).pipe(this.pendingRequest.trackRequest());

    constructor(readonly pendingRequest: PendingRequestService) {}
  }

  @Component({ standalone: true, selector: 'cca-test-2', template: '' })
  class EmptyTestComponent {}

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestComponent, EmptyTestComponent],
    });
    fixture = TestBed.createComponent(TestComponent);
    testComponent = fixture.componentInstance;
    router = TestBed.inject(Router);
    windowAlert = jest.spyOn(window, 'alert').mockImplementation();
    fixture.detectChanges();
  });

  it('should alert if deactivating while request is pending', async () => {
    const guard = TestBed.runInInjectionContext(() => PendingRequestGuard(testComponent));
    jest.useFakeTimers();

    testComponent.someRequest.subscribe();

    await expect(lastValueFrom(guard)).resolves.toBeFalsy();
    expect(windowAlert).toHaveBeenCalled();

    jest.advanceTimersByTime(3000);
  });

  it('should allow deactivation if forced navigation', () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { forceNavigation: true } } } as any);
    const guard = TestBed.runInInjectionContext(() => PendingRequestGuard(testComponent));

    expect(guard).toEqual(true);
  });

  it('should allow deactivation if request is not pending', () => {
    const guard = TestBed.runInInjectionContext(() => PendingRequestGuard(testComponent));

    return expect(lastValueFrom(guard)).resolves.toBeTruthy();
  });

  it('should allow deactivation if there is no globally pending request', async () => {
    jest.useFakeTimers();
    const pendingRequestService = TestBed.inject(PendingRequestService);
    const component = TestBed.createComponent(EmptyTestComponent).componentInstance;
    const guard = TestBed.runInInjectionContext(() => PendingRequestGuard(component));

    timer(3000).pipe(pendingRequestService.trackRequest()).subscribe();

    await expect(lastValueFrom(guard)).resolves.toBeFalsy();

    jest.advanceTimersByTime(3000);

    await expect(lastValueFrom(guard)).resolves.toBeTruthy();
  });

  it('should allow deactivation if there is no globally or locally pending request', async () => {
    jest.useFakeTimers();
    const pendingRequestService = TestBed.inject(PendingRequestService);
    const canDeactivate = TestBed.runInInjectionContext(() => PendingRequestGuard(testComponent));

    timer(2000).pipe(pendingRequestService.trackRequest()).subscribe();
    testComponent.someRequest.subscribe();

    await expect(lastValueFrom(canDeactivate)).resolves.toBeFalsy();

    jest.advanceTimersByTime(2000);

    await expect(lastValueFrom(canDeactivate)).resolves.toBeFalsy();

    jest.advanceTimersByTime(3000);

    await expect(lastValueFrom(canDeactivate)).resolves.toBeTruthy();
  });
});
