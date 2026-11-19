import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TasksApiService } from '@requests/common';
import { ErrorSummaryComponent, ErrorSummaryInfo } from '@shared/components';

import { EtsEnergyFuelSummaryComponent } from '../../../components/ets-energy-fuel-summary.component';
import { etsReportingQuery } from '../../../ets-reporting.selectors';
import { createEtsSaveDto, removeEtsNonStandardFuel } from '../../../ets-reporting.transform';
import { isEtsReportingComplete } from '../../../ets-reporting.validation';

@Component({
  selector: 'cca-ets-energy-fuel-amount-check-your-answers',
  template: `
    @if (error(); as errorInfo) {
      <cca-error-summary [errorSummaryInfo]="errorInfo" />
    }
    <netz-page-heading caption="UK ETS energy/fuel details">Check your answers</netz-page-heading>
    <cca-ets-energy-fuel-summary
      [inputDetails]="inputDetails()"
      [measurementType]="measurementType()"
      [isEditable]="isEditable()"
      (removeFuel)="onRemoveFuel($event)"
    />
    @if (isEditable()) {
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    }
    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    ButtonDirective,
    ErrorSummaryComponent,
    EtsEnergyFuelSummaryComponent,
    PageHeadingComponent,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
  ],
})
export class EnergyFuelAmountDetailsCheckYourAnswersComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly inputDetails = this.store.select(etsReportingQuery.selectInputDetails);
  protected readonly measurementType = this.store.select(etsReportingQuery.selectMeasurementType);
  protected readonly isEditable = this.store.select(requestTaskQuery.selectIsEditable);
  protected readonly error = signal<ErrorSummaryInfo | null>(null);

  onRemoveFuel(index: number): void {
    if (!this.isEditable()) return;
    const payload = this.store.select(etsReportingQuery.selectPayload)();
    const updatedPayload = removeEtsNonStandardFuel(payload, index);
    if (updatedPayload === payload) return;

    this.error.set(null);
    this.tasksApiService
      .saveRequestTaskAction(
        createEtsSaveDto(this.store.select(requestTaskQuery.selectRequestTaskId)(), updatedPayload),
      )
      .pipe(
        catchError(() => {
          this.error.set({ message: 'The fuel could not be deleted. Please try again.' });
          return EMPTY;
        }),
      )
      .subscribe();
  }

  onSubmit(): void {
    if (!this.isEditable()) return;
    this.error.set(null);
    const payload = this.store.select(etsReportingQuery.selectPayload)();
    if (!isEtsReportingComplete(payload)) {
      this.error.set({
        message: 'Complete the UK ETS energy/fuel details before confirming your answers.',
        link: '../',
        linkText: 'Return to UK ETS energy/fuel details',
      });
      return;
    }

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    this.tasksApiService
      .saveRequestTaskAction(createEtsSaveDto(requestTaskId, payload, 'COMPLETED'))
      .pipe(
        catchError(() => {
          this.error.set({ message: 'Your answers could not be saved. Please try again.' });
          return EMPTY;
        }),
      )
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.route, replaceUrl: true }));
  }
}
