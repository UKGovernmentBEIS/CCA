import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

import { GovukSelectOption, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { existingControlContainer } from '@shared/providers';

type ProductFormGroup = FormGroup;

@Component({
  selector: 'cca-add-product-item',
  templateUrl: './add-product-item.component.html',
  imports: [ReactiveFormsModule, TextInputComponent, SelectComponent],
  viewProviders: [existingControlContainer],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddProductItemComponent {
  private readonly decimalPipe = new DecimalPipe('en-GB');

  protected readonly group = input.required<ProductFormGroup>();
  protected readonly index = input.required<number>();
  protected readonly facilityUnit = input.required<string>();
  protected readonly baselineYearOptions = input.required<GovukSelectOption<number>[]>();
  protected readonly canRemove = input<boolean>(false);

  protected readonly remove = output<number>();

  protected energyIntensityDisplay(): string | null {
    const group = this.group();
    const intensity = group.controls['energyIntensity']?.value;
    const throughputUnitControl = group.controls['throughputUnit']?.value;
    const throughputUnit = typeof throughputUnitControl === 'string' ? throughputUnitControl : '';

    if (intensity === null || intensity === undefined || Number.isNaN(Number(intensity))) {
      return null;
    }

    const formattedIntensity = this.decimalPipe.transform(intensity, '1.0-7');
    const unitSuffix = throughputUnit ? `${this.facilityUnit()}/${throughputUnit}` : this.facilityUnit();

    return `${formattedIntensity} ${unitSuffix}`;
  }

  protected onRemove() {
    this.remove.emit(this.index());
  }
}
