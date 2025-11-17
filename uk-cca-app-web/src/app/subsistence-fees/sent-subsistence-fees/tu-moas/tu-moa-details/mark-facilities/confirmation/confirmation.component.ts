import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';
import { StatusPipe } from '@shared/pipes';

import { TuMoaDetailsStore } from '../../tu-moa-details.store';

@Component({
  selector: 'cca-confirmation',
  template: `
    @if (state()) {
      <div class="govuk-!-width-two-thirds">
        <govuk-panel>
          <strong>The selected facilities have been marked as {{ type | status }}</strong>
        </govuk-panel>
        <a class="govuk-link" routerLink="../../../" [replaceUrl]="true">
          Return to: Target unit MoA {{ state().moaTUDetails.transactionId }}
        </a>
      </div>
    }
  `,
  imports: [PanelComponent, RouterLink, StatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tuMoaDetailsStore = inject(TuMoaDetailsStore);

  protected readonly type = this.activatedRoute.snapshot.paramMap.get('type');

  protected readonly state = this.tuMoaDetailsStore.stateAsSignal;
}
