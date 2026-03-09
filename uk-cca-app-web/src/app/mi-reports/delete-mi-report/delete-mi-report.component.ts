import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { WarningTextComponent } from '@netz/govuk-components';

import { MiReportsUserDefinedService, MiReportUserDefinedDTO } from 'cca-api';

@Component({
  selector: 'cca-delete-mi-report',
  template: `
    <div>
      <netz-page-heading caption="Delete MI Report">
        Are you sure you want to delete the {{ query.reportName }} MI Report?
      </netz-page-heading>
      <govuk-warning-text assistiveText="">You will not be able to undo this action.</govuk-warning-text>
      <p class="govuk-body">Your MI Report and all its data will be deleted permanently.</p>
      <button (click)="onDelete()" class="govuk-button govuk-button--warning">Yes, delete the MI Report</button>
    </div>
    <a routerLink="../.." class="govuk-link">Return to: MI Reports</a>
  `,
  imports: [WarningTextComponent, PageHeadingComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteMiReportComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly miReportsUserDefinedService = inject(MiReportsUserDefinedService);

  protected readonly query = this.activatedRoute.snapshot.data['query'] as MiReportUserDefinedDTO;
  private readonly queryId = +this.activatedRoute.snapshot.paramMap.get('queryId');

  onDelete() {
    if (!this.queryId) return;
    this.miReportsUserDefinedService.deleteMiReportUserDefined(this.queryId).subscribe(() => {
      this.router.navigate(['../..'], {
        relativeTo: this.activatedRoute,
      });
    });
  }
}
