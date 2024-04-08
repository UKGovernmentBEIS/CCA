import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { BehaviorSubject, EMPTY, map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { catchBadRequest, ErrorCodes as BusinessErrorCode } from '@error/business-errors';

import { GovukValidators } from 'govuk-components';

import { CustomMiReportParams, MiReportsService } from 'cca-api';

import { ExtendedMiReportResult } from '../core/mi-interfaces';
import { manipulateResultsAndExportToExcel } from '../core/mi-report';

@Component({
  selector: 'cca-custom',
  templateUrl: './custom.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class CustomReportComponent {
  errorMessage$ = new BehaviorSubject<string>(null);

  reportOptionsForm: FormGroup = this.fb.group({
    query: [null, [GovukValidators.required('Query must not be empty')]],
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly miReportsService: MiReportsService,
    readonly pendingRequest: PendingRequestService,
  ) {}

  exportToExcel() {
    if (this.reportOptionsForm.valid) {
      this.miReportsService
        .generateCustomReport({
          reportType: 'CUSTOM',
          sqlQuery: this.reportOptionsForm.get('query').value,
        } as CustomMiReportParams)
        .pipe(
          this.pendingRequest.trackRequest(),
          catchBadRequest(BusinessErrorCode.REPORT1001, (res) => {
            this.errorMessage$.next(res.error.message);
            return EMPTY;
          }),
        )
        .pipe(
          map((results: ExtendedMiReportResult) => {
            manipulateResultsAndExportToExcel(results, 'Custom sql report');
          }),
        )
        .subscribe({
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          next: (_) => this.errorMessage$.next(null),
          error: (err) => this.errorMessage$.next(err.message),
        });
    }
  }
}
