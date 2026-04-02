import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { TasksApiService } from '@requests/common';

import { mockNonComplianceDetailsState } from '../testing/mock-data';
import { ProvideDetailsComponent } from './provide-details.component';

describe('ProvideDetailsComponent', () => {
  let component: ProvideDetailsComponent;
  let fixture: ComponentFixture<ProvideDetailsComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = {
    snapshot: {
      params: {},
      paramMap: { get: jest.fn() },
      pathFromRoot: [],
    },
  };

  const mockTasksApiService = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProvideDetailsComponent],
      providers: [
        provideHttpClient(),
        RequestTaskStore,
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: route },
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
    const options = (component as any).nonComplianceTypeOptions;

    expect(options.some((option) => option.value == null || option.text === '')).toBe(false);
  });

  it('should submit and call saveRequestTaskAction', () => {
    component.onSubmit();

    expect(mockTasksApiService.saveRequestTaskAction).toHaveBeenCalledTimes(1);
  });

  it('should navigate to check your answers after submit when wizard is already completed', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route as any });
  });

  it('should call saveRequestTaskAction with expected dto and converted dates', () => {
    const form = (component as any).form;
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
