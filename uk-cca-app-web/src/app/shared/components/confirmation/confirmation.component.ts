import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnInit, TemplateRef } from '@angular/core';
import { RouterLink } from '@angular/router';

import { BreadcrumbService } from '@netz/common/navigation';
import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-confirmation-shared',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel class="pre-wrap" [title]="title">
          {{ titleReferenceText }}
          <div style="font-weight: bold;">{{ titleReferenceId }}</div>
        </govuk-panel>

        <ng-container
          *ngTemplateOutlet="whatHappensNextTemplate ? whatHappensNextTemplate : defaultWhatHappensNextTemplate"
        >
        </ng-container>

        <ng-template #defaultWhatHappensNextTemplate></ng-template>
        <a class="govuk-link" [routerLink]="returnToLink"> Return to dashboard </a>
      </div>
    </div>
  `,
  standalone: true,
  imports: [PanelComponent, RouterLink, NgTemplateOutlet],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationSharedComponent implements OnInit {
  @Input() title: string;
  @Input() titleReferenceText: string;
  @Input() titleReferenceId: string;
  @Input() whatHappensNextTemplate: TemplateRef<any>;
  @Input() returnToLink = '/dashboard';

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
