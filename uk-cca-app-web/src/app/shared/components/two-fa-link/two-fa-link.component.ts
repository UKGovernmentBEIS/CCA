import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { LinkDirective } from '@netz/govuk-components';

@Component({
  selector: 'cca-two-fa-link',
  template: `
    <div class="govuk-button-group">
      <a
        govukLink
        [routerLink]="link()"
        [relativeTo]="activatedRoute"
        [state]="{
          userId: userId(),
          accountId: accountId(),
          userName: userName(),
          role: role(),
          sectorAssociationId: sectorAssociationId(),
        }"
      >
        {{ title() }}</a
      >
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [LinkDirective, RouterLink],
})
export class TwoFaLinkComponent {
  protected readonly activatedRoute = inject(ActivatedRoute);

  title = input<string>();
  link = input<string>();
  userId = input<string>();
  accountId = input<number>();
  userName = input<string>();
  role = input<string>();
  sectorAssociationId = input<string>();
}
