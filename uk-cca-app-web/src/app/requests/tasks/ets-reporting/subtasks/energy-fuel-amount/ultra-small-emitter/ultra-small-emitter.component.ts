import { Component, inject, signal, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DetailsComponent, LegendContentDirective, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import { TasksApiService } from '@requests/common';
import { ErrorSummaryComponent, ErrorSummaryInfo, WizardStepComponent } from '@shared/components';

import { etsReportingQuery } from '../../../ets-reporting.selectors';
import { createEtsSaveDto, updateEtsUseAnswers } from '../../../ets-reporting.transform';
import { requiresEtsFuelDetails } from '../../../ets-reporting.validation';
import { ULTRA_SMALL_EMITTER_FORM, UltraSmallEmitterFormProvider } from './ultra-small-emitter-form.provider';

@Component({
  selector: 'cca-ets-ultra-small-emitter',
  templateUrl: './ultra-small-emitter.component.html',
  providers: [UltraSmallEmitterFormProvider],
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    DetailsComponent,
    RadioComponent,
    RadioOptionComponent,
    LegendContentDirective,
    ErrorSummaryComponent,
    ReturnToTaskOrActionPageComponent,
  ],
})
export class UltraSmallEmitterComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly api = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly wizard = viewChild(WizardStepComponent);

  readonly form = inject(ULTRA_SMALL_EMITTER_FORM);
  readonly isEditable = this.store.select(requestTaskQuery.selectIsEditable);
  readonly isUltraSmallEmitter = toSignal(this.form.controls.isUltraSmallEmitter.valueChanges, {
    initialValue: this.form.controls.isUltraSmallEmitter.value,
  });
  readonly errorSummaryInfo = signal<ErrorSummaryInfo | null>(null);

  onSubmit(): void {
    if (!this.isEditable()) return;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.wizard()?.showErrorSummary();
      return;
    }
    this.errorSummaryInfo.set(null);
    const { isUltraSmallEmitter, provideFuelDetails } = this.form.getRawValue();
    const payload = updateEtsUseAnswers(
      this.store.select(etsReportingQuery.selectPayload)(),
      isUltraSmallEmitter,
      provideFuelDetails,
    );
    const dto = createEtsSaveDto(this.store.select(requestTaskQuery.selectRequestTaskId)(), payload);
    this.api
      .saveRequestTaskAction(dto)
      .pipe(
        catchError(() => {
          this.errorSummaryInfo.set({ message: 'Your answers could not be saved. Please try again.' });
          return EMPTY;
        }),
      )
      .subscribe(() => {
        const step = requiresEtsFuelDetails(payload.emissionsTradingSchemeDataInputDetails)
          ? 'details'
          : 'check-your-answers';
        this.router.navigate([`../${step}`], { relativeTo: this.route });
      });
  }
}
