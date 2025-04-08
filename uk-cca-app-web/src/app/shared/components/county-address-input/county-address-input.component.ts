import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { TextInputComponent } from '@netz/govuk-components';

import { existingControlContainer } from '../../providers/control-container.factory';
@Component({
  selector: 'cca-county-address-input',
  templateUrl: './county-address-input.component.html',
  standalone: true,
  imports: [TextInputComponent, ReactiveFormsModule],
  viewProviders: [existingControlContainer],
})
export class CountyAddressInputComponent {}
