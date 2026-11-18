import { Component, input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { TextInputComponent } from '@netz/govuk-components';

import { existingControlContainer } from '../../providers/control-container.factory';
import { CountyAddressInputComponent } from '../county-address-input/county-address-input.component';
import { PhoneInputComponent } from '../phone-input/phone-input.component';

@Component({
  selector: 'cca-user-input',
  templateUrl: './user-input.component.html',
  imports: [TextInputComponent, PhoneInputComponent, CountyAddressInputComponent, ReactiveFormsModule],
  viewProviders: [existingControlContainer],
})
export class UserInputComponent {
  protected readonly phoneType = input<'full' | 'national'>(undefined);
  protected readonly isNotification = input(false);
}
