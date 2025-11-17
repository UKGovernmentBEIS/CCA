import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

import { createProductFormGroup } from '../add-product-form.provider';
import { AddProductItemComponent } from './add-product-item.component';

@Component({
  selector: 'cca-test-wrapper',
  template: `
    <form [formGroup]="form">
      <ng-container formArrayName="products">
        <div [formGroupName]="0">
          <cca-add-product-item
            [group]="productsArray.at(0)"
            [index]="0"
            [facilityUnit]="'kWh'"
            [baselineYearOptions]="baselineYearOptions"
            [canRemove]="true"
            (remove)="onRemove($event)"
          />
        </div>
      </ng-container>
    </form>
  `,
  imports: [ReactiveFormsModule, AddProductItemComponent],
})
class TestWrapperComponent {
  formBuilder = new FormBuilder();
  destroyRef = { onDestroy: jest.fn() } as any;

  productFormGroup = createProductFormGroup(this.formBuilder, this.destroyRef, {
    productName: 'Product 1',
    baselineYear: 2022,
    energy: '100',
    throughput: '50',
    throughputUnit: 'tonnes',
  });

  form = this.formBuilder.group({
    products: this.formBuilder.array([this.productFormGroup]),
  });

  get productsArray() {
    return this.form.controls.products;
  }

  baselineYearOptions = [
    { value: 2022, text: '2022' },
    { value: 2023, text: '2023' },
  ];

  removedIndex: number | null = null;

  onRemove(index: number) {
    this.removedIndex = index;
  }
}

describe('AddProductItemComponent', () => {
  let component: TestWrapperComponent;
  let fixture: ComponentFixture<TestWrapperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestWrapperComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display product number in heading', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Product 1');
  });

  it('should display product name label', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Product name');
  });

  it('should display baseline year label', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Baseline year');
  });

  it('should display baseline variable energy label', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Baseline variable energy');
  });

  it('should display baseline year throughput label', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Baseline year throughput');
  });

  it('should display throughput unit label', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Throughput unit');
  });

  it('should display energy intensity label', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Energy intensity');
  });

  it('should display automatically calculated hint', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Automatically calculated');
  });

  it('should display remove product button when canRemove is true', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Remove product');
  });

  it('should have product name input with correct value', () => {
    const productGroup = component.productsArray.at(0);
    expect(productGroup.get('productName')?.value).toBe('Product 1');
  });

  it('should have baseline year with correct value', () => {
    const productGroup = component.productsArray.at(0);
    expect(productGroup.get('baselineYear')?.value).toBe(2022);
  });

  it('should have baseline variable energy with correct value', () => {
    const productGroup = component.productsArray.at(0);
    expect(productGroup.get('baselineVariableEnergy')?.value).toBe('100');
  });

  it('should have baseline throughput with correct value', () => {
    const productGroup = component.productsArray.at(0);
    expect(productGroup.get('baselineThroughput')?.value).toBe('50');
  });

  it('should have throughput unit with correct value', () => {
    const productGroup = component.productsArray.at(0);
    expect(productGroup.get('throughputUnit')?.value).toBe('tonnes');
  });

  it('should calculate energy intensity correctly', () => {
    const productGroup = component.productsArray.at(0);
    const energyIntensity = productGroup.get('energyIntensity')?.value;
    expect(energyIntensity).toBe(2); // 100 / 50
  });

  it('should emit remove event when remove button is clicked', () => {
    const removeButton = fixture.nativeElement.querySelector('.govuk-button--secondary');
    expect(removeButton).toBeTruthy();

    removeButton?.click();
    fixture.detectChanges();

    expect(component.removedIndex).toBe(0);
  });

  it('should update energy intensity when energy value changes', () => {
    const productGroup = component.productsArray.at(0);
    productGroup.get('baselineVariableEnergy')?.setValue('200');
    fixture.detectChanges();

    const energyIntensity = productGroup.get('energyIntensity')?.value;
    expect(energyIntensity).toBe(4); // 200 / 50
  });

  it('should update energy intensity when throughput value changes', () => {
    const productGroup = component.productsArray.at(0);
    productGroup.get('baselineThroughput')?.setValue('25');
    fixture.detectChanges();

    const energyIntensity = productGroup.get('energyIntensity')?.value;
    expect(energyIntensity).toBe(4); // 100 / 25
  });

  it('should set energy intensity to null when throughput is zero', () => {
    const productGroup = component.productsArray.at(0);
    productGroup.get('baselineThroughput')?.setValue('0');
    fixture.detectChanges();

    const energyIntensity = productGroup.get('energyIntensity')?.value;
    expect(energyIntensity).toBeNull();
  });
});
