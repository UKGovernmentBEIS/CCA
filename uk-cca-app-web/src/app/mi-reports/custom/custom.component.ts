import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { EMPTY, map } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { PendingRequestService } from '@netz/common/services';
import { ButtonDirective, GovukValidators, TextareaComponent } from '@netz/govuk-components';

import { CustomMiReportParams, MiReportsService } from 'cca-api';

import { ExtendedMiReportResult } from '../core/mi-interfaces';
import { MiReportsExportService } from '../core/mi-reports-export.service';

@Component({
  selector: 'cca-custom',
  templateUrl: './custom.component.html',
  imports: [PageHeadingComponent, ReactiveFormsModule, TextareaComponent, ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CustomReportComponent {
  private readonly fb = inject(FormBuilder);
  private readonly miReportsService = inject(MiReportsService);
  private readonly miReportsExportService = inject(MiReportsExportService);
  private readonly pendingRequest = inject(PendingRequestService);

  protected readonly errorMessage = signal(null);

  protected readonly reportOptionsForm: FormGroup = this.fb.group({
    query: [null, [GovukValidators.required('Query must not be empty')]],
  });

  exportToExcel() {
    if (this.reportOptionsForm.valid) {
      this.miReportsService
        .generateCustomReport({
          reportType: 'CUSTOM',
          sqlQuery: this.reportOptionsForm.get('query').value,
        } as CustomMiReportParams)
        .pipe(
          this.pendingRequest.trackRequest(),
          catchBadRequest(ErrorCodes.REPORT1001, (res) => {
            this.errorMessage.set(res.error.message);
            return EMPTY;
          }),
          map((results: ExtendedMiReportResult) => {
            this.miReportsExportService.manipulateResultsAndExportToExcel(results, 'Custom sql report');
          }),
        )
        .subscribe(() => {
          this.errorMessage.set(null);
        });
    }
  }
}
