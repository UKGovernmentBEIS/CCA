import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { MockType } from '@netz/common/testing';
import {
  TasksApiService,
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
import { ExcludeProductComponent } from './exclude-product.component';

describe('ExcludeProductComponent', () => {
  let component: ExcludeProductComponent;
  let fixture: ComponentFixture<ExcludeProductComponent>;
  let store: RequestTaskStore;
  let tasksApiService: MockType<TasksApiService>;

  beforeEach(() => {
    tasksApiService = {
      saveRequestTaskAction: jest.fn().mockReturnValue(of(mockRequestTaskPayload)),
    };

    TestBed.configureTestingModule({
      imports: [ExcludeProductComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: mockActivatedRouteWithProductParams },
        { provide: TasksApiService, useValue: tasksApiService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);

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

    fixture = TestBed.createComponent(ExcludeProductComponent);
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

  it('should call onExclude when exclude button is clicked', () => {
    const onExcludeSpy = jest.spyOn(component, 'onExclude');
    const button = fixture.nativeElement.querySelector('.govuk-button--warning');
    button.click();
    expect(onExcludeSpy).toHaveBeenCalled();
  });
});
