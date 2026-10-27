import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { CountryService } from '@shared/services';

import { mockVariationReviewRequestTaskState } from '../../../../../common/underlying-agreement/testing/variation-review-mock-data';
import { AvailableActionsComponent } from './available-actions.component';

describe('AvailableActionsComponent', () => {
  let fixture: ComponentFixture<AvailableActionsComponent>;
  let store: RequestTaskStore;

  const mockCountryService = {
    countries: signal([
      { code: 'GB', name: 'United Kingdom', officialName: 'United Kingdom' },
      { code: 'GR', name: 'Greece', officialName: 'Greece' },
    ]),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvailableActionsComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review application for underlying agreement' },
        { provide: CountryService, useValue: mockCountryService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockVariationReviewRequestTaskState);

    fixture = TestBed.createComponent(AvailableActionsComponent);
    fixture.detectChanges();
  });

  it('should match snapshot for AvailableActionsComponent', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});
