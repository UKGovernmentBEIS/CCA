import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'cca-user-registration',
  template: `<router-outlet ccaSkipLinkFocus></router-outlet>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserRegistrationComponent {}
