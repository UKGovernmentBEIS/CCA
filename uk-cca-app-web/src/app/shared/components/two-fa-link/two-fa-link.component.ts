import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'cca-two-fa-link',
  template: `
    <div class="govuk-button-group">
      <a
        class="govuk-link"
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
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TwoFaLinkComponent {
  protected readonly activatedRoute = inject(ActivatedRoute);

  protected readonly title = input<string>();
  protected readonly link = input<string>();
  protected readonly userId = input<string>();
  protected readonly accountId = input<number>();
  protected readonly userName = input<string>();
  protected readonly role = input<string>();
  protected readonly sectorAssociationId = input<string>();
}
