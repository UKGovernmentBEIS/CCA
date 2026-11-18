import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { ButtonDirective, ErrorSummaryComponent } from '@netz/govuk-components';

import { MiReportsListComponent } from './mi-reports-list/mi-reports-list.component';

@Component({
  selector: 'cca-mi-reports',
  template: `
    @if (isErrorSummaryDisplayed()) {
      <govuk-error-summary [form]="form" />
    }

    <div class="govuk-grid-row">
      <div class="govuk-grid-column-one-half">
        <netz-page-heading size="xl">MI Reports</netz-page-heading>
      </div>

      <div class="govuk-grid-column-one-quarter" style="text-align: end">
        <a routerLink="custom" govukButton class="govuk-button--secondary" type="button">Custom report</a>
      </div>
      <div class="govuk-grid-column-one-quarter">
        <a routerLink="create-mi-report" govukButton type="button">Create new report</a>
      </div>
    </div>

    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <cca-mi-reports-list (exportError)="onExportError($event)" />
      </div>
    </div>
  `,
  imports: [PageHeadingComponent, RouterLink, ButtonDirective, MiReportsListComponent, ErrorSummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MiReportsComponent {
  private readonly fb = inject(FormBuilder);

  protected readonly form: FormGroup<{ exportError: FormControl<string> }> = this.fb.group({
    exportError: this.fb.control<string>(null),
  });
  protected readonly isErrorSummaryDisplayed = signal(false);

  onExportError(errorMessage: string | null) {
    if (errorMessage) {
      this.form.controls.exportError.setErrors({ apiError: errorMessage });
      this.isErrorSummaryDisplayed.set(true);
    } else {
      this.form.controls.exportError.setErrors(null);
      this.isErrorSummaryDisplayed.set(false);
    }
  }
}
