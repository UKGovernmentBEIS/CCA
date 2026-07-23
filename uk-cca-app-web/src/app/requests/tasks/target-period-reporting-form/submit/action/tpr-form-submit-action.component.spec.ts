import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { Mocked } from 'vitest';

import { mockTprRequestTaskStateThroughputTotalsOnly } from '../../testing/mock-data';
import { TprFormSubmitActionComponent } from './tpr-form-submit-action.component';

describe('TprFormSubmitActionComponent', () => {
  let component: TprFormSubmitActionComponent;
  let fixture: ComponentFixture<TprFormSubmitActionComponent>;
  let store: RequestTaskStore;
  let tasksApiService: Mocked<Pick<TasksApiService, 'saveRequestTaskAction'>>;
  let router: Router;

  beforeEach(async () => {
    tasksApiService = { saveRequestTaskAction: vi.fn().mockReturnValue(of({})) };

    await TestBed.configureTestingModule({
      imports: [TprFormSubmitActionComponent],
      providers: [
        RequestTaskStore,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Confirm results and submit' },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        provideRouter([]),
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    store.setState(mockTprRequestTaskStateThroughputTotalsOnly);

    fixture = TestBed.createComponent(TprFormSubmitActionComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit with EMPTY_PAYLOAD', () => {
    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith(
      expect.objectContaining({
        requestTaskActionType: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_APPLICATION',
        requestTaskActionPayload: { payloadType: 'EMPTY_PAYLOAD' },
      }),
    );

    expect(router.navigate).toHaveBeenCalledWith(['confirmation'], {
      relativeTo: expect.anything(),
      replaceUrl: true,
    });
  });

  it('should navigate to cancel page on onCancel', () => {
    component.onCancel();

    expect(router.navigate).toHaveBeenCalledWith(['../../cancel'], {
      relativeTo: expect.anything(),
      replaceUrl: true,
    });
  });

  it('should show cancel error summary and not navigate to confirmation for TPRDF1002', () => {
    tasksApiService.saveRequestTaskAction.mockReturnValueOnce(throwError(() => ({ error: { code: 'TPRDF1002' } })));

    component.onSubmit();
    fixture.detectChanges();

    expect(router.navigate).not.toHaveBeenCalledWith(['confirmation'], {
      relativeTo: expect.anything(),
      replaceUrl: true,
    });
    expect(component.isErrorSummaryDisplayed()).toBe(true);
    expect(component.isCancelError()).toBe(true);
    expect(component.errorSummaryInfo().link).toBe('../../cancel');

    const nativeElement = fixture.nativeElement as HTMLElement;
    expect(nativeElement.textContent).toContain('Go to cancel task');
    expect(document.activeElement).toBe(nativeElement.querySelector('.govuk-error-summary'));
  });

  it('should show refresh baseline error summary and keep submit button for TPRDF1008', () => {
    tasksApiService.saveRequestTaskAction.mockReturnValueOnce(throwError(() => ({ error: { code: 'TPRDF1008' } })));

    component.onSubmit();
    fixture.detectChanges();

    expect(component.isErrorSummaryDisplayed()).toBe(true);
    expect(component.isCancelError()).toBe(false);
    expect(component.errorSummaryInfo().link).toBe('../refresh-baseline-data');

    const nativeElement = fixture.nativeElement as HTMLElement;
    expect(nativeElement.textContent).toContain('Confirm and submit TPR');
  });

  it('should show cancel error summary for TPRDF1005', () => {
    tasksApiService.saveRequestTaskAction.mockReturnValueOnce(throwError(() => ({ error: { code: 'TPRDF1005' } })));

    component.onSubmit();
    fixture.detectChanges();

    expect(component.isErrorSummaryDisplayed()).toBe(true);
    expect(component.isCancelError()).toBe(true);
    expect(component.errorSummaryInfo().message).toBe(
      'The baseline data for this facility must contain at least one product with a base year equal to the facility base year, and at least one product with a base year less than or equal to the year the report data relates to. You must submit a variation to correct the base year of products in this facility before you can submit your report.',
    );
    expect(component.errorSummaryInfo().link).toBe('../../cancel');
  });

  it('should show locked error summary and keep submit button for TPRDF1004', () => {
    tasksApiService.saveRequestTaskAction.mockReturnValueOnce(throwError(() => ({ error: { code: 'TPRDF1004' } })));

    component.onSubmit();
    fixture.detectChanges();

    expect(component.isErrorSummaryDisplayed()).toBe(true);
    expect(component.isCancelError()).toBe(false);
    expect(component.errorSummaryInfo().linkText).toContain('this target period must be unlocked before submitting');

    const nativeElement = fixture.nativeElement as HTMLElement;
    expect(nativeElement.textContent).toContain('Confirm and submit TPR');
  });
});
