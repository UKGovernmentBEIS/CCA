import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { EMPTY, map } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, ErrorSummaryComponent, GovukValidators, TextareaComponent } from '@netz/govuk-components';

import { CustomMiReportQuery, MiReportsUserDefinedService } from 'cca-api';

import { ExtendedMiReportResult } from '../core/mi-interfaces';
import { MiReportsExportService } from '../core/mi-reports-export.service';

@Component({
  selector: 'cca-custom',
  templateUrl: './custom.component.html',
  imports: [
    PageHeadingComponent,
    ReactiveFormsModule,
    TextareaComponent,
    ButtonDirective,
    PendingButtonDirective,
    ErrorSummaryComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CustomReportComponent {
  private readonly fb = inject(FormBuilder);
  private readonly miReportsUserDefinedService = inject(MiReportsUserDefinedService);
  private readonly miReportsExportService = inject(MiReportsExportService);

  protected readonly form: FormGroup<{ query: FormControl<string> }> = this.fb.group({
    query: this.fb.control<string>(null, [GovukValidators.required('Query must not be empty')]),
  });
  protected readonly isErrorSummaryDisplayed = signal(false);

  exportToExcel() {
    if (this.form.invalid) {
      this.isErrorSummaryDisplayed.set(true);
      return;
    }

    this.miReportsUserDefinedService
      .generateCustomReport({
        sqlQuery: this.form.controls.query.value,
      } as CustomMiReportQuery)
      .pipe(
        catchBadRequest(ErrorCodes.REPORT1001, () => {
          this.form.controls.query.setErrors({ apiError: 'Unable to execute query' });
          this.isErrorSummaryDisplayed.set(true);
          return EMPTY;
        }),
        map((results: ExtendedMiReportResult) => {
          this.miReportsExportService.exportToExcel(results, 'Custom sql report');
        }),
      )
      .subscribe(() => {
        this.form.controls.query.setErrors(null);
        this.isErrorSummaryDisplayed.set(false);
      });
  }
}
