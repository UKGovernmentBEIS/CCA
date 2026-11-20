import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of, Subject, throwError } from 'rxjs';

import { PendingRequestService } from '@netz/common/services';
import {
  ITEM_TYPE_TO_RETURN_TEXT_MAPPER,
  RequestTaskState,
  RequestTaskStore,
  TYPE_AWARE_STORE,
} from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { assertInCellControlLabelsHidden } from '@testing';

import { EtsMeasurementType, EtsReportingTaskPayload } from '../../../ets-reporting.types';
import { ETS_FUEL_ENTRIES } from '../../../ets-reporting-calculations';
import { EnergyFuelAmountDetailsComponent } from './energy-fuel-amount-details.component';

const payloadFixture = (overrides: Partial<EtsReportingTaskPayload> = {}): EtsReportingTaskPayload => ({
  payloadType: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_PAYLOAD',
  targetYear: 2026,
  uketsId: 'UKETS123',
  measurementType: 'ENERGY_KWH',
  emissionsTradingSchemeDataInputDetails: {
    isUltraSmallEmitter: false,
    energyFuelDetails: {
      standardFuels: { NATURAL_GAS: { deliveredEnergy: '1000', primaryEnergy: '1000' } },
      nonStandardFuels: [],
      totalPrimaryEnergy: '1000',
      totalCo2Emissions: '0.18254',
    },
  },
  sectionsCompleted: { unrelated: 'COMPLETED', etsEnergyFuelDetails: 'COMPLETED' },
  ...overrides,
});

