import { Component, input, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import {
  ConditionalContentDirective,
  GovukSelectOption,
  LinkDirective,
  RadioComponent,
  RadioOptionComponent,
  SelectComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { operatorTypeOptions } from '@shared/pipes';
import { existingControlContainer } from '@shared/providers';

import { SubsectorAssociationSchemeInfoDTO } from 'cca-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'cca-target-unit-details-input',
  templateUrl: './target-unit-details-input.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    TextInputComponent,
    RadioComponent,
    RadioOptionComponent,
    ConditionalContentDirective,
    LinkDirective,
    RouterLink,
    SelectComponent,
  ],
  viewProviders: [existingControlContainer],
})
export class TargetUnitDetailsInputComponent implements OnInit {
  subSectors = input<SubsectorAssociationSchemeInfoDTO[]>([]);

  subSectorOptions: GovukSelectOption[];

  readonly operatorTypeOptions = operatorTypeOptions;

  ngOnInit(): void {
    this.subSectorOptions = this.subSectors().map((scheme) => ({
      text: scheme.subsectorAssociation.name,
      value: scheme.id,
    }));
  }
}
