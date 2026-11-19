import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { getByText } from '@testing';

import { NonComplianceConclusionSubmitRequestTaskPayload, RequestTaskItemDTO } from 'cca-api';

import { ProvideDetailsComponent } from './provide-details.component';

describe('ProvideDetailsComponent', () => {
  let component: ProvideDetailsComponent;
  let fixture: ComponentFixture<ProvideDetailsComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub({ taskId: '123' });

  const tasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  const initialPayload: NonComplianceConclusionSubmitRequestTaskPayload = {
    payloadType: 'NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD',
    nonComplianceConclusion: null,
    nonComplianceAttachments: {},
    sectionsCompleted: {},
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProvideDetailsComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Provide conclusion of non-compliance' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_CONCLUSION_SUBMIT',
        payload: initialPayload,
      },
      requestInfo: { accountId: 1 },
    } as RequestTaskItemDTO);
    store.setIsEditable(true);

    fixture = TestBed.createComponent(ProvideDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    tasksApiService.saveRequestTaskAction.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the provide details step content', () => {
    expect(getByText('Provide conclusion details', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Has compliance been restored?', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Has the operator paid the penalty?', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Would you like to reissue or withdraw the penalty?', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText(
        'This will start a new task to upload a replacement penalty notice allowing you to reduce the penalty amount and extend the time for payment',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
    expect(getByText('This will require you to upload a withdrawal notice', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText(
        'You must allow at least 28 calendar days for the operator to appeal the penalty notice before concluding this non-compliance task',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
  });

  it('should reject future compliance restored dates on initial submission', () => {
    const today = new Date();
    const tomorrow = new Date(Date.UTC(today.getFullYear(), today.getMonth(), today.getDate() + 1));

    component['form'].controls.complianceRestored.setValue(true);
    component['form'].controls.complianceRestoredDate.setValue(tomorrow);

    expect(component['form'].controls.complianceRestoredDate.errors?.invalidDate).toEqual(
      'This date cannot be in the future',
    );
  });

  it('should reject future penalty payment dates on initial submission', () => {
    const today = new Date();
    const tomorrow = new Date(Date.UTC(today.getFullYear(), today.getMonth(), today.getDate() + 1));

    component['form'].controls.penaltyPaid.setValue(true);
    component['form'].controls.penaltyPaymentDate.setValue(tomorrow);

    expect(component['form'].controls.penaltyPaymentDate.errors?.invalidDate).toEqual(
      'This date cannot be in the future',
    );
  });

  describe.each([
    ['complianceRestored', 'complianceRestoredDate'],
    ['penaltyPaid', 'penaltyPaymentDate'],
  ] as const)('%s conditional date', (choiceName, dateName) => {
    it.each([0, 23])('accepts today and rejects tomorrow at %i:30 local time during summer', (hour) => {
      fixture.destroy();
      vi.useFakeTimers({ toFake: ['Date'] });
      try {
        vi.setSystemTime(new Date(2025, 6, 15, hour, 30));
        fixture = TestBed.createComponent(ProvideDetailsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();

        component['form'].controls[choiceName].setValue(true);
        fixture.detectChanges();
        const date = component['form'].controls[dateName];
        date.setValue(new Date('2025-07-15'));
        expect(date.valid).toBe(true);

        date.setValue(new Date('2025-07-16'));
        expect(date.errors).toEqual({ invalidDate: 'This date cannot be in the future' });
      } finally {
        vi.useRealTimers();
      }
    });

    it('preserves the required and future-date validators after toggling the choice and teardown', () => {
      const choice = component['form'].controls[choiceName];
      const date = component['form'].controls[dateName];

      choice.setValue(true);
      fixture.detectChanges();
      date.setValue(new Date(Date.UTC(2099, 0, 1)));
      expect(date.hasError('invalidDate')).toBe(true);

      choice.setValue(false);
      fixture.detectChanges();
      expect(date.value).toBeNull();
      expect(date.disabled).toBe(true);
      expect(date.validator).not.toBeNull();

      choice.setValue(true);
      fixture.detectChanges();
      expect(date.hasError('required')).toBe(true);
      date.setValue(new Date(Date.UTC(2099, 0, 1)));
      fixture.destroy();

      expect(date.errors).toEqual({ invalidDate: 'This date cannot be in the future' });
      date.setValue(new Date('2020-01-01'));
      expect(date.valid).toBe(true);
      date.reset();
      expect(date.errors).toEqual({ required: 'Enter a date' });
    });

    it('removes the conditional validators when the user chooses No before teardown', () => {
      const choice = component['form'].controls[choiceName];
      const date = component['form'].controls[dateName];
      choice.setValue(true);
      fixture.detectChanges();
      date.setValue(new Date(Date.UTC(2099, 0, 1)));
      expect(date.hasError('invalidDate')).toBe(true);

      choice.setValue(false);
      fixture.detectChanges();
      fixture.destroy();

      expect(date.validator).toBeNull();
      expect(date.value).toBeNull();
      date.enable();
      expect(date.valid).toBe(true);
    });

    it('preserves validators added by a consumer when toggling the choice and tearing down', () => {
      const choice = component['form'].controls[choiceName];
      const date = component['form'].controls[dateName];
      const externalValidator = () => ({ external: true });
      date.addValidators(externalValidator);

      choice.setValue(true);
      fixture.detectChanges();
      expect(date.hasValidator(externalValidator)).toBe(true);
      expect(date.hasError('external')).toBe(true);

      choice.setValue(false);
      fixture.detectChanges();
      fixture.destroy();

      date.enable();
      expect(date.hasValidator(externalValidator)).toBe(true);
      expect(date.errors).toEqual({ external: true });
    });

    it('validates empty, partial and impossible dates after Yes/No/Yes changes in the UI', () => {
      const form = component['form'];
      const date = form.controls[dateName];
      form.patchValue({
        complianceRestored: false,
        penaltyPaid: false,
        comment: 'Status confirmed',
        penaltyOutcome: 'NONE',
      });
      fixture.detectChanges();

      const choose = (value: boolean) => {
        fixture.nativeElement.querySelector(`[formcontrolname="${choiceName}"] input[value="${value}"]`).click();
        fixture.detectChanges();
      };
      const submit = () => {
        fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit', { bubbles: true }));
        fixture.detectChanges();
      };
      const enterDate = (day: string, month: string, year: string) => {
        const inputs = fixture.nativeElement.querySelectorAll(`[formcontrolname="${dateName}"] input`);
        [day, month, year].forEach((value, index) => {
          inputs[index].value = value;
          inputs[index].dispatchEvent(new Event('input', { bubbles: true }));
        });
        fixture.detectChanges();
      };
      const expectDateMessage = (message: string) => {
        const inlineErrors = fixture.nativeElement.querySelectorAll(
          `[formcontrolname="${dateName}"] .govuk-error-message > span`,
        );
        expect(Array.from(inlineErrors, (error: HTMLElement) => error.textContent.replace(/\s+/g, ' ').trim())).toEqual(
          [`Error: ${message}`],
        );
        const summaryErrors = fixture.nativeElement.querySelectorAll('.govuk-error-summary__list a');
        expect(Array.from(summaryErrors, (error: HTMLElement) => error.textContent.trim())).toEqual([message]);
      };

      choose(true);
      enterDate('31', '2', '2025');
      expect(date.hasError('isUnrealDate')).toBe(true);

      choose(false);
      expect(date.disabled).toBe(true);
      expect(date.value).toBeNull();
      choose(true);
      submit();
      expect(date.errors).toEqual({ required: 'Enter a date' });
      expectDateMessage('Enter a date');
      expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();

      enterDate('31', '', '');
      submit();
      expect(date.hasError('isIncomplete')).toBe(true);
      expectDateMessage('Enter a full date');
      expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();

      enterDate('31', '2', '2025');
      submit();
      expect(date.hasError('isUnrealDate')).toBe(true);
      expectDateMessage('Enter a real date');
      expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();

      enterDate('1', '1', '2099');
      submit();
      expect(date.errors).toEqual({ invalidDate: 'This date cannot be in the future' });
      expectDateMessage('This date cannot be in the future');
      expect(
        fixture.nativeElement.querySelectorAll(`[formcontrolname="${dateName}"] input.govuk-input--error`),
      ).toHaveLength(3);
      expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();

      enterDate('1', '1', '2025');
      submit();
      expect(date.valid).toBe(true);
      expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledExactlyOnceWith(
        expect.objectContaining({
          requestTaskActionPayload: expect.objectContaining({
            nonComplianceConclusion: expect.objectContaining({
              details: expect.objectContaining({ [dateName]: '2025-01-01T00:00:00.000Z' }),
            }),
          }),
        }),
      );
    });
  });

  it('should save and navigate to provide-withdrawal-notice when WITHDRAW selected and no file yet', () => {
    component['form'].setValue({
      complianceRestored: false,
      complianceRestoredDate: null,
      penaltyPaid: false,
      penaltyPaymentDate: null,
      comment: 'Some comments',
      penaltyOutcome: 'WITHDRAW',
    });

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_CONCLUSION_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_CONCLUSION_SAVE_PAYLOAD',
        nonComplianceConclusion: expect.objectContaining({
          details: {
            complianceRestored: false,
            complianceRestoredDate: null,
            penaltyPaid: false,
            penaltyPaymentDate: null,
            comment: 'Some comments',
            penaltyOutcome: 'WITHDRAW',
          },
        }),
        sectionsCompleted: { 'provide-conclusion': 'IN_PROGRESS' },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../provide-withdrawal-notice'], { relativeTo: route });
  });

  it('should save and navigate to check-your-answers when REISSUE selected', () => {
    component['form'].setValue({
      complianceRestored: false,
      complianceRestoredDate: null,
      penaltyPaid: false,
      penaltyPaymentDate: null,
      comment: 'Some comments',
      penaltyOutcome: 'REISSUE',
    });

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_CONCLUSION_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_CONCLUSION_SAVE_PAYLOAD',
        nonComplianceConclusion: expect.objectContaining({
          details: {
            complianceRestored: false,
            complianceRestoredDate: null,
            penaltyPaid: false,
            penaltyPaymentDate: null,
            comment: 'Some comments',
            penaltyOutcome: 'REISSUE',
          },
        }),
        sectionsCompleted: { 'provide-conclusion': 'IN_PROGRESS' },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });

  it('should save and navigate to check-your-answers when WITHDRAW selected but file already uploaded', () => {
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_CONCLUSION_SUBMIT',
        payload: {
          ...initialPayload,
          nonComplianceConclusion: {
            details: {
              complianceRestored: false,
              complianceRestoredDate: null,
              penaltyPaid: false,
              penaltyPaymentDate: null,
              comment: 'Existing comments',
              penaltyOutcome: 'WITHDRAW',
            },
            withdrawNotice: { file: 'uuid-1', comments: 'Notice comments' },
          },
          nonComplianceAttachments: { 'uuid-1': 'notice.pdf' },
        } as NonComplianceConclusionSubmitRequestTaskPayload,
      },
      requestInfo: { accountId: 1 },
    } as RequestTaskItemDTO);

    fixture = TestBed.createComponent(ProvideDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    tasksApiService.saveRequestTaskAction.mockClear();

    component['form'].setValue({
      complianceRestored: false,
      complianceRestoredDate: null,
      penaltyPaid: false,
      penaltyPaymentDate: null,
      comment: 'Existing comments',
      penaltyOutcome: 'WITHDRAW',
    });

    component.onSubmit();

    expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });

  it('should clear withdrawNotice when switching from WITHDRAW to NONE', () => {
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_CONCLUSION_SUBMIT',
        payload: {
          ...initialPayload,
          nonComplianceConclusion: {
            details: {
              complianceRestored: false,
              complianceRestoredDate: null,
              penaltyPaid: false,
              penaltyPaymentDate: null,
              comment: 'Comments',
              penaltyOutcome: 'WITHDRAW',
            },
            withdrawNotice: { file: 'uuid-1', comments: 'Notice comments' },
          },
          nonComplianceAttachments: { 'uuid-1': 'notice.pdf' },
        } as NonComplianceConclusionSubmitRequestTaskPayload,
      },
      requestInfo: { accountId: 1 },
    } as RequestTaskItemDTO);

    fixture = TestBed.createComponent(ProvideDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    tasksApiService.saveRequestTaskAction.mockClear();

    component['form'].controls.penaltyOutcome.setValue('NONE');
    component['form'].controls.comment.setValue('Comments');

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith(
      expect.objectContaining({
        requestTaskActionPayload: expect.objectContaining({
          nonComplianceConclusion: expect.objectContaining({ withdrawNotice: null }),
        }),
      }),
    );
  });
});
