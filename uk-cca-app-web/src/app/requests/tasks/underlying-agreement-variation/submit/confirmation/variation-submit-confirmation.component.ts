import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ConfirmationSharedComponent } from '@shared/components';

@Component({
  selector: 'cca-una-variation-submit-confirmation',
  standalone: true,
  imports: [ConfirmationSharedComponent],
  template: `
    <cca-confirmation-shared
      title="Variation application sent to regulator"
      [whatHappensNextTemplate]="whatHappensNextTemplate"
    ></cca-confirmation-shared>

    <ng-template #whatHappensNextTemplate>
      <h3 class="govuk-heading-m">What happens next</h3>

      <p class="govuk-body">The regulator will review your variation application and contact you.</p>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationSubmitConfirmationComponent {}
