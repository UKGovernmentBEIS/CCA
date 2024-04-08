import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';

import { GovukComponentsModule } from 'govuk-components';

import { NotificationTemplateDTO } from 'cca-api';

@Component({
  selector: 'cca-email-template-details-template',
  templateUrl: './email-template-details-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [GovukComponentsModule, GovukDatePipe, NgIf, NgFor, RouterLink],
})
export class EmailTemplateDetailsTemplateComponent {
  @Input() emailTemplate: NotificationTemplateDTO;

  constructor(private readonly router: Router) {}

  navigateToDocumentTemplate(id: number): void {
    this.router.navigateByUrl(`templates/document/${id}`);
  }
}
