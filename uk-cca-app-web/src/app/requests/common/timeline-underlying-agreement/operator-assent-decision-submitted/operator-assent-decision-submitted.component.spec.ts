import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockUnARegulatorLedVariationSubmittedRequestAction } from '../testing/mock-data';
import { OperatorAssentDecisionSubmittedComponent } from './operator-assent-decision-submitted.component';

describe('OperatorAssentDecisionSubmittedComponent', () => {
  let fixture: ComponentFixture<OperatorAssentDecisionSubmittedComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorAssentDecisionSubmittedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setAction(mockUnARegulatorLedVariationSubmittedRequestAction);

    fixture = TestBed.createComponent(OperatorAssentDecisionSubmittedComponent);
    fixture.detectChanges();
  });

  it('should show summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
