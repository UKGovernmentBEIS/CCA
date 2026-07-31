import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY, map, of } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  ButtonDirective,
  DetailsComponent,
  GovukTableColumn,
  RadioComponent,
  RadioOptionComponent,
  TableComponent,
} from '@netz/govuk-components';
import {
  calculatePrimaryCarbon,
  calculatePrimaryEnergy,
  calculateThroughputAdjustmentFactor,
  isCarbonMeasurementType,
  primaryCarbonDisplayUnit,
  roundHalfUpTo7Decimals,
  TaskItemStatus,
  TasksApiService,
  TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK,
  tprFormQuery,
} from '@requests/common';
import { TextInputComponent, WizardStepComponent } from '@shared/components';
import { MEASUREMENT_TYPE_TO_UNIT_MAP, MeasurementUnit } from '@shared/pipes';
import { logger, to7DecimalPlacesNumber, toBigNumber, toNumber } from '@shared/utils';
import { produce } from 'immer';

import { createRequestTaskActionProcessDTO, toPerformanceDataFacilityDigitalFormSavePayload } from '../../../transform';
import {
  applyNonStandardFuelRowValidators,
  createNonStandardFuelRowGroup,
  ENERGY_FUEL_AMOUNT_DETAILS_FORM,
  EnergyFuelAmountDetailsFormProvider,
  FuelForm,
} from './energy-fuel-amount-details-form.provider';

