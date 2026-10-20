import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockRequestActionState } from '../testing/mock-data';
import { UnderlyingAgreementReviewedAcceptedDecisionDetailsComponent } from './underlying-agreement-reviewed-accepted-decision-details.component';

describe('UnderlyingAgreementReviewAcceptedDecisionComponent', () => {
  let component: UnderlyingAgreementReviewedAcceptedDecisionDetailsComponent;
  let fixture: ComponentFixture<UnderlyingAgreementReviewedAcceptedDecisionDetailsComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementReviewedAcceptedDecisionDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockRequestActionState);

    fixture = TestBed.createComponent(UnderlyingAgreementReviewedAcceptedDecisionDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
