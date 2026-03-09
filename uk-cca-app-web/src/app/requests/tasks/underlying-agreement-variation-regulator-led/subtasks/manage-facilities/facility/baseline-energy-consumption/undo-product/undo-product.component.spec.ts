import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { MockType } from '@netz/common/testing';
import {
  BaselineEnergyDraftService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';

import {
  mockActivatedRouteWithProductParams,
  mockFacility,
  mockRequestTaskId,
  mockRequestTaskPayload,
  mockUnderlyingAgreement,
} from '../testing/mock-data';
import { UndoProductComponent } from './undo-product.component';

describe('UndoProductComponent', () => {
  let component: UndoProductComponent;
  let fixture: ComponentFixture<UndoProductComponent>;
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
      excludeProduct: jest.fn(),
      undoExcludeProduct: jest.fn(),
      updateTotalFixedEnergy: jest.fn(),
      clear: jest.fn(),
    };

    TestBed.configureTestingModule({
      imports: [UndoProductComponent, RouterModule],
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
        return signal(mockRequestTaskPayload);
      }

      if (selector === underlyingAgreementQuery.selectSectionsCompleted) return signal({});
      if (selector === requestTaskQuery.selectRequestTaskId) return signal(mockRequestTaskId);
      if (selector === underlyingAgreementQuery.selectUnderlyingAgreement) {
        return signal(mockUnderlyingAgreement);
      }

      if (selector === underlyingAgreementReviewQuery.selectReviewSectionsCompleted) {
        return signal({});
      }

      if (selector === underlyingAgreementVariationQuery.selectReviewGroupDecisions) {
        return signal({});
      }

      if (selector === underlyingAgreementVariationQuery.selectFacilityReviewGroupDecisions) {
        return signal({});
      }

      if (typeof selector === 'function') {
        return signal(mockFacility);
      }

      return signal({});
    });

    fixture = TestBed.createComponent(UndoProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the component with the correct content', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should display the product name in the heading', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Product 1');
  });

  it('should display warning text', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Only undo if the product should remain in scope');
  });

  it('should display undo confirmation message', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('The product will be reinstated as live');
  });

  it('should have undo button', () => {
    const compiled = fixture.nativeElement;
    const button = compiled.querySelector('.govuk-button');
    expect(button).toBeTruthy();
    expect(button.textContent).toContain('Undo exclusion');
  });

  it('should call draftService.undoExcludeProduct when onUndo is called', () => {
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
    component.onUndo();
    expect(draftService.undoExcludeProduct).toHaveBeenCalledWith('Product 1');
  });

  it('should navigate after undo', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    component.onUndo();
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], expect.any(Object));
  });
});
