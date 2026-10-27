import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockUNAReviewRequestTaskState } from '@requests/common';
import { CountryService } from '@shared/services';

import ReviewTargetUnitDetailsDecisionComponent from './review-target-unit-details-decision.component';

describe('Review Target Unit Details Decision', () => {
  let fixture: ComponentFixture<ReviewTargetUnitDetailsDecisionComponent>;
  let store: RequestTaskStore;

  const mockCountryService = {
    countries: signal([
      { code: 'GB', name: 'United Kingdom', officialName: 'United Kingdom' },
      { code: 'GR', name: 'Greece', officialName: 'Greece' },
    ]),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewTargetUnitDetailsDecisionComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        RequestTaskStore,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review application for underlying agreement' },
        { provide: CountryService, useValue: mockCountryService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockUNAReviewRequestTaskState);

    fixture = TestBed.createComponent(ReviewTargetUnitDetailsDecisionComponent);
    fixture.detectChanges();
  });

  it('should match snapshot', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});
