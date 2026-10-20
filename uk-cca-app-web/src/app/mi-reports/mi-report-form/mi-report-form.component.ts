import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { EMPTY } from 'rxjs';

import { catchBadRequest, catchErrorCode, ErrorCodes } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, ErrorSummaryComponent, TextareaComponent } from '@netz/govuk-components';
import { TextInputComponent } from '@shared/components';

import { CustomMiReportQuery, MiReportsUserDefinedService, MiReportUserDefinedDTO } from 'cca-api';

import { ExtendedMiReportResult } from '../core/mi-interfaces';
import { MiReportsExportService } from '../core/mi-reports-export.service';
import { MI_REPORT_FORM, MiReportFormModel, MiReportFormProvider } from './mi-report-form.provider';

@Component({
  selector: 'cca-mi-report-form',
  templateUrl: './mi-report-form.component.html',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    PageHeadingComponent,
    ButtonDirective,
    TextareaComponent,
    ErrorSummaryComponent,
    TextInputComponent,
    PendingButtonDirective,
  ],
  providers: [MiReportFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MiReportFormComponent {
  private readonly miReportsUserDefinedService = inject(MiReportsUserDefinedService);
  private readonly miReportsExportService = inject(MiReportsExportService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly form = inject<MiReportFormModel>(MI_REPORT_FORM);
  protected readonly isErrorSummaryDisplayed = signal(false);

  protected readonly isEditMode = !!this.route.snapshot.data['query'];
  protected readonly queryId = this.route.snapshot.paramMap.get('queryId');

  exportToExcel() {
    if (this.form.controls.queryDefinition.invalid) {
      this.isErrorSummaryDisplayed.set(true);
      return;
    }

    this.miReportsUserDefinedService
      .generateCustomReport({ sqlQuery: this.form.controls.queryDefinition.value } as CustomMiReportQuery)
      .pipe(
        catchBadRequest(ErrorCodes.REPORT1001, () => {
          this.form.controls.queryDefinition.setErrors({ apiError: 'Unable to execute query' });
          this.isErrorSummaryDisplayed.set(true);
          return EMPTY;
        }),
      )
      .subscribe((results: ExtendedMiReportResult) => {
        this.form.controls.queryDefinition.setErrors(null);
        this.isErrorSummaryDisplayed.set(false);
        this.miReportsExportService.exportToExcel(results, 'Preview query report');
      });
  }

  onSubmit() {
    if (this.form.invalid) {
      this.isErrorSummaryDisplayed.set(true);
      return;
    }

    const dto: MiReportUserDefinedDTO = {
      reportName: this.form.value.reportName,
      description: this.form.value.description,
      queryDefinition: this.form.value.queryDefinition,
    };

    const request$ = this.isEditMode
      ? this.miReportsUserDefinedService.updateMiReportUserDefined(+this.queryId, dto)
      : this.miReportsUserDefinedService.createMiReportUserDefined(dto);

    request$
      .pipe(
        catchErrorCode(ErrorCodes.MIREPORT1001, (err) => {
          this.form.controls.reportName.setErrors({ apiError: err.error.message });
          this.isErrorSummaryDisplayed.set(true);
          return EMPTY;
        }),
      )
      .subscribe(() => this.router.navigate([this.isEditMode ? '../..' : '..'], { relativeTo: this.route }));
  }
}
