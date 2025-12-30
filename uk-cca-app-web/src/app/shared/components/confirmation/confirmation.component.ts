import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, input, OnInit, TemplateRef } from '@angular/core';
import { RouterLink } from '@angular/router';

import { BreadcrumbService } from '@netz/common/navigation';
import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-confirmation-shared',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel class="pre-line" [title]="title()">
          {{ titleReferenceText() }}
          <div style="font-weight: bold;">{{ titleReferenceId() }}</div>
        </govuk-panel>

        <ng-container
          *ngTemplateOutlet="whatHappensNextTemplate() ? whatHappensNextTemplate() : defaultWhatHappensNextTemplate"
        />

        <ng-template #defaultWhatHappensNextTemplate />
        <a class="govuk-link" [routerLink]="returnToLink()"> Return to dashboard </a>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink, NgTemplateOutlet],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationSharedComponent implements OnInit {
  protected readonly title = input<string>(undefined);
  protected readonly titleReferenceText = input<string>(undefined);
  protected readonly titleReferenceId = input<string>(undefined);
  protected readonly whatHappensNextTemplate = input<TemplateRef<any>>(undefined);
  protected readonly returnToLink = input('/dashboard');

  protected readonly breadcrumbs = inject(BreadcrumbService);

  ngOnInit(): void {
    this.breadcrumbs.show([
      {
        text: 'Dashboard',
        link: ['dashboard'],
      },
    ]);
  }
}
