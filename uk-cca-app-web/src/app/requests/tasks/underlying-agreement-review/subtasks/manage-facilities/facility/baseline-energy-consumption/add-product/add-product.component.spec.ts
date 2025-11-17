import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { DestroyRef, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { of } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { MockType } from '@netz/common/testing';
import { TasksApiService, underlyingAgreementQuery, underlyingAgreementReviewQuery } from '@requests/common';

import {
  mockActivatedRoute,
  mockProductVariableEnergyData,
  mockRequestTaskId,
  mockRequestTaskPayloadWithProducts,
  mockUnderlyingAgreementWithProducts,
} from '../testing/mock-data';
import { AddProductComponent } from './add-product.component';
import { ADD_PRODUCT_FORM, createProductFormGroup } from './add-product-form.provider';

describe('AddProductComponent', () => {
  let component: AddProductComponent;
  let fixture: ComponentFixture<AddProductComponent>;
  let store: RequestTaskStore;
  let tasksApiService: MockType<TasksApiService>;

  beforeEach(() => {
    tasksApiService = {
      saveRequestTaskAction: jest.fn().mockReturnValue(of(mockRequestTaskPayloadWithProducts)),
    };

    const destroyRef = { onDestroy: jest.fn() } as unknown as DestroyRef;
    const formBuilder = new FormBuilder();

    const productFormGroup = createProductFormGroup(formBuilder, destroyRef, mockProductVariableEnergyData);

    const testForm = formBuilder.group({
      products: formBuilder.array([productFormGroup]),
    });

    TestBed.configureTestingModule({
      imports: [AddProductComponent, ReactiveFormsModule, RouterModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: DestroyRef, useValue: destroyRef },
        { provide: ADD_PRODUCT_FORM, useValue: testForm },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

    jest.spyOn(store, 'select').mockImplementation((selector) => {
      if (selector === requestTaskQuery.selectRequestTaskPayload) {
        return signal(mockRequestTaskPayloadWithProducts);
      }

      if (selector === underlyingAgreementQuery.selectSectionsCompleted) return signal({});
      if (selector === requestTaskQuery.selectRequestTaskId) return signal(mockRequestTaskId);
      if (selector === underlyingAgreementQuery.selectUnderlyingAgreement) {
        return signal(mockUnderlyingAgreementWithProducts);
      }

      if (selector === underlyingAgreementReviewQuery.selectReviewSectionsCompleted) {
        return signal({});
      }

      if (selector === underlyingAgreementReviewQuery.selectDetermination) {
        return signal({ type: null });
      }

      if (typeof selector === 'function') {
        return signal({
          measurementType: 'ENERGY_KWH',
        });
      }

      return signal({});
    });

    fixture = TestBed.createComponent(AddProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the form with the correct initial values', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should have products array with one product', () => {
    expect(component.productsArray.length).toBe(1);
  });

  it('should have product name control in first product', () => {
    const firstProduct = component.productsArray.at(0);
    expect(firstProduct.get('productName')).toBeDefined();
  });

  it('should have baseline year control in first product', () => {
    const firstProduct = component.productsArray.at(0);
    expect(firstProduct.get('baselineYear')).toBeDefined();
  });

  it('should have baseline variable energy control in first product', () => {
    const firstProduct = component.productsArray.at(0);
    expect(firstProduct.get('baselineVariableEnergy')).toBeDefined();
  });

  it('should have baseline throughput control in first product', () => {
    const firstProduct = component.productsArray.at(0);
    expect(firstProduct.get('baselineThroughput')).toBeDefined();
  });

  it('should have throughput unit control in first product', () => {
    const firstProduct = component.productsArray.at(0);
    expect(firstProduct.get('throughputUnit')).toBeDefined();
  });

  it('should add a new product when addProduct is called', () => {
    const initialLength = component.productsArray.length;
    component.onAddProduct();
    expect(component.productsArray.length).toBe(initialLength + 1);
  });

  it('should remove a product when removeProduct is called with valid index', () => {
    component.onAddProduct();
    const initialLength = component.productsArray.length;
    component.onRemoveProduct(1);
    expect(component.productsArray.length).toBe(initialLength - 1);
  });

  it('should not remove product if only one product exists', () => {
    const initialLength = component.productsArray.length;
    component.onRemoveProduct(0);
    expect(component.productsArray.length).toBe(initialLength);
  });

  it('should have baseline year options', () => {
    expect(component['baselineYearOptions'].length).toBeGreaterThan(0);
  });

  it('should have facility throughput unit', () => {
    expect(component['facilityThroughputUnit']).toBe('ENERGY_KWH');
  });

  it('should call onSubmit when form is submitted', () => {
    const onSubmitSpy = jest.spyOn(component, 'onSubmit');
    component.onSubmit();
    expect(onSubmitSpy).toHaveBeenCalled();
  });

  it('should set hasFormErrors to true when form is invalid on submit', () => {
    component.productsArray.at(0).get('productName')?.setValue('');
    component.onSubmit();
    expect(component['hasFormErrors']()).toBe(true);
  });

  it('should display add product button', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Add product');
  });

  it('should display save and continue button', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Save and continue');
  });
});
