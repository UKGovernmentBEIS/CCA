import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'cca-email-confirmed',
  template: `
    <govuk-panel>Email address confirmed</govuk-panel>

    <p class="govuk-body">You can continue to create an Energy Savings Opportunity Scheme reporting sign in.</p>

    <a routerLink="../user/contact-details" govukButton>Continue</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailConfirmedComponent {}
