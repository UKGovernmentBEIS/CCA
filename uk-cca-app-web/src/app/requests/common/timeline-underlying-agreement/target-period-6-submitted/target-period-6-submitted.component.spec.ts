import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockUnderlyingAgreementSubmittedRequestAction } from '../testing/mock-data';
import { TargetPeriod6SubmittedComponent } from './target-period-6-submitted.component';

describe('TargetPeriod6Component', () => {
  let fixture: ComponentFixture<TargetPeriod6SubmittedComponent>;
  let store: RequestActionStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TargetPeriod6SubmittedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setAction(mockUnderlyingAgreementSubmittedRequestAction);

    fixture = TestBed.createComponent(TargetPeriod6SubmittedComponent);
    fixture.detectChanges();
  });

  it('should show summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
