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
import { UndoProductComponent } from './undo-product.component';

describe('UndoProductComponent', () => {
  let component: UndoProductComponent;
  let fixture: ComponentFixture<UndoProductComponent>;
  let store: RequestTaskStore;
  let tasksApiService: MockType<TasksApiService>;

  beforeEach(() => {
    tasksApiService = {
      saveRequestTaskAction: jest.fn().mockReturnValue(of(mockRequestTaskPayload)),
    };

    TestBed.configureTestingModule({
      imports: [UndoProductComponent],
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

  it('should call onUndo when undo button is clicked', () => {
    const onUndoSpy = jest.spyOn(component, 'onUndo');
    const button = fixture.nativeElement.querySelector('.govuk-button');
    button.click();
    expect(onUndoSpy).toHaveBeenCalled();
  });
});
