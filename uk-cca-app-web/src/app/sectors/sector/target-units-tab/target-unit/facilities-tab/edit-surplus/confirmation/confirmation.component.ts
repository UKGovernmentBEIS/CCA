import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

import { FacilityInfoDTO } from 'cca-api';

@Component({
  selector: 'cca-facility-surplus-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Surplus gained updated" />
        <a class="govuk-link" routerLink="../../" [replaceUrl]="true" fragment="details">
          Return to: {{ facilityBusinessId() }}
        </a>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
})
export default class FacilitySurplusConfirmationComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly routeData = toSignal(this.route.data, { initialValue: this.route.snapshot.data });

  protected readonly facilityBusinessId = computed(
    () => (this.routeData().facilityDetails as FacilityInfoDTO).facilityBusinessId,
  );
}
