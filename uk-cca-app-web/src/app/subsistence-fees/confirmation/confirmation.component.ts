import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-confirmation',
  templateUrl: './confirmation.component.html',
  imports: [RouterLink, PanelComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly referenceCode = this.activatedRoute.snapshot.queryParamMap.get('referenceCode');
}
