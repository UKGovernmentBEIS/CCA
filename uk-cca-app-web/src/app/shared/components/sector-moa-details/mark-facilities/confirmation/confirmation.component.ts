import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';
import { StatusPipe } from '@shared/pipes';

import { SectorMoaDetailsStore } from '../../sector-moa-details.store';

@Component({
  selector: 'cca-confirmation',
  template: `
    @if (state()) {
      <div class="govuk-!-width-two-thirds">
        <govuk-panel>
          <strong>The selected facilities have been marked as {{ type | status }}</strong>
        </govuk-panel>
        <a class="govuk-link" routerLink="../../../" [replaceUrl]="true">
          Return to: Sector MoA {{ state().sectorMoaDetails?.transactionId }}
        </a>
      </div>
    }
  `,
  standalone: true,
  imports: [PanelComponent, RouterLink, StatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly sectorMoaDetailsStore = inject(SectorMoaDetailsStore);

  protected readonly type = this.activatedRoute.snapshot.paramMap.get('type');

  protected readonly state = this.sectorMoaDetailsStore.stateAsSignal;
}
