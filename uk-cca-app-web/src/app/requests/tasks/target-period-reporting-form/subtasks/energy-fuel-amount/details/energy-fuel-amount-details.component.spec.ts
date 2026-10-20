import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import {
  ITEM_TYPE_TO_RETURN_TEXT_MAPPER,
  RequestTaskState,
  RequestTaskStore,
  TYPE_AWARE_STORE,
} from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { expect, Mocked } from 'vitest';

import {
  PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload,
  PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
} from 'cca-api';

import {
  mockTprRequestTaskState,
  mockTprRequestTaskStateChpZero,
  mockTprRequestTaskStateNoFuels,
} from '../../../testing/mock-data';
import { EnergyFuelAmountDetailsComponent } from './energy-fuel-amount-details.component';

describe('EnergyFuelAmountDetailsComponent', () => {
  let component: EnergyFuelAmountDetailsComponent;
  let fixture: ComponentFixture<EnergyFuelAmountDetailsComponent>;
  let store: RequestTaskStore;
  let tasksApiService: Mocked<Pick<TasksApiService, 'saveRequestTaskAction'>>;
  let router: Router;

  const getForm = () => component.form;

  const withReportingMechanism = (state: RequestTaskState, usedReportingMechanism: boolean) => ({
    ...state,
    requestTaskItem: {
      ...state.requestTaskItem,
      requestTask: {
        ...state.requestTaskItem.requestTask,
        payload: {
          ...(state.requestTaskItem.requestTask.payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload),
          referenceData: {
            ...(state.requestTaskItem.requestTask.payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload)
              .referenceData,
            baselineAndTargets: {
              ...(
                state.requestTaskItem.requestTask.payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
              ).referenceData.baselineAndTargets,
              usedReportingMechanism,
            },
          },
        },
      },
    },
  });

  const setupComponent = async (state = mockTprRequestTaskState) => {
    tasksApiService = { saveRequestTaskAction: vi.fn().mockReturnValue(of({})) };

    await TestBed.configureTestingModule({
      imports: [EnergyFuelAmountDetailsComponent],
      providers: [
        RequestTaskStore,
        { provide: Router, useValue: { navigate: vi.fn() } },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Report energy/fuel consumption' },
        { provide: TasksApiService, useValue: tasksApiService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    store = TestBed.inject(RequestTaskStore);
    store.setState(state);

    fixture = TestBed.createComponent(EnergyFuelAmountDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  afterEach(() => {
    vi.clearAllMocks();
    TestBed.resetTestingModule();
  });

  describe('Carbon measurement facility', () => {
    const carbonState = {
      ...mockTprRequestTaskState,
      requestTaskItem: {
        ...mockTprRequestTaskState.requestTaskItem,
        requestTask: {
          ...mockTprRequestTaskState.requestTaskItem.requestTask,
          payload: {
            ...mockTprRequestTaskState.requestTaskItem.requestTask.payload,
            referenceData: {
              ...(
                mockTprRequestTaskState.requestTaskItem.requestTask
                  .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
              ).referenceData,
              baselineAndTargets: {
                ...(
                  mockTprRequestTaskState.requestTaskItem.requestTask
                    .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
                ).referenceData?.baselineAndTargets,
                measurementType: 'CARBON_KG',
              },
            },
          },
        },
      },
    };

    beforeEach(async () => {
      await setupComponent(carbonState);
    });

    it('should display Primary CO2e header instead of Primary energy', () => {
      // The primary energy/carbon column is the 5th column (index 4) in the table
      const primaryHeader = fixture.nativeElement.querySelector('thead th:nth-child(5)');
      expect(primaryHeader?.textContent?.trim()).toBe('Primary CO2e (kgCO2e)');
    });

    it('should add a custom fuel row without throwing errors', () => {
      const initialCount = getForm().controls.fuels.length;

      expect(() => {
        component.onAddFuel();
        fixture.detectChanges();
      }).not.toThrow();

      expect(getForm().controls.fuels.length).toBe(initialCount + 1);
    });

    it('should render primary carbon for standard fuel row', () => {
      // Natural gas: delivered 1000, CO2 factor 0.18254, PE factor 1
      // primaryCarbon = 1000 × 1 × 0.18254 = 182.54 kgCO2e
      const rows = Array.from<Element>(fixture.nativeElement.querySelectorAll('tbody tr'));
      const naturalGasRow = rows.find((row) => row.textContent?.includes('Natural gas'));
      const primaryCell = naturalGasRow?.querySelectorAll('.govuk-table__cell')[4];
      expect(primaryCell?.textContent?.trim()).toBe('182.54');
    });
  });

  describe('Initialization', () => {
    beforeEach(() => setupComponent());

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the 70% eligible activities question for the reporting period', () => {
      expect(fixture.nativeElement.textContent).toContain(
        'Was at least 70% of the total energy used in carrying out eligible activities for this period?',
      );
    });

    it('should use the reporting period validation message for the 70% question', () => {
      const control = getForm().controls.atLeastSeventyPercentEnergyUsed;
      control.setValue(null);

      expect(control.errors?.['required']).toBe(
        'Select yes if at least 70% of the total energy was used in carrying out eligible activities for this period.',
      );
    });

    it('should pre-populate standard fuel delivered energy values from the store', () => {
      const form = getForm();
      const naturalGasRow = form.controls.fuels.controls.find((ctrl) => ctrl.controls.fuelKey.value === 'NATURAL_GAS');
      const gridRow = form.controls.fuels.controls.find((ctrl) => ctrl.controls.fuelKey.value === 'GRID_ELECTRICITY');

      expect(Number(naturalGasRow?.controls.deliveredEnergy.value)).toBe(1000);
      expect(Number(gridRow?.controls.deliveredEnergy.value)).toBe(500);
    });

    it('should pre-populate standard fuel CO2 conversion factors using measurement units', () => {
      const form = getForm();
      const naturalGasRow = form.controls.fuels.controls.find((ctrl) => ctrl.controls.fuelKey.value === 'NATURAL_GAS');

      expect(Number(naturalGasRow?.controls.co2ConversionFactor.value)).toBeCloseTo(0.18254, 7);
    });

    it('should include the SRM control for this task', () => {
      const form = getForm();
      expect(form.controls.specialReportingMethodology).toBeDefined();
    });

    it('should pre-populate the SRM value when reporting mechanism is used', () => {
      const form = getForm();
      expect(Number(form.controls.specialReportingMethodology.value)).toBe(200);
      expect(form.controls.specialReportingMethodology.disabled).toBe(false);
    });
  });

  describe('Initialization with no prior data', () => {
    beforeEach(() => {
      const stateWithoutEnergyFuelDetails = {
        ...mockTprRequestTaskStateNoFuels,
        requestTaskItem: {
          ...mockTprRequestTaskStateNoFuels.requestTaskItem,
          requestTask: {
            ...mockTprRequestTaskStateNoFuels.requestTaskItem.requestTask,
            payload: {
              ...(mockTprRequestTaskStateNoFuels.requestTaskItem.requestTask
                .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload),
              performanceData: {
                throughputDetails: null,
                calculatedResults: null,
              },
            } as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
          },
        },
      };

      return setupComponent(stateWithoutEnergyFuelDetails as RequestTaskState);
    });

    it('should initialize the SRM value as null when no energy fuel details exist', () => {
      const form = getForm();
      expect(form.controls.specialReportingMethodology.value).toBeNull();
    });
  });

  describe('SRM visibility', () => {
    beforeEach(() => setupComponent());

    it('should show the SRM field when reporting mechanism is used', () => {
      fixture.detectChanges();
      const element: HTMLElement = fixture.nativeElement;
      expect(element.textContent).toContain('Special reporting methodology');
    });
  });

  describe('SRM visibility when reporting mechanism is not used', () => {
    beforeEach(() => setupComponent(withReportingMechanism(mockTprRequestTaskState, false)));

    it('should hide the SRM field and throughput factor', () => {
      fixture.detectChanges();

      const element: HTMLElement = fixture.nativeElement;
      expect(element.textContent).not.toContain('Special reporting methodology');
      expect(element.textContent).not.toContain('Throughput adjustment factor');
    });

    it('should disable and clear the SRM control', () => {
      const form = getForm();

      expect(form.controls.specialReportingMethodology.disabled).toBe(true);
      expect(form.controls.specialReportingMethodology.value).toBeNull();
    });
  });

  describe('Throughput adjustment factor', () => {
    beforeEach(() => setupComponent());

    it('should display the calculated throughput adjustment factor', () => {
      // GRID=500, NON_GRID=0, CHP=200 => 500/(500+200) ≈ 0.7142857
      fixture.detectChanges();
      const element: HTMLElement = fixture.nativeElement;
      expect(element.textContent).toContain('0.7142857');
    });

    it('should remain rendered when an energy input contains invalid characters', () => {
      const gridElectricity = getForm().controls.fuels.controls.find(
        (control) => control.controls.fuelKey.value === 'GRID_ELECTRICITY',
      )!.controls.deliveredEnergy;
      gridElectricity.setValue('`');

      expect(() => fixture.detectChanges()).not.toThrow();
      expect(gridElectricity.invalid).toBe(true);
    });
  });

  describe('Throughput adjustment factor when all electricity is zero', () => {
    beforeEach(() => setupComponent(mockTprRequestTaskStateChpZero));

    it('should return 1 when all electricity values are zero', () => {
      // standardFuels: {}, CHP: '0' => factor = 0/(0+0) = denominator 0 → 1
      fixture.detectChanges();
      const element: HTMLElement = fixture.nativeElement;
      expect(element.textContent).toContain('Throughput adjustment factor');
      expect(element.textContent).toMatch(/Throughput adjustment factor\s+1/);
    });
  });

  describe('Primary carbon rendering', () => {
    beforeEach(() =>
      setupComponent({
        ...mockTprRequestTaskState,
        requestTaskItem: {
          ...mockTprRequestTaskState.requestTaskItem,
          requestTask: {
            ...mockTprRequestTaskState.requestTaskItem.requestTask,
            payload: {
              ...mockTprRequestTaskState.requestTaskItem.requestTask.payload,
              referenceData: {
                ...(
                  mockTprRequestTaskState.requestTaskItem.requestTask
                    .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
                ).referenceData,
                baselineAndTargets: {
                  ...(
                    mockTprRequestTaskState.requestTaskItem.requestTask
                      .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
                  ).referenceData?.baselineAndTargets,
                  measurementType: 'CARBON_KG',
                },
              },
            },
          },
        },
      } as RequestTaskState),
    );

    it('should display primary carbon values using CO2 factors', () => {
      fixture.detectChanges();

      const element: HTMLElement = fixture.nativeElement;
      expect(element.textContent).toContain('Primary CO2e (kgCO2e)');
      expect(element.textContent).toContain('182.54');
    });

    it('should use kWh as the delivered energy suffix', () => {
      fixture.detectChanges();

      const element: HTMLElement = fixture.nativeElement;
      const suffixes = Array.from(element.querySelectorAll('.govuk-input__suffix')).map((suffix) =>
        suffix.textContent?.trim(),
      );

      expect(suffixes).toContain('kWh');
      expect(suffixes).not.toContain('kg');
    });
  });

  describe('Custom fuel rows', () => {
    beforeEach(() => setupComponent());

    it('should add a custom fuel row when "Add a fuel type" is clicked', () => {
      const form = getForm();
      const initialCount = form.controls.fuels.length;

      component.onAddFuel();

      expect(form.controls.fuels.length).toBe(initialCount + 1);
      const addedFuel = form.controls.fuels.controls[form.controls.fuels.length - 1];
      expect(addedFuel.controls.isCustom.value).toBe(true);
      expect(addedFuel.controls.co2ConversionFactor.value).toBe('');
    });

    it('should not add more than 10 custom fuel rows', () => {
      for (let i = 0; i < 11; i++) {
        component.onAddFuel();
      }
      const form = getForm();
      const customCount = form.controls.fuels.controls.filter((c) => c.controls.isCustom.value).length;
      expect(customCount).toBe(10);
    });

    it('should remove a custom fuel row at the given index', () => {
      component.onAddFuel();
      const form = getForm();
      const indexToRemove = form.controls.fuels.length - 1;
      const countBefore = form.controls.fuels.length;

      component.onRemoveFuel(indexToRemove);

      expect(form.controls.fuels.length).toBe(countBefore - 1);
    });

    it('should return the duplicate non-standard fuel names validation error', () => {
      component.onAddFuel();
      component.onAddFuel();

      const fuels = getForm().controls.fuels;
      const customFuels = fuels.controls.filter((control) => control.controls.isCustom.value);
      customFuels[0].controls.fuelType.setValue('Custom fuel');
      customFuels[1].controls.fuelType.setValue('Custom fuel');
      fuels.updateValueAndValidity();

      expect(fuels.errors).toEqual({
        duplicateNames: 'Duplicate non-standard fuel names were found. Please ensure each fuel name is unique.',
      });
    });
  });

  describe('Form submission', () => {
    beforeEach(() => setupComponent());

    const setDeliveredEnergyForFuel = (fuelKey: string, value: string) => {
      const form = getForm();
      const fuelRow = form.controls.fuels.controls.find((ctrl) => ctrl.controls.fuelKey.value === fuelKey)!;
      fuelRow.controls.deliveredEnergy.setValue(value);
    };

    it('should call the API with IN_PROGRESS status and navigate to check-your-answers on valid submit', () => {
      vi.spyOn(router, 'navigate');

      component.onSubmit();

      expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith(
        expect.objectContaining({
          requestTaskId: 42,
          requestTaskActionType: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SAVE_APPLICATION',
          requestTaskActionPayload: expect.objectContaining({
            sectionsCompleted: expect.objectContaining({ tprEnergyFuelDetails: 'IN_PROGRESS' }),
          }),
        }),
      );
      expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], expect.any(Object));
    });

    it('should exclude zero-value standard fuels from the submitted payload', () => {
      component.onSubmit();

      const dto = tasksApiService.saveRequestTaskAction.mock.calls[0][0];
      const standardFuels = (
        dto.requestTaskActionPayload as PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload
      ).energyFuelDetails.standardFuels;

      expect(standardFuels['NATURAL_GAS']).toBeDefined();
      expect(standardFuels['GRID_ELECTRICITY']).toBeDefined();
      expect(Object.values(standardFuels).every((v) => Number(v) !== 0)).toBe(true);
    });

    it('should not call the API when a standard fuel has a negative delivered energy', () => {
      const form = getForm();
      const naturalGasCtrl = form.controls.fuels.controls.find((ctrl) => ctrl.controls.fuelKey.value === 'NATURAL_GAS')!
        .controls.deliveredEnergy;

      naturalGasCtrl.setValue('-1');

      component.onSubmit();

      expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();
    });

    it('should not call the API when a custom fuel row is incomplete on submit', () => {
      component.onAddFuel();

      component.onSubmit();

      expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();
    });

    it('should not call the API when SRM is required but empty', () => {
      const form = getForm();
      form.controls.specialReportingMethodology.setValue(null);

      component.onSubmit();

      expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();
    });

    it('should not call the API when CHP is entered but both grid and non-grid electricity are zero', () => {
      const form = getForm();
      setDeliveredEnergyForFuel('GRID_ELECTRICITY', '0');
      setDeliveredEnergyForFuel('NON_GRID_ELECTRICITY', '0');
      form.controls.specialReportingMethodology.setValue('100');

      component.onSubmit();

      expect(form.controls.specialReportingMethodology.errors).toEqual(
        expect.objectContaining({ srmInconsistent: 'Input inconsistent with SRM rules. Contact your regulator' }),
      );
      expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();
    });

    it('should set SRM error when fuel values change without touching SRM field', () => {
      const form = getForm();
      // Set CHP to a value greater than 0 first
      form.controls.specialReportingMethodology.setValue('100');
      fixture.detectChanges();

      // Now change only the fuel values — don't touch SRM
      setDeliveredEnergyForFuel('GRID_ELECTRICITY', '0');
      setDeliveredEnergyForFuel('NON_GRID_ELECTRICITY', '0');
      fixture.detectChanges();

      // With the group-level validator, error should be set without touching SRM
      expect(form.controls.specialReportingMethodology.errors).toEqual(
        expect.objectContaining({ srmInconsistent: 'Input inconsistent with SRM rules. Contact your regulator' }),
      );

      component.onSubmit();
      expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();
    });

    it('should clear SRM error when fuel values are corrected without touching SRM field', () => {
      const form = getForm();
      // Set up inconsistent state
      form.controls.specialReportingMethodology.setValue('100');
      setDeliveredEnergyForFuel('GRID_ELECTRICITY', '0');
      setDeliveredEnergyForFuel('NON_GRID_ELECTRICITY', '0');
      fixture.detectChanges();

      expect(form.controls.specialReportingMethodology.hasError('srmInconsistent')).toBe(true);

      // Correct grid electricity — don't touch SRM
      setDeliveredEnergyForFuel('GRID_ELECTRICITY', '1');
      fixture.detectChanges();

      expect(form.controls.specialReportingMethodology.hasError('srmInconsistent')).toBe(false);
    });

    it('should allow submission when CHP is entered and non-grid electricity is greater than zero', () => {
      const form = getForm();
      setDeliveredEnergyForFuel('GRID_ELECTRICITY', '1');
      setDeliveredEnergyForFuel('NON_GRID_ELECTRICITY', '0');
      form.controls.specialReportingMethodology.setValue('100');

      component.onSubmit();

      expect(form.controls.specialReportingMethodology.hasError('srmInconsistent')).toBe(false);
      expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
    });

    it('should allow submission when CHP is entered and non-grid electricity is greater than zero', () => {
      const form = getForm();
      setDeliveredEnergyForFuel('GRID_ELECTRICITY', '0');
      setDeliveredEnergyForFuel('NON_GRID_ELECTRICITY', '1');
      form.controls.specialReportingMethodology.setValue('100');

      component.onSubmit();

      expect(form.controls.specialReportingMethodology.hasError('srmInconsistent')).toBe(false);
      expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
    });

    it('should persist primary carbon values when measurement type is carbon', () => {
      store.setState({
        ...mockTprRequestTaskState,
        requestTaskItem: {
          ...mockTprRequestTaskState.requestTaskItem,
          requestTask: {
            ...mockTprRequestTaskState.requestTaskItem.requestTask,
            payload: {
              ...mockTprRequestTaskState.requestTaskItem.requestTask.payload,
              referenceData: {
                ...(
                  mockTprRequestTaskState.requestTaskItem.requestTask
                    .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
                ).referenceData,
                baselineAndTargets: {
                  ...(
                    mockTprRequestTaskState.requestTaskItem.requestTask
                      .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
                  ).referenceData?.baselineAndTargets,
                  measurementType: 'CARBON_KG',
                },
              },
            },
          },
        },
      } as RequestTaskState);

      fixture.detectChanges();
      component.onSubmit();

      const dto = tasksApiService.saveRequestTaskAction.mock.calls[0][0];
      const standardFuels = (
        dto.requestTaskActionPayload as PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload
      ).energyFuelDetails.standardFuels;

      // When the form was originally created with ENERGY_KWH, the form controls already
      // have the kWh-based co2ConversionFactors. Since the form was not recreated after
      // the store state change, the onSubmit still uses those values. This mirrors the
      // real-world scenario where a measurement type would never change mid-session.
      expect(Number(standardFuels['NATURAL_GAS'].primaryEnergy)).toBeCloseTo(182.54, 7);
      expect(Number(standardFuels['GRID_ELECTRICITY'].primaryEnergy)).toBeCloseTo(105.483, 7);
    });
  });

  describe('Form submission when reporting mechanism is not used', () => {
    beforeEach(() => setupComponent(withReportingMechanism(mockTprRequestTaskState, false)));

    it('should persist electricitySuppliedFromCHP as null', () => {
      component.onSubmit();

      const dto = tasksApiService.saveRequestTaskAction.mock.calls[0][0];
      expect(
        (dto.requestTaskActionPayload as PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload)
          .energyFuelDetails.electricitySuppliedFromCHP,
      ).toBeNull();
    });
  });
});
