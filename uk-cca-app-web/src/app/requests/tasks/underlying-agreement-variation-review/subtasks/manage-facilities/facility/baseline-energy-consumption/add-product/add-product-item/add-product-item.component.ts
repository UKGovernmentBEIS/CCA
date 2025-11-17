import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { GovukSelectOption, SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { existingControlContainer } from '@shared/providers';

import { ProductFormGroup } from '../add-product-form.provider';

@Component({
  selector: 'cca-add-product-item',
  templateUrl: './add-product-item.component.html',
  imports: [ReactiveFormsModule, TextInputComponent, SelectComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  viewProviders: [existingControlContainer],
})
export class AddProductItemComponent {
  private readonly decimalPipe = new DecimalPipe('en-GB');
  protected readonly group = input.required<ProductFormGroup>();
  protected readonly index = input.required<number>();
  protected readonly facilityUnit = input.required<string>();
  protected readonly baselineYearOptions = input.required<GovukSelectOption<number>[]>();
  protected readonly canRemove = input<boolean>(false);

  readonly remove = output<number>();

  protected energyIntensityDisplay(): string | null {
    const group = this.group();
    const intensity = group.controls.energyIntensity.value;

    if (intensity === null || intensity === undefined || Number.isNaN(Number(intensity))) {
      return null;
    }

    const formattedIntensity = this.decimalPipe.transform(intensity, '1.0-7');
    const throughputUnit = group.controls.throughputUnit.value ?? '';
    const unitSuffix = throughputUnit ? `${this.facilityUnit()}/${throughputUnit}` : `${this.facilityUnit()}`;

    return `${formattedIntensity} ${unitSuffix}`;
  }

  protected onRemove() {
    this.remove.emit(this.index());
  }
}
