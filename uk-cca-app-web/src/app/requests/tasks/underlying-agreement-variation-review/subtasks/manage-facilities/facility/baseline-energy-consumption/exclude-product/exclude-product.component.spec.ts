import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { MockType } from '@netz/common/testing';
import { BaselineEnergyDraftService, underlyingAgreementQuery, underlyingAgreementReviewQuery } from '@requests/common';

import { ProductVariableEnergyConsumptionData } from 'cca-api';

import {
  mockActivatedRouteWithProductParams,
  mockFacility,
  mockRequestTaskId,
  mockRequestTaskPayload,
  mockUnderlyingAgreement,
} from '../testing/mock-data';
import { ExcludeProductComponent } from './exclude-product.component';

describe('ExcludeProductComponent', () => {
  let component: ExcludeProductComponent;
  let fixture: ComponentFixture<ExcludeProductComponent>;
  let store: RequestTaskStore;
  let draftService: MockType<BaselineEnergyDraftService>;
  let router: Router;

  beforeEach(() => {
    draftService = {
      initializeFromStore: vi.fn(),
      draftSignal: signal({
        totalFixedEnergy: '100',
        hasVariableEnergy: true,
        variableEnergyType: 'BY_PRODUCT' as const,
        products: [] as ProductVariableEnergyConsumptionData[],
      }),
      saveFormSnapshot: vi.fn(),
      setProducts: vi.fn(),
      removeProduct: vi.fn(),
      excludeProduct: vi.fn(),
      undoExcludeProduct: vi.fn(),
      updateTotalFixedEnergy: vi.fn(),
      clear: vi.fn(),
    } as unknown as MockType<BaselineEnergyDraftService>;

    TestBed.configureTestingModule({
      imports: [ExcludeProductComponent, RouterModule.forRoot([])],
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

    vi.spyOn(store, 'select').mockImplementation((selector) => {
      if (selector === requestTaskQuery.selectRequestTaskPayload) {
        return signal(mockRequestTaskPayload);
      }

      if (selector === underlyingAgreementQuery.selectSectionsCompleted) return signal({});
      if (selector === requestTaskQuery.selectRequestTaskId) return signal(mockRequestTaskId);
      if (selector === underlyingAgreementQuery.selectUnderlyingAgreement) {
        return signal(mockUnderlyingAgreement);
      }

      if (selector === underlyingAgreementReviewQuery.selectReviewSectionsCompleted) return signal({});

      if (selector === underlyingAgreementReviewQuery.selectReviewGroupDecisions) return signal({});

      if (selector === underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions) return signal({});

      if (selector === underlyingAgreementReviewQuery.selectDetermination) return signal({ type: null });

      if (typeof selector === 'function') {
        return signal(mockFacility);
      }

      return signal({});
    });

    fixture = TestBed.createComponent(ExcludeProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the component with the correct content', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });

  it('should display the product name in the heading', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Product 1');
  });

  it('should display warning text', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('The product will be treated as excluded');
  });

  it('should display exclude confirmation message', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Your product and all its data will remain available');
  });

  it('should have exclude button', () => {
    const compiled = fixture.nativeElement;
    const button = compiled.querySelector('.govuk-button--warning');
    expect(button).toBeTruthy();
    expect(button.textContent).toContain('Exclude product');
  });

  it('should call draftService.excludeProduct when exclude button is clicked', () => {
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    const button = fixture.nativeElement.querySelector('.govuk-button--warning');
    button.click();
    expect(draftService.excludeProduct).toHaveBeenCalledWith('Product 1');
  });
});
