import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ErrorSummaryComponent, GovukSelectOption, GovukValidators } from '@netz/govuk-components';
import {
  BaselineEnergyDraftService,
  mapToProductVariableEnergyConsumptionData,
  MeasurementTypeToUnitPipe,
  underlyingAgreementQuery,
} from '@requests/common';
import { AddProductItemComponent } from '@requests/common';
import { CCAGovukValidators } from '@shared/validators';

import {
  ADD_PRODUCT_FORM,
  AddProductFormModel,
  AddProductFormProvider,
  createProductFormGroup,
  ProductFormGroup,
} from './add-product-form.provider';

@Component({
  selector: 'cca-add-product',
  templateUrl: './add-product.component.html',
  imports: [
    ReactiveFormsModule,
    AddProductItemComponent,
    MeasurementTypeToUnitPipe,
    ErrorSummaryComponent,
    AddProductItemComponent,
  ],
  providers: [AddProductFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddProductComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly draftService = inject(BaselineEnergyDraftService);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly una = this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreement)();
  private readonly facilityIndex = this.una.facilities?.findIndex((f) => f.facilityId === this.facilityId);

  protected readonly form = inject<AddProductFormModel>(ADD_PRODUCT_FORM);
  protected readonly hasFormErrors = signal(false);

  protected readonly facilityThroughputUnit = this.requestTaskStore.select(
    underlyingAgreementQuery.selectFacilityTargetComposition(this.facilityIndex),
  )()?.measurementType;

  get productsArray(): FormArray<ProductFormGroup> {
    return this.form.controls.products as FormArray<ProductFormGroup>;
  }

  protected readonly baselineYearOptions: GovukSelectOption<number>[] = Array.from({ length: 9 }, (_, index) => {
    const year = 2022 + index;
    return { value: year, text: String(year) };
  });

  ngOnInit() {
    this.updateProductValidationMessages();
  }

  onAddProduct() {
    this.productsArray.push(createProductFormGroup(this.formBuilder, this.destroyRef));
    this.updateProductValidationMessages();
  }

  onRemoveProduct(index: number) {
    if (this.productsArray.length <= 1) return;
    this.productsArray.removeAt(index);
    this.form.markAsDirty();
    this.form.markAsTouched();
    this.updateProductValidationMessages();
  }

  onSubmit() {
    this.updateProductValidationMessages();
    this.form.markAllAsTouched();
    this.form.updateValueAndValidity();

    if (this.form.invalid) {
      this.hasFormErrors.set(true);
      return;
    }

    this.hasFormErrors.set(false);

    const baselineEnergyConsumption = this.requestTaskStore.select(
      underlyingAgreementQuery.selectFacilityBaselineEnergyConsumption(this.facilityIndex),
    )();
    const existingProducts =
      this.draftService.draftSignal()?.products ??
      baselineEnergyConsumption?.variableEnergyConsumptionDataByProduct ??
      [];
    const existingProductsByName = new Map(existingProducts.map((product) => [product.productName, product]));

    // Map form controls to product data
    const updatedProducts = this.productsArray.controls.map((control) => {
      const rawValue = control.getRawValue();
      const previousProduct = existingProductsByName.get(rawValue.productName ?? '');
      return mapToProductVariableEnergyConsumptionData(rawValue, previousProduct);
    });

    // Save to draft service (NO API CALL)
    this.draftService.setProducts(updatedProducts);

    // Navigate back to parent
    this.router.navigate(['..'], { relativeTo: this.activatedRoute });
  }

  private updateProductValidationMessages() {
    this.productsArray.controls.forEach((group, index) => {
      const productIndex = index + 1;

      group.controls.productName.setValidators([
        GovukValidators.required(`Product ${productIndex}: Enter a product name`),
      ]);

      group.controls.baselineYear.setValidators([
        GovukValidators.required(`Product ${productIndex}: Select the baseline year`),
      ]);

      group.controls.baselineVariableEnergy.setValidators([
        GovukValidators.required(`Product ${productIndex}: Enter the baseline variable energy`),
        CCAGovukValidators.maxDecimalsWithMessage(7, `Product ${productIndex}: Enter a number up to 7 decimal places`),
      ]);

      group.controls.baselineThroughput.setValidators([
        GovukValidators.required(`Product ${productIndex}: Enter the baseline throughput`),
        CCAGovukValidators.maxDecimalsWithMessage(7, `Product ${productIndex}: Enter a number up to 7 decimal places`),
        GovukValidators.positiveNumber(`Product ${productIndex}: Enter a number greater than zero`),
      ]);

      group.controls.throughputUnit.setValidators([
        GovukValidators.required(`Product ${productIndex}: Enter the throughput unit`),
      ]);

      this.form.updateValueAndValidity();
    });
  }
}
