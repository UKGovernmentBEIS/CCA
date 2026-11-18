import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ConfirmationSharedComponent } from '@shared/components';

@Component({
  selector: 'cca-una-submit-confirmation',
  template: `
    <cca-confirmation-shared
      title="Application sent to regulator"
      [whatHappensNextTemplate]="whatHappensNextTemplate"
    />

    <ng-template #whatHappensNextTemplate>
      <h3 class="govuk-heading-m">What happens next</h3>
      <p>The regulator will review your application and contact you.</p>
    </ng-template>
  `,
  imports: [ConfirmationSharedComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementSubmitConfirmationComponent {}
