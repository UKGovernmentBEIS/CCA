import { Component, Input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { TextInputComponent } from '@netz/govuk-components';

import { existingControlContainer } from '../../providers/control-container.factory';
import { CountyAddressInputComponent } from '../county-address-input/county-address-input.component';
import { PhoneInputComponent } from '../phone-input/phone-input.component';

@Component({
  selector: 'cca-user-input',
  templateUrl: './user-input.component.html',
  standalone: true,
  imports: [TextInputComponent, PhoneInputComponent, CountyAddressInputComponent, ReactiveFormsModule],
  viewProviders: [existingControlContainer],
})
export class UserInputComponent {
  @Input() phoneType: 'full' | 'national';
  @Input() isNotification = false;
}
