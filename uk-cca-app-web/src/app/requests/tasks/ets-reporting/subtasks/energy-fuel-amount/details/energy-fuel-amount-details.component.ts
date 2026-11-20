import { DecimalPipe } from '@angular/common';
import {
  afterNextRender,
  Component,
  computed,
  ElementRef,
  inject,
  Injector,
  OnInit,
  signal,
  viewChild,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY, map } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, DetailsComponent, GovukTableColumn, TableComponent } from '@netz/govuk-components';
import { TasksApiService } from '@requests/common';
import { ErrorSummaryComponent, ErrorSummaryInfo, TextInputComponent, WizardStepComponent } from '@shared/components';
import { logger } from '@shared/utils';

import { etsReportingQuery } from '../../../ets-reporting.selectors';
import { createEtsSaveDto, updateEtsFuelDetails } from '../../../ets-reporting.transform';
import { EtsFuelRowInput } from '../../../ets-reporting.types';
import { areEtsUseAnswersComplete, requiresEtsFuelDetails } from '../../../ets-reporting.validation';
import {
  calculateEtsPrimaryAmount,
  createEtsFuelDetails,
  getEtsDeliveredUnit,
  getEtsPrimaryUnit,
  isEtsCarbonMeasurement,
} from '../../../ets-reporting-calculations';
import {
  applyNonStandardFuelRowValidators,
  createNonStandardFuelRowGroup,
  ENERGY_FUEL_AMOUNT_DETAILS_FORM,
  EnergyFuelAmountDetailsFormProvider,
} from './energy-fuel-amount-details-form.provider';

@Component({
  selector: 'cca-ets-energy-fuel-amount-details',
  templateUrl: './energy-fuel-amount-details.component.html',
  styles: `
    .fuel-conversion-factor {
      max-width: 12rem;
      margin-left: auto;
    }
    .fuel-delivered-energy {
      min-width: 14rem;
    }
  `,
  providers: [EnergyFuelAmountDetailsFormProvider],
  imports: [
    DecimalPipe,
    ReactiveFormsModule,
    WizardStepComponent,
    DetailsComponent,
    ReturnToTaskOrActionPageComponent,
    TableComponent,
    TextInputComponent,
    ButtonDirective,
    ErrorSummaryComponent,
  ],
})
export class EnergyFuelAmountDetailsComponent implements OnInit {
  private readonly store = inject(RequestTaskStore);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly injector = inject(Injector);
  private readonly fuelTable = viewChild('fuelTable', { read: ElementRef });
  private readonly addFuelButton = viewChild('addFuelButton', { read: ElementRef });
  private readonly wizardStep = viewChild(WizardStepComponent);

  readonly form = inject(ENERGY_FUEL_AMOUNT_DETAILS_FORM);
  protected readonly errorSummaryInfo = signal<ErrorSummaryInfo | null>(null);

  protected readonly targetYear = this.store.select(etsReportingQuery.selectTargetYear);
  protected readonly measurementType = this.store.select(etsReportingQuery.selectMeasurementType);
  protected readonly measurementUnit = computed(() => getEtsPrimaryUnit(this.measurementType()));
  protected readonly isCarbonPrimaryOutput = computed(() => isEtsCarbonMeasurement(this.measurementType()));
  protected readonly deliveredUnit = computed(() => getEtsDeliveredUnit(this.measurementType()));

  private readonly fuelRows = toSignal(
    this.form.controls.fuels.valueChanges.pipe(map(() => this.form.controls.fuels.controls.slice())),
    { initialValue: this.form.controls.fuels.controls.slice() },
  );
  protected readonly tableData = computed(() => this.fuelRows());
  protected readonly nonStandardCount = computed(
    () => this.fuelRows().filter((row) => row.controls.isCustom.value).length,
  );
  protected readonly tableColumns = computed<GovukTableColumn[]>(() => [
    { field: 'fuelType', header: 'Energy/fuel type', widthClass: 'govuk-!-width-one-quarter' },
    {
      field: 'co2ConversionFactor',
      header: `CO2 conversion factor (kgCO2e/${this.deliveredUnit()})`,
      isNumeric: true,
    },
    { field: 'deliveredEnergy', header: 'Delivered UK ETS energy' },
    {
      field: 'primaryEnergyConversionFactor',
      header: this.isCarbonPrimaryOutput() ? 'Primary conversion factor' : 'Primary energy conversion factor',
      isNumeric: true,
    },
    {
      field: 'primaryEnergy',
      isNumeric: true,
      header: this.isCarbonPrimaryOutput()
        ? `Primary CO2e (${this.measurementUnit()})`
        : `Primary energy (${this.measurementUnit()})`,
    },
    { field: 'actions', header: 'Actions', isNumeric: true },
  ]);

  ngOnInit(): void {
    this.ensureFuelBranch();
  }

  private ensureFuelBranch(): boolean {
    const input = this.store.select(etsReportingQuery.selectInputDetails)();
    const step = !areEtsUseAnswersComplete(input)
      ? 'ultra-small-emitter'
      : !requiresEtsFuelDetails(input)
        ? 'check-your-answers'
        : null;
    if (step) this.router.navigate([`../${step}`], { relativeTo: this.route, replaceUrl: true });
    return !step;
  }

  protected primaryAmount(row: EtsFuelRowInput): string {
    return calculateEtsPrimaryAmount(this.measurementType(), row).toFixed();
  }

  protected customFuelNumber(index: number): number {
    return index - (this.fuelRows().length - this.nonStandardCount()) + 1;
  }

  onAddFuel(): void {
    if (this.nonStandardCount() >= 10) return;
    this.form.controls.fuels.push(createNonStandardFuelRowGroup(this.fb));
  }

  onRemoveFuel(index: number): void {
    if (!this.form.controls.fuels.at(index)?.controls.isCustom.value) return;
    this.form.controls.fuels.removeAt(index);
    afterNextRender(
      {
        write: () => {
          const rows = this.fuelTable()?.nativeElement.querySelectorAll('tbody tr');
          const nextFuelInput = rows?.[index]?.querySelector('input');
          (nextFuelInput ?? this.addFuelButton()?.nativeElement)?.focus();
        },
      },
      { injector: this.injector },
    );
  }

  onSubmit(): void {
    if (!this.store.select(requestTaskQuery.selectIsEditable)()) return;
    if (!this.ensureFuelBranch()) return;
    this.errorSummaryInfo.set(null);
    this.form.controls.fuels.controls.forEach(applyNonStandardFuelRowValidators);
    this.form.controls.fuels.updateValueAndValidity();
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.wizardStep()?.showErrorSummary();
      return;
    }

    const payload = this.store.select(etsReportingQuery.selectPayload)();
    const details = createEtsFuelDetails(this.measurementType(), this.form.getRawValue().fuels);
    const updatedPayload = updateEtsFuelDetails(payload, details);
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createEtsSaveDto(requestTaskId, updatedPayload);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .pipe(
        catchError((error) => {
          logger.error(error);
          this.errorSummaryInfo.set({ message: 'Your answers could not be saved. Please try again.' });
          return EMPTY;
        }),
      )
      .subscribe(() => this.router.navigate(['../check-your-answers'], { relativeTo: this.route }));
  }
}

export default EnergyFuelAmountDetailsComponent;
