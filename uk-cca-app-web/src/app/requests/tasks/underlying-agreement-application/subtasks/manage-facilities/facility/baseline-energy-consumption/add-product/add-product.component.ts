import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ErrorSummaryComponent, GovukSelectOption, GovukValidators } from '@netz/govuk-components';
import {
  mapToProductVariableEnergyConsumptionData,
  MeasurementTypeToUnitPipe,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { CCAGovukValidators } from '@shared/validators';
import { produce } from 'immer';

import { UnderlyingAgreementApplySavePayload, UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../../../transform';
import {
  ADD_PRODUCT_FORM,
  AddProductFormModel,
  AddProductFormProvider,
  createProductFormGroup,
  ProductFormGroup,
} from './add-product-form.provider';
import { AddProductItemComponent } from './add-product-item/add-product-item.component';

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
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

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

    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);
    const updatedPayload = updateVariableEnergyProducts(actionPayload, this.form, this.facilityId);

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[this.facilityId] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['..'], { relativeTo: this.activatedRoute });
    });
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

function updateVariableEnergyProducts(
  payload: UnderlyingAgreementApplySavePayload,
  form: AddProductFormModel,
  facilityId: string,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((facility) => facility.facilityId === facilityId);
    if (facilityIndex === -1) return;

    const baselineEnergyConsumption = draft.facilities[facilityIndex].cca3BaselineAndTargets
      ?.facilityBaselineEnergyConsumption ?? { variableEnergyConsumptionDataByProduct: [] };

    const existingProducts = baselineEnergyConsumption?.variableEnergyConsumptionDataByProduct ?? [];
    const productControls = form.controls.products.controls;

    const updatedProducts = productControls.map((control, index) => {
      const { productName, baselineYear, baselineVariableEnergy, baselineThroughput, throughputUnit } =
        control.getRawValue();

      return mapToProductVariableEnergyConsumptionData(
        {
          productName,
          baselineYear,
          baselineVariableEnergy,
          baselineThroughput,
          throughputUnit,
        },
        existingProducts[index],
      );
    });

    baselineEnergyConsumption.variableEnergyConsumptionDataByProduct = updatedProducts;
    draft.facilities[facilityIndex].cca3BaselineAndTargets.facilityBaselineEnergyConsumption =
      baselineEnergyConsumption as any;
  });
}
