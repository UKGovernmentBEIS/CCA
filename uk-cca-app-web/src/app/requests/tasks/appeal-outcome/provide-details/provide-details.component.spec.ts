import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { RequestTaskFileService } from '@shared/services';

import { RequestTaskItemDTO } from 'cca-api';

import { ProvideDetailsComponent } from './provide-details.component';

describe('Appeal outcome date validation', () => {
  let fixture: ComponentFixture<ProvideDetailsComponent>;
  let router: Router;
  const tasksApiService = { saveRequestTaskAction: vi.fn().mockReturnValue(of({})) };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProvideDetailsComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ taskId: '123' }) },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: RequestTaskFileService, useValue: { buildFormControl: () => new FormControl(null) } },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Provide appeal outcome' },
      ],
    }).compileComponents();

    const store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_APPEAL_OUTCOME',
        payload: {
          payloadType: 'NON_COMPLIANCE_APPEAL_OUTCOME_PAYLOAD',
          nonComplianceAttachments: {},
          sectionsCompleted: {},
        },
      },
      requestInfo: { accountId: 1 },
    } as RequestTaskItemDTO);
    store.setIsEditable(true);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture = TestBed.createComponent(ProvideDetailsComponent);
    fixture.detectChanges();
    fixture.componentInstance['form'].controls.tribunalDecision.setValue('APPEAL_ALLOWED');
    tasksApiService.saveRequestTaskAction.mockClear();
  });

  function enterDateAndSubmit(day: string, month: string, year: string) {
    const inputs = fixture.nativeElement.querySelectorAll('[formcontrolname="appealOutcomeDate"] input');
    [day, month, year].forEach((value, index) => {
      inputs[index].value = value;
      inputs[index].dispatchEvent(new Event('input', { bubbles: true }));
    });
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit', { bubbles: true }));
    fixture.detectChanges();
  }

  it.each([
    ['', '', '', 'Enter the date of appeal outcome'],
    ['31', '', '', 'Enter a full date'],
    ['31', '2', '2025', 'Enter a real date'],
    ['1', '1', '2099', 'This date must be today or in the past'],
  ])('shows one date message and prevents saving for %s/%s/%s', (day, month, year, message) => {
    enterDateAndSubmit(day, month, year);

    const inlineErrors = fixture.nativeElement.querySelectorAll(
      '[formcontrolname="appealOutcomeDate"] .govuk-error-message > span',
    );
    expect(Array.from(inlineErrors, (error: HTMLElement) => error.textContent.replace(/\s+/g, ' ').trim())).toEqual([
      `Error: ${message}`,
    ]);
    const summaryErrors = fixture.nativeElement.querySelectorAll('.govuk-error-summary__list a');
    expect(Array.from(summaryErrors, (error: HTMLElement) => error.textContent.trim())).toEqual([message]);
    expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('saves the selected calendar day after correcting an impossible date', () => {
    enterDateAndSubmit('31', '2', '2025');
    expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();

    enterDateAndSubmit('28', '2', '2025');
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledExactlyOnceWith(
      expect.objectContaining({
        requestTaskActionPayload: expect.objectContaining({
          appealOutcome: expect.objectContaining({ appealOutcomeDate: '2025-02-28' }),
        }),
      }),
    );
  });
});
