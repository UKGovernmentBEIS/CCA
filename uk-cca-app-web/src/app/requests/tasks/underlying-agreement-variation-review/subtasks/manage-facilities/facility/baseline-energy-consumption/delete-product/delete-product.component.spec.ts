import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { MockType } from '@netz/common/testing';
import { TasksApiService, underlyingAgreementQuery, underlyingAgreementReviewQuery } from '@requests/common';

import {
  mockActivatedRouteWithProductParams,
  mockFacility,
  mockRequestTaskId,
  mockRequestTaskPayload,
  mockUnderlyingAgreement,
} from '../testing/mock-data';
import { DeleteProductComponent } from './delete-product.component';

describe('DeleteProductComponent', () => {
  let component: DeleteProductComponent;
  let fixture: ComponentFixture<DeleteProductComponent>;
  let store: RequestTaskStore;
  let tasksApiService: MockType<TasksApiService>;

  beforeEach(() => {
    tasksApiService = {
      saveRequestTaskAction: jest.fn().mockReturnValue(of(mockRequestTaskPayload)),
    };

    TestBed.configureTestingModule({
      imports: [DeleteProductComponent],
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

      if (selector === underlyingAgreementReviewQuery.selectReviewSectionsCompleted) return signal({});

      if (selector === underlyingAgreementReviewQuery.selectReviewGroupDecisions) return signal({});

      if (selector === underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions) return signal({});

      if (selector === underlyingAgreementReviewQuery.selectDetermination) return signal({ type: null });

      if (typeof selector === 'function') {
        return signal(mockFacility);
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

  it('should render the component with the correct content', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should display the product name in the heading', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Product 1');
  });

  it('should display warning text', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('You will not be able to undo this action');
  });

  it('should display delete confirmation message', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Your product and all its data will be deleted permanently');
  });

  it('should have delete button', () => {
    const compiled = fixture.nativeElement;
    const button = compiled.querySelector('.govuk-button--warning');
    expect(button).toBeTruthy();
    expect(button.textContent).toContain('Delete product');
  });

  it('should call onDelete when delete button is clicked', () => {
    const onDeleteSpy = jest.spyOn(component, 'onDelete');
    const button = fixture.nativeElement.querySelector('.govuk-button--warning');
    button.click();
    expect(onDeleteSpy).toHaveBeenCalled();
  });
});
