import { Component, Input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { TextInputComponent } from '@netz/govuk-components';
import { CountyAddressInputComponent } from '@shared/components';
import { PhoneInputComponent } from '@shared/components';

import { existingControlContainer } from '../../providers/control-container.factory';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'cca-user-input',
  templateUrl: './user-input.component.html',
  viewProviders: [existingControlContainer],
  standalone: true,
  imports: [TextInputComponent, PhoneInputComponent, CountyAddressInputComponent, ReactiveFormsModule],
})
export class UserInputComponent {
  @Input() phoneType: 'full' | 'national';
  @Input() isNotification = false;
}
