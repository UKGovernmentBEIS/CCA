import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { CountiesDirective } from '@shared/directives/counties.directive';

import { existingControlContainer } from '../../providers/control-container.factory';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'cca-county-address-input',
  templateUrl: './county-address-input.component.html',
  standalone: true,
  imports: [TextInputComponent, ReactiveFormsModule, SelectComponent, CountiesDirective],
  viewProviders: [existingControlContainer],
})
export class CountyAddressInputComponent {}
