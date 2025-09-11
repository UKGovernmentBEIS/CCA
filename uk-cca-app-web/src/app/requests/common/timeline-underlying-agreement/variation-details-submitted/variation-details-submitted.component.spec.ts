import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockUnderlyingAgreementVariationSubmittedRequestAction } from '../testing/mock-data';
import { VariationDetailsSubmittedComponent } from './variation-details-submitted.component';

describe('VariationDetailsComponent', () => {
  let fixture: ComponentFixture<VariationDetailsSubmittedComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VariationDetailsSubmittedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setAction(mockUnderlyingAgreementVariationSubmittedRequestAction);

    fixture = TestBed.createComponent(VariationDetailsSubmittedComponent);
    fixture.detectChanges();
  });

  it('should show summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