@Component({
  selector: 'cca-energy-fuel-amount-details',
  templateUrl: './energy-fuel-amount-details.component.html',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    DetailsComponent,
    ReturnToTaskOrActionPageComponent,
    TableComponent,
    TextInputComponent,
    DecimalPipe,
    ButtonDirective,
    RadioComponent,
    RadioOptionComponent,
  ],
  providers: [EnergyFuelAmountDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnergyFuelAmountDetailsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  readonly form = inject<FuelForm>(ENERGY_FUEL_AMOUNT_DETAILS_FORM);

  private readonly selectReferenceData = this.requestTaskStore.select(tprFormQuery.selectReferenceData);

  protected readonly measurementUnit = computed<MeasurementUnit>(() => {
    const measurementType = this.selectReferenceData()?.baselineAndTargets?.measurementType;
    return measurementType ? MEASUREMENT_TYPE_TO_UNIT_MAP[measurementType] : MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH;
  });

  protected readonly usesReportingMechanism = computed(
    () => this.selectReferenceData()?.baselineAndTargets?.usedReportingMechanism,
  );

  private readonly isCarbonMeasurement = computed(() => isCarbonMeasurementType(this.measurementUnit()));

  protected readonly resolvedMeasurementUnit = computed<MeasurementUnit>(() =>
    this.isCarbonMeasurement() ? MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH : this.measurementUnit(),
  );

  protected readonly calculatePrimaryEnergy = calculatePrimaryEnergy;
  protected readonly calculatePrimaryCarbon = calculatePrimaryCarbon;

  private readonly fuelRows = toSignal(
    this.form.controls.fuels.valueChanges.pipe(map(() => this.form.controls.fuels.controls.slice())),
    { initialValue: this.form.controls.fuels.controls.slice() },
  );

  private readonly gridElectricityCtrl = this.form.controls.fuels.controls.find(
    (control) => control.controls.fuelKey.value === 'GRID_ELECTRICITY',
  )?.controls.deliveredEnergy;

  private readonly nonGridElectricityCtrl = this.form.controls.fuels.controls.find(
    (control) => control.controls.fuelKey.value === 'NON_GRID_ELECTRICITY',
  )?.controls.deliveredEnergy;

  protected readonly gridElectricity = toSignal(
    this.gridElectricityCtrl ? this.gridElectricityCtrl.valueChanges : of('0'),
    { initialValue: this.gridElectricityCtrl?.value ?? '0' },
  );

  protected readonly nonGridElectricity = toSignal(
    this.nonGridElectricityCtrl ? this.nonGridElectricityCtrl.valueChanges : of('0'),
    { initialValue: this.nonGridElectricityCtrl?.value ?? '0' },
  );

  protected readonly specialReportingMethodologyValue = toSignal(
    this.form.controls.specialReportingMethodology.valueChanges,
    { initialValue: this.form.controls.specialReportingMethodology.value },
  );

  protected readonly throughputAdjustmentFactor = computed(() =>
    calculateThroughputAdjustmentFactor(
      this.gridElectricity() ?? '0',
      this.nonGridElectricity() ?? '0',
      this.specialReportingMethodologyValue() ?? '0',
    ),
  );

  protected readonly nonStandardCount = computed(() => this.fuelRows().filter((row) => row.value.isCustom).length);

  readonly displayRounded = to7DecimalPlacesNumber;

  protected readonly tableColumns: Signal<GovukTableColumn[]> = computed(() => [
    { field: 'fuelType', header: 'Fuel type' },
    {
      field: 'co2ConversionFactor',
      header: `CO2 conversion factor (kgCO2e/${this.resolvedMeasurementUnit()})`,
    },
    { field: 'deliveredEnergy', header: 'Delivered energy (excluding UK ETS)' },
    { field: 'primaryEnergyConversionFactor', header: 'Primary energy conversion factor' },
    {
      field: 'primaryEnergy',
      header: this.isCarbonMeasurement()
        ? `Primary CO2e (${primaryCarbonDisplayUnit(this.measurementUnit())})`
        : `Primary energy (${this.measurementUnit()})`,
    },
    { field: 'actions', header: 'Actions' },
  ]);

  protected readonly tableData = computed(() => this.fuelRows());
  protected readonly isCarbonPrimaryOutput = this.isCarbonMeasurement;

  onAddFuel() {
    if (this.nonStandardCount() >= 10) return;
    this.form.controls.fuels.push(createNonStandardFuelRowGroup(this.fb));
  }

  onRemoveFuel(index: number) {
    this.form.controls.fuels.removeAt(index);
  }

  onSubmit() {
    this.form.controls.fuels.controls.forEach((row) => {
      applyNonStandardFuelRowValidators(row);
    });

    if (this.form.invalid) return;

    const payload = this.requestTaskStore.select(tprFormQuery.selectPayload)();
    const actionPayload = toPerformanceDataFacilityDigitalFormSavePayload(payload);

    const standardFuels = this.form.controls.fuels.controls.reduce<
      Record<string, { deliveredEnergy: string; primaryEnergy: string }>
    >((acc, ctrl) => {
      const deliveredEnergy = ctrl.value.deliveredEnergy;

      if (ctrl.value.fuelKey && !toBigNumber(deliveredEnergy).isZero()) {
        const primaryFactor = toNumber(ctrl.value.primaryEnergyConversionFactor);
        const conversionFactor = toNumber(ctrl.value.co2ConversionFactor);

        const primaryEnergy = this.isCarbonPrimaryOutput()
          ? roundHalfUpTo7Decimals(
              calculatePrimaryCarbon(deliveredEnergy, primaryFactor, conversionFactor, this.measurementUnit()),
            )
          : roundHalfUpTo7Decimals(calculatePrimaryEnergy(deliveredEnergy, primaryFactor));

        acc[ctrl.value.fuelKey] = {
          deliveredEnergy: deliveredEnergy ?? '0',
          primaryEnergy,
        };
      }

      return acc;
    }, {});

    const nonStandardFuels = this.form.controls.fuels.controls
      .filter((ctrl) => ctrl.value.isCustom)
      .map((ctrl) => {
        const deliveredEnergy = ctrl.value.deliveredEnergy;
        const conversionFactorBN = toBigNumber(ctrl.value.co2ConversionFactor);

        return {
          deliveredEnergy: deliveredEnergy ?? '0',
          name: ctrl.value.fuelType.trim(),
          conversionFactor: roundHalfUpTo7Decimals(conversionFactorBN),
          primaryEnergy: this.isCarbonPrimaryOutput()
            ? roundHalfUpTo7Decimals(
                calculatePrimaryCarbon(deliveredEnergy, 1, conversionFactorBN, this.measurementUnit()),
              )
            : roundHalfUpTo7Decimals(calculatePrimaryEnergy(deliveredEnergy, 1)),
        };
      });

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.energyFuelDetails = {
        atLeastSeventyPercentEnergyUsed: this.form.controls.atLeastSeventyPercentEnergyUsed.value ?? false,
        standardFuels,
        nonStandardFuels,
        electricitySuppliedFromCHP: this.usesReportingMechanism()
          ? String(this.form.controls.specialReportingMethodology.value ?? 0)
          : null,
        throughputAdjustmentFactor: this.usesReportingMechanism()
          ? roundHalfUpTo7Decimals(this.throughputAdjustmentFactor())
          : null,
      };
    });

    const currentSectionsCompleted = this.requestTaskStore.select(tprFormQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .pipe(
        catchError((error) => {
          logger.error(error);
          return EMPTY;
        }),
      )
      .subscribe(() => this.router.navigate(['../check-your-answers'], { relativeTo: this.route }));
  }
}