describe('ETS energy/fuel details', () => {
  let component: EnergyFuelAmountDetailsComponent;
  let fixture: ComponentFixture<EnergyFuelAmountDetailsComponent>;
  let store: RequestTaskStore;
  let router: Router;
  let save: ReturnType<typeof vi.fn>;

  const rows = () => component.form.controls.fuels;
  const gas = () => rows().controls.find((row) => row.controls.fuelKey.value === 'NATURAL_GAS');
  const addCustom = (fuelType = 'Custom fuel', co2ConversionFactor = '0.25', deliveredEnergy = '10') => {
    component.onAddFuel();
    const row = rows().at(rows().length - 1);
    row.patchValue({ fuelType, co2ConversionFactor, deliveredEnergy });
    return row;
  };
  const text = () => fixture.nativeElement.textContent.replace(/\s+/g, ' ');

  async function setup(payload = payloadFixture()) {
    save = vi.fn().mockReturnValue(of({}));
    await TestBed.configureTestingModule({
      imports: [EnergyFuelAmountDetailsComponent],
      providers: [
        RequestTaskStore,
        PendingRequestService,
        provideRouter([]),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'UK ETS reporting - Submit form' },
        { provide: TasksApiService, useValue: { saveRequestTaskAction: save } },
      ],
    }).compileComponents();
    const state: RequestTaskState = {
      requestTaskItem: {
        requestTask: { id: 42, type: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT', payload },
        requestInfo: { id: 'ETS001', type: 'EMISSIONS_TRADING_SCHEME_DATA' },
        allowedRequestTaskActions: ['EMISSIONS_TRADING_SCHEME_DATA_SAVE_APPLICATION'],
      },
      relatedTasks: [],
      timeline: [],
      taskReassignedTo: null,
      isEditable: true,
    };
    store = TestBed.inject(RequestTaskStore);
    store.setState(state);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture = TestBed.createComponent(EnergyFuelAmountDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  afterEach(() => {
    vi.clearAllMocks();
    TestBed.resetTestingModule();
  });

  it.each([
    { input: undefined, step: 'ultra-small-emitter' },
    { input: { isUltraSmallEmitter: true }, step: 'ultra-small-emitter' },
    { input: { isUltraSmallEmitter: true, provideFuelDetails: false }, step: 'check-your-answers' },
  ])('redirects a direct fuel visit to $step for an inactive branch', async ({ input, step }) => {
    await setup(payloadFixture({ emissionsTradingSchemeDataInputDetails: input }));
    expect(router.navigate).toHaveBeenCalledWith([`../${step}`], expect.objectContaining({ replaceUrl: true }));
    gas().controls.deliveredEnergy.setValue('1');
    component.onSubmit();
    expect(save).not.toHaveBeenCalled();
  });

  it('restores persisted correction data, displays the filtered standard order and hides in-cell labels', async () => {
    await setup();
    expect(rows().controls.map((row) => row.controls.fuelKey.value)).toEqual(ETS_FUEL_ENTRIES.map(([key]) => key));
    expect(rows().length).toBe(12);
    expect(gas().controls.deliveredEnergy.value).toBe('1000');
    expect(fixture.nativeElement.querySelector('[formControlName="deliveredEnergy"] input').value).toBe('1,000');
    expect(fixture.nativeElement.querySelector('h2').textContent.trim()).toBe('Reporting year');
    expect(fixture.nativeElement.querySelector('h2 + p').textContent.trim()).toBe('2026');
    expect(text()).toContain('Delivered UK ETS energy');
    expect(text()).not.toContain('70%');
    expect(text()).not.toContain('Special reporting methodology');
    assertInCellControlLabelsHidden(fixture.nativeElement);
  });

  it.each<[EtsMeasurementType, string, string, string]>([
    ['ENERGY_KWH', 'kWh', 'Primary energy (kWh)', '1,000'],
    ['ENERGY_MWH', 'MWh', 'Primary energy (MWh)', '1,000'],
    ['ENERGY_GJ', 'GJ', 'Primary energy (GJ)', '1,000'],
    ['CARBON_KG', 'kWh', 'Primary CO2e (kgCO2e)', '182.54'],
    ['CARBON_TONNE', 'kWh', 'Primary CO2e (tCO2e)', '0.18254'],
  ])('uses the %s workflow snapshot for units and primary amounts', async (measurementType, unit, heading, primary) => {
    await setup(payloadFixture({ measurementType }));
    expect(text()).toContain(`CO2 conversion factor (kgCO2e/${unit})`);
    expect(text()).toContain(heading);
    const gasRow = fixture.nativeElement.querySelector('tbody tr');
    expect(gasRow.querySelectorAll('td')[4].textContent.trim()).toBe(primary);
  });

  it.each<[EtsMeasurementType, string, string]>([
    ['CARBON_KG', '2.5', '5'],
    ['CARBON_TONNE', '0.0025', '0.005'],
  ])(
    'updates custom primary carbon while typing and saves the latest factor for %s',
    async (measurementType, initial, updated) => {
      await setup(payloadFixture({ measurementType }));
      component.onAddFuel();
      fixture.detectChanges();
      const customRow: HTMLElement = fixture.nativeElement.querySelectorAll('tbody tr')[12];
      const enterValue = (controlName: string, value: string) => {
        const input = customRow.querySelector<HTMLInputElement>(`[formControlName="${controlName}"] input`);
        input.value = value;
        input.dispatchEvent(new Event('input', { bubbles: true }));
        fixture.detectChanges();
      };

      enterValue('fuelType', 'Custom fuel');
      enterValue('deliveredEnergy', '10');
      enterValue('co2ConversionFactor', '0.25');
      expect(customRow.querySelectorAll('td')[4].textContent.trim()).toBe(initial);

      enterValue('co2ConversionFactor', '0.5');
      expect(customRow.querySelectorAll('td')[4].textContent.trim()).toBe(updated);
      expect(save).not.toHaveBeenCalled();

      fixture.nativeElement
        .querySelector('form')
        .dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
      expect(save).toHaveBeenCalledOnce();
      expect(
        save.mock.calls[0][0].requestTaskActionPayload.emissionsTradingSchemeDataInputDetails.energyFuelDetails
          .nonStandardFuels,
      ).toEqual([{ name: 'Custom fuel', conversionFactor: '0.5', deliveredEnergy: '10', primaryEnergy: updated }]);
    },
  );

  it('reopens valid API scientific decimals in plain editable decimal notation', async () => {
    const payload = payloadFixture();
    payload.emissionsTradingSchemeDataInputDetails.energyFuelDetails.standardFuels.NATURAL_GAS.deliveredEnergy = '1E-7';
    payload.emissionsTradingSchemeDataInputDetails.energyFuelDetails.nonStandardFuels = [
      { name: 'Custom', conversionFactor: '0E-7', deliveredEnergy: '2E+2', primaryEnergy: '200' },
    ];
    await setup(payload);
    expect(gas().controls.deliveredEnergy.value).toBe('0.0000001');
    expect(rows().at(12).getRawValue()).toMatchObject({ co2ConversionFactor: '0', deliveredEnergy: '200' });
    component.onSubmit();
    expect(save).toHaveBeenCalledOnce();
  });

  it('requires positive standard consumption when no custom fuel exists and exposes the error summary', async () => {
    await setup(payloadFixture({ emissionsTradingSchemeDataInputDetails: { isUltraSmallEmitter: false } }));
    expect(rows().controls.every((row) => row.controls.deliveredEnergy.value === '0')).toBe(true);
    component.onSubmit();
    fixture.detectChanges();
    expect(save).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('.govuk-error-summary').textContent).toContain(
      'At least one standard fuel must have a value greater than zero',
    );
    gas().controls.deliveredEnergy.setValue('0.0000001');
    component.onSubmit();
    expect(save).toHaveBeenCalledOnce();
  });

  it.each(['-1', '0.12345678', '', 'abc'])('rejects invalid standard delivered energy %s', async (value) => {
    await setup();
    const input: HTMLInputElement = fixture.nativeElement.querySelector('[formControlName="deliveredEnergy"] input');
    input.focus();
    input.value = value;
    input.dispatchEvent(new Event('input', { bubbles: true }));
    input.blur();
    component.onSubmit();
    expect(save).not.toHaveBeenCalled();
    expect(gas().controls.deliveredEnergy.invalid).toBe(true);
  });

  it('limits custom rows to ten and makes Add available after deletion', async () => {
    await setup();
    for (let index = 0; index < 10; index++) {
      fixture.nativeElement.querySelector('button[govukSecondaryButton]').click();
      fixture.detectChanges();
    }
    expect(rows().length).toBe(22);
    expect(text()).not.toContain('Add a fuel type');
    component.onAddFuel();
    expect(rows().length).toBe(22);
    component.onRemoveFuel(12);
    fixture.detectChanges();
    expect(rows().length).toBe(21);
    expect(text()).toContain('Add a fuel type');
    component.onRemoveFuel(0);
    expect(rows().length).toBe(21);
  });

  it('moves focus to the next custom row, then Add after deleting the last row', async () => {
    await setup();
    addCustom('First');
    addCustom('Second');
    fixture.detectChanges();
    component.onRemoveFuel(12);
    fixture.detectChanges();
    await fixture.whenStable();
    expect(document.activeElement).toBe(fixture.nativeElement.querySelectorAll('tbody tr')[12].querySelector('input'));
    component.onRemoveFuel(12);
    fixture.detectChanges();
    await fixture.whenStable();
    expect(document.activeElement?.textContent).toBe('Add a fuel type');
  });

  it('trims custom names, allows a zero factor and omits zero standard rows when saving', async () => {
    await setup();
    gas().controls.deliveredEnergy.setValue('0');
    addCustom('  Zero carbon fuel  ', '0', '25.1234567');
    component.onSubmit();
    expect(save).toHaveBeenCalledWith({
      requestTaskId: 42,
      requestTaskActionType: 'EMISSIONS_TRADING_SCHEME_DATA_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'EMISSIONS_TRADING_SCHEME_DATA_SAVE_PAYLOAD',
        emissionsTradingSchemeDataInputDetails: {
          isUltraSmallEmitter: false,
          energyFuelDetails: {
            standardFuels: {},
            nonStandardFuels: [
              {
                name: 'Zero carbon fuel',
                conversionFactor: '0',
                deliveredEnergy: '25.1234567',
                primaryEnergy: '25.1234567',
              },
            ],
            totalPrimaryEnergy: '25.1234567',
            totalCo2Emissions: '0',
          },
        },
        sectionsCompleted: { unrelated: 'COMPLETED', etsEnergyFuelDetails: 'IN_PROGRESS' },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], expect.any(Object));
  });

  it('shows required custom fields after submit and rejects duplicate trimmed names', async () => {
    await setup();
    const empty = addCustom('', '', null);
    component.onSubmit();
    fixture.detectChanges();
    expect(text()).toContain('Enter a name for the additional fuel type');
    expect(text()).toContain('Enter the conversion factor for the additional fuel type');
    expect(empty.invalid).toBe(true);
    expect(save).not.toHaveBeenCalled();
    empty.patchValue({ fuelType: '  Fuel  ', co2ConversionFactor: '0', deliveredEnergy: '1' });
    const duplicate = addCustom('fUeL');
    component.onSubmit();
    expect(empty.controls.fuelType.errors.duplicateName).toBe('Enter a unique name for this fuel');
    expect(duplicate.controls.fuelType.errors.duplicateName).toBe('Enter a unique name for this fuel');
    component.onRemoveFuel(rows().length - 1);
    expect(empty.controls.fuelType.hasError('duplicateName')).toBe(false);
  });

  it.each([' natural GAS ', 'lpg', 'Gas oil / Diesel'])(
    'rejects a custom name already used by a standard fuel: %s',
    async (name) => {
      await setup();
      const custom = addCustom(name, '0', '100');
      component.onSubmit();
      fixture.detectChanges();
      expect(custom.controls.fuelType.errors.duplicateName).toBe('Enter a unique name for this fuel');
      expect(text()).toContain('Enter a unique name for this fuel');
      expect(save).not.toHaveBeenCalled();

      custom.controls.fuelType.setValue('Distinct custom fuel');
      component.onSubmit();
      expect(custom.controls.fuelType.hasError('duplicateName')).toBe(false);
      expect(save).toHaveBeenCalledTimes(1);
    },
  );

  it.each(['0', '-1', '0.12345678'])('rejects invalid custom delivered energy %s', async (value) => {
    await setup();
    const custom = addCustom('Custom', '0.5', value);
    component.onSubmit();
    expect(custom.controls.deliveredEnergy.invalid).toBe(true);
    expect(save).not.toHaveBeenCalled();
  });

  it.each(['-1', '0.12345678'])('rejects invalid custom factor %s', async (value) => {
    await setup();
    const custom = addCustom('Custom', value);
    component.onSubmit();
    expect(custom.controls.co2ConversionFactor.invalid).toBe(true);
    expect(save).not.toHaveBeenCalled();
  });

  it('rejects whitespace-only and overlong custom names', async () => {
    await setup();
    const custom = addCustom('   ');
    component.onSubmit();
    expect(custom.controls.fuelType.invalid).toBe(true);
    custom.controls.fuelType.setValue('x'.repeat(256));
    fixture.detectChanges();
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
    fixture.detectChanges();
    expect(custom.controls.fuelType.hasError('maxlength')).toBe(true);
    const message = 'Enter a fuel name with 255 characters or fewer';
    expect(fixture.nativeElement.querySelector('.govuk-error-summary').textContent).toContain(message);
    expect(fixture.nativeElement.querySelector('.govuk-error-message').textContent).toContain(message);
    expect(text()).not.toContain('[object Object]');
    expect(save).not.toHaveBeenCalled();
    custom.controls.fuelType.setValue('x'.repeat(255));
    component.onSubmit();
    expect(custom.controls.fuelType.valid).toBe(true);
    expect(save).toHaveBeenCalledTimes(1);
  });

  it.each([0, 400])('shows a save error for HTTP status %s, retains the draft and permits retry', async (status) => {
    await setup();
    gas().controls.deliveredEnergy.setValue('2000');
    save.mockReturnValueOnce(throwError(() => new HttpErrorResponse({ status })));
    component.onSubmit();
    fixture.detectChanges();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(gas().controls.deliveredEnergy.value).toBe(2000);
    const errorSummary = fixture.nativeElement.querySelector('cca-error-summary [role="alert"]');
    expect(errorSummary.textContent).toContain('Your answers could not be saved. Please try again.');
    expect(document.activeElement).toBe(errorSummary);
    const payload = store.state.requestTaskItem.requestTask.payload as EtsReportingTaskPayload;
    expect(payload.sectionsCompleted.etsEnergyFuelDetails).toBe('COMPLETED');
    component.onSubmit();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('cca-error-summary')).toBeNull();
    expect(router.navigate).toHaveBeenCalledOnce();
  });

  it('navigates only after a successful response and prevents repeated pending saves', async () => {
    await setup();
    const response = new Subject<unknown>();
    save.mockReturnValue(response.pipe(TestBed.inject(PendingRequestService).trackRequest()));
    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]') as HTMLButtonElement;
    submitButton.click();
    expect(submitButton.disabled).toBe(true);
    submitButton.click();
    expect(save).toHaveBeenCalledOnce();
    expect(router.navigate).not.toHaveBeenCalled();
    response.next({});
    response.complete();
    expect(router.navigate).toHaveBeenCalledOnce();
  });
});
