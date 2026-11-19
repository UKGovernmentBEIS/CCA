import { Service, signal } from '@angular/core';

import { produce } from 'immer';

import { FacilityBaselineEnergyConsumption, ProductVariableEnergyConsumptionData } from 'cca-api';

export interface BaselineEnergyDraft {
  totalFixedEnergy: string | null;
  hasVariableEnergy: boolean | null;
  variableEnergyType: 'TOTALS' | 'BY_PRODUCT' | null;
  products: ProductVariableEnergyConsumptionData[];
}

@Service({ autoProvided: false })
export class BaselineEnergyDraftService {
  private readonly _draft = signal<BaselineEnergyDraft | null>(null);

  get draftSignal() {
    return this._draft.asReadonly();
  }

  /**
   * Initialize draft from store data. This is idempotent - subsequent calls are no-ops.
   * Called by `initializeBaselineEnergyDraftGuard` (after a `clear`) when the routes activate; the form
   * provider's call is then a no-op.
   */
  initializeFromStore(storeData: FacilityBaselineEnergyConsumption | null | undefined): void {
    if (this.draftSignal() !== null) return;

    this._draft.set({
      totalFixedEnergy: storeData?.totalFixedEnergy,
      hasVariableEnergy: storeData?.hasVariableEnergy,
      variableEnergyType: storeData?.variableEnergyType,
      products: storeData?.variableEnergyConsumptionDataByProduct,
    });
  }

  /**
   * Update totalFixedEnergy in real-time as user types.
   */
  updateTotalFixedEnergy(value: string | null): void {
    const current = this.draftSignal();

    const updated = produce(current, (draft) => {
      draft.totalFixedEnergy = value;
    });

    this._draft.set(updated);
  }

  /**
   * Save a form snapshot before navigating to child routes.
   * This preserves form values that need to survive navigation.
   */
  saveFormSnapshot(formValue: Partial<BaselineEnergyDraft>): void {
    const current = this.draftSignal();

    const updated = produce(current, (draft) => {
      return { ...draft, ...formValue };
    });

    this._draft.set(updated);
  }

  /**
   * Replace the products array. Used by add-product component.
   */
  setProducts(products: ProductVariableEnergyConsumptionData[]): void {
    const current = this.draftSignal();

    const updated = produce(current, (draft) => {
      draft.products = products;
    });

    this._draft.set(updated);
  }

  /**
   * Remove a product by name. Used by delete-product component.
   */
  removeProduct(productName: string): void {
    const current = this.draftSignal();

    const updated = produce(current, (draft) => {
      const index = draft.products?.findIndex((p) => p.productName === productName) ?? -1;
      if (index !== -1) {
        draft.products.splice(index, 1);
      }
    });

    this._draft.set(updated);
  }

  /**
   * Mark a product as excluded. Used by exclude-product component.
   * The product remains in the array but with status 'EXCLUDED'.
   */
  excludeProduct(productName: string): void {
    const current = this.draftSignal();

    const updated = produce(current, (draft) => {
      const product = draft.products?.find((p) => p.productName === productName);
      if (product) {
        product.productStatus = 'EXCLUDED';
      }
    });

    this._draft.set(updated);
  }

  /**
   * Undo product exclusion, setting status back to 'LIVE'.
   * Used by undo-product component.
   */
  undoExcludeProduct(productName: string): void {
    const current = this.draftSignal();

    const updated = produce(current, (draft) => {
      const product = draft.products?.find((p) => p.productName === productName);
      if (product) {
        product.productStatus = 'LIVE';
      }
    });

    this._draft.set(updated);
  }

  /**
   * Clear the draft state. Called after a successful save, and by `initializeBaselineEnergyDraftGuard` before
   * re-seeding.
   */
  clear(): void {
    this._draft.set(null);
  }
}
