import { Component, input, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import {
  ConditionalContentDirective,
  GovukSelectOption,
  RadioComponent,
  RadioOptionComponent,
  SelectComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { operatorTypeOptions } from '@shared/pipes';
import { existingControlContainer } from '@shared/providers';

import { SubsectorAssociationSchemeInfoDTO } from 'cca-api';

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
