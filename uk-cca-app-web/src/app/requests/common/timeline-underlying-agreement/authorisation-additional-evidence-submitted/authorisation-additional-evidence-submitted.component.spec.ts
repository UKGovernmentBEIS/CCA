import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockUnderlyingAgreementSubmittedRequestAction } from '../testing/mock-data';
import { AuthorisationAdditionalEvidenceSubmittedComponent } from './authorisation-additional-evidence-submitted.component';

describe('AuthorisationAdditionalEvidenceComponent', () => {
  let fixture: ComponentFixture<AuthorisationAdditionalEvidenceSubmittedComponent>;
  let store: RequestActionStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AuthorisationAdditionalEvidenceSubmittedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setAction(mockUnderlyingAgreementSubmittedRequestAction);

    fixture = TestBed.createComponent(AuthorisationAdditionalEvidenceSubmittedComponent);
    fixture.detectChanges();
  });

  it('should show summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
