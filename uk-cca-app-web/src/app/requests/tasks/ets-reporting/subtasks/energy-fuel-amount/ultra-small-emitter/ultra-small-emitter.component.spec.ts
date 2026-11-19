import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import { mockEtsFuelPayload, mockEtsPayload } from '../../../testing/mock-data';
import { UltraSmallEmitterComponent } from './ultra-small-emitter.component';

describe('ETS USE questions', () => {
  let fixture: ComponentFixture<UltraSmallEmitterComponent>;
  let store: RequestTaskStore;
  let router: Router;
  const saveRequestTaskAction = vi.fn();

  beforeEach(async () => {
    saveRequestTaskAction.mockReturnValue(of({}));
    await TestBed.configureTestingModule({
      imports: [UltraSmallEmitterComponent],
      providers: [
        RequestTaskStore,
        { provide: TasksApiService, useValue: { saveRequestTaskAction } },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'UK ETS reporting' },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        provideRouter([]),
      ],
    }).compileComponents();
    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({
      requestTask: { id: 123, type: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT', payload: mockEtsPayload() },
    });
    store.setIsEditable(true);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture = TestBed.createComponent(UltraSmallEmitterComponent);
    await fixture.whenStable();
  });

  it('requires the first answer and shows the existing wizard error summary', async () => {
    fixture.nativeElement.querySelector('button[type="submit"]').click();
    await fixture.whenStable();
    expect(saveRequestTaskAction).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('.govuk-error-summary').textContent).toContain(
      'Select yes if this facility',
    );
    expect(fixture.nativeElement.querySelectorAll('input[type="radio"]')).toHaveLength(2);
  });

  it('conditionally requires the voluntary answer, with no keystroke saves', async () => {
    const form = fixture.componentInstance.form;
    form.controls.isUltraSmallEmitter.setValue(true);
    await fixture.whenStable();
    expect(form.invalid).toBe(true);
    expect(fixture.nativeElement.querySelectorAll('input[type="radio"]')).toHaveLength(4);
    form.controls.provideFuelDetails.setValue(false);
    expect(form.valid).toBe(true);
    form.controls.isUltraSmallEmitter.setValue(false);
    await fixture.whenStable();
    expect(form.controls.provideFuelDetails.value).toBeNull();
    expect(form.valid).toBe(true);
    expect(fixture.nativeElement.querySelectorAll('input[type="radio"]')).toHaveLength(2);
    expect(saveRequestTaskAction).not.toHaveBeenCalled();
  });

  it.each([
    [false, null, 'details'],
    [true, true, 'details'],
    [true, false, 'check-your-answers'],
  ])('saves USE %s / voluntary %s with the whole payload then advances to %s', (use, voluntary, step) => {
    store.setPayload(mockEtsFuelPayload());
    fixture.componentInstance.form.setValue({ isUltraSmallEmitter: use, provideFuelDetails: voluntary });
    fixture.componentInstance.onSubmit();
    const dto = saveRequestTaskAction.mock.calls[0][0];
    expect(dto.requestTaskId).toBe(123);
    expect(dto.requestTaskActionType).toBe('EMISSIONS_TRADING_SCHEME_DATA_SAVE_APPLICATION');
    expect(dto.requestTaskActionPayload.payloadType).toBe('EMISSIONS_TRADING_SCHEME_DATA_SAVE_PAYLOAD');
    expect(dto.requestTaskActionPayload.sectionsCompleted).toEqual({
      etsEnergyFuelDetails: 'IN_PROGRESS',
      anotherSection: 'COMPLETED',
    });
    expect(dto.requestTaskActionPayload.emissionsTradingSchemeDataInputDetails.isUltraSmallEmitter).toBe(use);
    if (use === false)
      expect(dto.requestTaskActionPayload.emissionsTradingSchemeDataInputDetails.provideFuelDetails).toBeUndefined();
    if (use && voluntary === false)
      expect(dto.requestTaskActionPayload.emissionsTradingSchemeDataInputDetails.energyFuelDetails).toBeUndefined();
    else
      expect(dto.requestTaskActionPayload.emissionsTradingSchemeDataInputDetails.energyFuelDetails).toEqual(
        mockEtsFuelPayload().emissionsTradingSchemeDataInputDetails.energyFuelDetails,
      );
    expect(router.navigate).toHaveBeenCalledWith([`../${step}`], { relativeTo: expect.anything() });
  });

  it('retains the page, entered answers and persisted state when saving fails', async () => {
    saveRequestTaskAction.mockReturnValueOnce(throwError(() => ({ status: 500 })));
    fixture.componentInstance.form.controls.isUltraSmallEmitter.setValue(false);
    fixture.componentInstance.onSubmit();
    await fixture.whenStable();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(fixture.componentInstance.form.controls.isUltraSmallEmitter.value).toBe(false);
    expect(store.state.requestTaskItem.requestTask.payload).toEqual(mockEtsPayload());
    expect(fixture.nativeElement.querySelector('.govuk-error-summary').textContent).toContain(
      'Your answers could not be saved',
    );
  });

  it('rebuilds from persisted correction answers on entry and has the official 2026–2030 list link', async () => {
    fixture.destroy();
    store.setPayload(
      mockEtsPayload({
        emissionsTradingSchemeDataInputDetails: { isUltraSmallEmitter: true, provideFuelDetails: true },
      }),
    );
    fixture = TestBed.createComponent(UltraSmallEmitterComponent);
    await fixture.whenStable();
    expect(fixture.componentInstance.form.getRawValue()).toEqual({
      isUltraSmallEmitter: true,
      provideFuelDetails: true,
    });
    expect(fixture.nativeElement.querySelector('govuk-details a').href).toContain('use-list-2026-2030.ods');
  });
});
