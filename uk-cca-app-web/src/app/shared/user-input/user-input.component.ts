import { Component, Input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { CountyAddressInputComponent } from '@shared/county-address-input/county-address-input.component';
import { PhoneInputComponent } from '@shared/phone-input/phone-input.component';

import { TextInputComponent } from 'govuk-components';

import { existingControlContainer } from '../providers/control-container.factory';

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
