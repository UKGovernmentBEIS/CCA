import { Component, OnInit } from '@angular/core';
import { ControlContainer, ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';

import { CountiesDirective } from '@shared/directives/counties.directive';

import { SelectComponent, TextInputComponent } from 'govuk-components';

import { existingControlContainer } from '../providers/control-container.factory';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'cca-county-address-input',
  templateUrl: './county-address-input.component.html',
  standalone: true,
  imports: [TextInputComponent, ReactiveFormsModule, SelectComponent, CountiesDirective],
  viewProviders: [existingControlContainer],
})
export class CountyAddressInputComponent implements OnInit {
  form: UntypedFormGroup;

  constructor(private controlContainer: ControlContainer) {}

  ngOnInit(): void {
    this.form = this.controlContainer.control as UntypedFormGroup;
  }
}
