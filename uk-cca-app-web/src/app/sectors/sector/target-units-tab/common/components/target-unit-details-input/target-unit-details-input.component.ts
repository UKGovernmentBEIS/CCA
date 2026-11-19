import {
  afterNextRender,
  Component,
  ElementRef,
  inject,
  Injector,
  input,
  OnInit,
  viewChild,
  viewChildren,
} from '@angular/core';
import {
  ControlContainer,
  FormArray,
  FormGroup,
  ReactiveFormsModule,
  UntypedFormArray,
  UntypedFormGroup,
} from '@angular/forms';

import {
  ButtonDirective,
  GovukSelectOption,
  RadioComponent,
  RadioOptionComponent,
  SelectComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { operatorTypeOptions } from '@shared/pipes';
import { existingControlContainer } from '@shared/providers';

import { SubsectorAssociationInfoDTO } from 'cca-api';

import { addSicCodeFormControl } from '../../utils';

@Component({
  selector: 'cca-target-unit-details-input',
  templateUrl: './target-unit-details-input.component.html',
  viewProviders: [existingControlContainer],
  imports: [
    ReactiveFormsModule,
    TextInputComponent,
    RadioComponent,
    RadioOptionComponent,
    SelectComponent,
    ButtonDirective,
  ],
})
export class TargetUnitDetailsInputComponent implements OnInit {
  private readonly controlContainer = inject(ControlContainer);
  private readonly injector = inject(Injector);

  private readonly sicCodeRows = viewChildren('sicCodeRow', { read: ElementRef });
  private readonly addCodeButton = viewChild('addCodeButton', { read: ElementRef });

  protected readonly subSectors = input<SubsectorAssociationInfoDTO[]>([]);
  protected subSectorOptions: GovukSelectOption[];
  protected form: FormGroup;
  protected sicCodeFormArray: FormArray;

  readonly operatorTypeOptions = operatorTypeOptions;

  ngOnInit() {
    this.form = this.controlContainer.control as UntypedFormGroup;
    this.sicCodeFormArray = this.form.get('sicCodes') as UntypedFormArray;

    this.subSectorOptions = this.subSectors().map((subsector) => ({
      text: subsector.name,
      value: subsector.id,
    }));
  }

  addCode() {
    this.sicCodeFormArray.push(addSicCodeFormControl());
    this.sicCodeFormArray.at(this.sicCodeFormArray.length - 1);
    this.sicCodeFormArray.markAsDirty();
  }

  removeCode(index: number) {
    this.sicCodeFormArray.removeAt(index);

    afterNextRender(
      {
        write: () => {
          const nextRowInput = this.sicCodeRows()[index]?.nativeElement.querySelector('input');
          (nextRowInput ?? this.addCodeButton()?.nativeElement)?.focus();
        },
      },
      { injector: this.injector },
    );
  }
}
