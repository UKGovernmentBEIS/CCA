import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY, of, switchMap, take, tap, throwError } from 'rxjs';

import { isBadRequest } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ItemLinkPipe } from '@netz/common/pipes';
import { ButtonDirective, ErrorSummaryComponent, SelectComponent } from '@netz/govuk-components';
import { isGloballyHandledHttpError } from '@shared/services';

import { RequestItemsService, RequestsService } from 'cca-api';

import { ETS_REPORTING_FORM, etsReportingFormProvider } from './ets-reporting-form.provider';

const ETS_REPORTING_ERROR_MESSAGES = {
  ETS1001: 'This facility is not eligible to report for the year you selected. Select a different year.',
  ETS1002: 'UK ETS reporting is not available for the year you selected. Select a different year.',
  ETS1003: 'The reporting period for the year you selected has not started yet.',
  REQUEST_CREATE_ACTION1000:
    'You cannot start UK ETS reporting because a task is already in progress or reporting is unavailable for this facility.',
};

@Component({
  selector: 'cca-ets-reporting',
  templateUrl: './ets-reporting.component.html',
  providers: [etsReportingFormProvider],
  imports: [
    ReactiveFormsModule,
    ButtonDirective,
    PendingButtonDirective,
    PageHeadingComponent,
    ErrorSummaryComponent,
    SelectComponent,
  ],
})
export class EtsReportingComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestsService = inject(RequestsService);
  private readonly requestItemsService = inject(RequestItemsService);
  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  readonly form = inject(ETS_REPORTING_FORM);
  readonly hasFormErrors = signal(false);
  readonly requestCreated = signal(false);
  readonly years = [2026, 2027, 2028, 2029, 2030].map((year) => ({ text: String(year), value: year }));

  onSubmit() {
    this.form.updateValueAndValidity();

    if (this.form.invalid) {
      this.hasFormErrors.set(true);
      return;
    }
    this.hasFormErrors.set(false);

    const requestCreateActionPayload = {
      payloadType: 'EMISSIONS_TRADING_SCHEME_DATA_CREATE_ACTION_PAYLOAD',
      targetYear: this.form.getRawValue().targetYear,
    };

    this.requestsService
      .processRequestCreateAction(
        { requestType: 'EMISSIONS_TRADING_SCHEME_DATA', requestCreateActionPayload },
        this.facilityId,
      )
      .pipe(
        take(1),
        catchError((err: unknown) => {
          const validationError = Object.entries(ETS_REPORTING_ERROR_MESSAGES).find(([code]) =>
            isBadRequest(err, code),
          );
          if (!validationError) return throwError(() => err);

          const [code, message] = validationError;
          const control = code === 'REQUEST_CREATE_ACTION1000' ? this.form : this.form.controls.targetYear;
          control.setErrors({ responseError: message });
          this.hasFormErrors.set(true);
          return EMPTY;
        }),
        tap(() => this.requestCreated.set(true)),
        switchMap(({ requestId }) =>
          this.requestItemsService
            .getItemsByRequest(requestId)
            .pipe(catchError((err: unknown) => (isGloballyHandledHttpError(err) ? EMPTY : of({ items: [] })))),
        ),
      )
      .subscribe(({ items }) => {
        const link = items?.length === 1 ? new ItemLinkPipe().transform(items[0]) : ['/dashboard'];
        this.router.navigate(link, { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
