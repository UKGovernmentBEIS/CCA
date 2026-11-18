import { provideHttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import { mockNonComplianceDetailsState } from '../testing/mock-data';
import { ProvideDetailsComponent } from './provide-details.component';

@Component({ template: '' })
class DummyComponent {}

describe('ProvideDetailsComponent', () => {
  let component: ProvideDetailsComponent;
  let fixture: ComponentFixture<ProvideDetailsComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub();

  const mockTasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProvideDetailsComponent],
      providers: [
        provideHttpClient(),
        provideRouter([{ path: '**', component: DummyComponent }]),
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Dashboard' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockNonComplianceDetailsState);

    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(ProvideDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    mockTasksApiService.saveRequestTaskAction.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not include a blank non-compliance type option', () => {
    const options = component['nonComplianceTypeOptions'];

    expect(options.some((option) => option.value == null || option.text === '')).toBe(false);
  });

  it('should submit and call saveRequestTaskAction', () => {
    component.onSubmit();

    expect(mockTasksApiService.saveRequestTaskAction).toHaveBeenCalledTimes(1);
  });

  it('should navigate to check your answers after submit when wizard is already completed', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');

    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith(['../check-your-answers'], {
      relativeTo: route,
    });
  });

  it('should call saveRequestTaskAction with expected dto and converted dates', () => {
    const form = component['form'];
    const nonCompliantDate = new Date('2025-03-01T00:00:00.000Z');
    const compliantDate = new Date('2025-03-10T00:00:00.000Z');

    form.patchValue({
      nonComplianceType: 'FAILURE_TO_PROVIDE_TPR',
      nonCompliantDate,
      compliantDate,
      comment: 'Updated comment',
    });

    component.onSubmit();

    expect(mockTasksApiService.saveRequestTaskAction).toHaveBeenCalledWith(
      expect.objectContaining({
        requestTaskId: 100,
        requestTaskActionType: 'NON_COMPLIANCE_DETAILS_SAVE_APPLICATION',
        requestTaskActionPayload: expect.objectContaining({
          payloadType: 'NON_COMPLIANCE_DETAILS_SAVE_PAYLOAD',
          nonComplianceDetails: expect.objectContaining({
            nonComplianceType: 'FAILURE_TO_PROVIDE_TPR',
            nonCompliantDate: nonCompliantDate.toISOString(),
            compliantDate: compliantDate.toISOString(),
            comment: 'Updated comment',
          }),
        }),
      }),
    );
  });
});
