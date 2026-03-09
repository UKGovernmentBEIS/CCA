import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { MockType } from '@netz/common/testing';
import { BaselineEnergyDraftService, underlyingAgreementQuery } from '@requests/common';

import {
  mockActivatedRouteWithProductParams,
  mockFacilityWithProducts,
  mockRequestTaskId,
  mockRequestTaskPayloadWithProducts,
} from '../testing/mock-data';
import { DeleteProductComponent } from './delete-product.component';

describe('DeleteProductComponent', () => {
  let component: DeleteProductComponent;
  let fixture: ComponentFixture<DeleteProductComponent>;
  let store: RequestTaskStore;
  let draftService: MockType<BaselineEnergyDraftService>;
  let router: Router;

  beforeEach(() => {
    draftService = {
      initializeFromStore: jest.fn(),
      draftSignal: jest.fn().mockReturnValue({
        totalFixedEnergy: '100',
        hasVariableEnergy: true,
        variableEnergyType: 'BY_PRODUCT',
        products: [],
      }) as any,
      saveFormSnapshot: jest.fn(),
      setProducts: jest.fn(),
      removeProduct: jest.fn(),
      clear: jest.fn(),
    };

    TestBed.configureTestingModule({
      imports: [DeleteProductComponent, RouterModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: mockActivatedRouteWithProductParams },
        { provide: BaselineEnergyDraftService, useValue: draftService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);

    jest.spyOn(store, 'select').mockImplementation((selector) => {
      if (selector === requestTaskQuery.selectRequestTaskPayload) {
        return signal(mockRequestTaskPayloadWithProducts);
      }

      if (selector === underlyingAgreementQuery.selectSectionsCompleted) return signal({});
      if (selector === requestTaskQuery.selectRequestTaskId) return signal(mockRequestTaskId);

      if (typeof selector === 'function') {
        return signal(mockFacilityWithProducts);
      }

      return signal({});
    });

    fixture = TestBed.createComponent(DeleteProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the delete confirmation', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should display product name in heading', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Product 1');
  });

  it('should display delete confirmation message', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Are you sure you want to delete');
  });

  it('should display warning about permanent deletion', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain(
      'When you submit the "Baseline energy or carbon consumption" page, your product and all its data will be deleted permanently',
    );
  });

  it('should display warning about irreversible action', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('You will not be able to undo this action');
  });

  it('should display delete product button', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Delete product');
  });

  it('should have product name from route params', () => {
    expect(component['productName']).toBe('Product 1');
  });

  it('should have facility from store', () => {
    expect(component['facility']()).toEqual(mockFacilityWithProducts);
  });

  it('should call draftService.removeProduct when onDelete is called', () => {
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
    component.onDelete();
    expect(draftService.removeProduct).toHaveBeenCalledWith('Product 1');
  });

  it('should navigate after deletion', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    component.onDelete();
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], expect.any(Object));
  });

  it('should display facility name in caption', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Facility 1');
  });
});
