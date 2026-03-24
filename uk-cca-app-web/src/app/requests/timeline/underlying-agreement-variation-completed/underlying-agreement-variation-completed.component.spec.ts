import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockCompletedRequestActionState } from './testing/mock-data';
import { UnderlyingAgreementVariationCompletedComponent } from './underlying-agreement-variation-completed.component';

describe('UnderlyingAgreementVariationCompletedComponent', () => {
  let fixture: ComponentFixture<UnderlyingAgreementVariationCompletedComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementVariationCompletedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockCompletedRequestActionState);

    fixture = TestBed.createComponent(UnderlyingAgreementVariationCompletedComponent);
    fixture.detectChanges();
  });

  it('should match snapshot', () => {
    expect(fixture.nativeElement).toMatchSnapshot();
  });
});
