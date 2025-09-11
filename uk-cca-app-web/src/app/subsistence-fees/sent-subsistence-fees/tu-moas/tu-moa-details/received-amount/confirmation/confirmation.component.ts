import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

import { SubsistenceFeesMoAViewService } from 'cca-api';

@Component({
  selector: 'cca-confirmation',
  template: `
    @if (details()) {
      <govuk-panel><strong>Received amount updated</strong></govuk-panel>
      <a class="govuk-link" routerLink="../../" [replaceUrl]="true">
        Return to: Target unit MoA {{ details().transactionId }}</a
      >
    }
  `,
  standalone: true,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  private readonly activateDroute = inject(ActivatedRoute);
  private readonly subsistenceFeesMoAViewService = inject(SubsistenceFeesMoAViewService);

  private readonly moaId = +this.activateDroute.snapshot.paramMap.get('moaId');

  protected readonly details = toSignal(
    this.subsistenceFeesMoAViewService.getSubsistenceFeesMoaDetailsById(this.moaId),
  );
}
