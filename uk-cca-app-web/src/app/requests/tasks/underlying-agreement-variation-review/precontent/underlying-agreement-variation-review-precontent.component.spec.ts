import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { mockClass } from '@netz/common/testing';
import { mockUNAReviewRequestTaskState } from '@requests/common';
import { click, getByText } from '@testing';

import { UnderlyingAgreementVariationReviewPrecontentComponent } from './underlying-agreement-variation-review-precontent.component';

describe('UnderlyingAgreementReviewPrecontentComponent', () => {
  let fixture: ComponentFixture<UnderlyingAgreementVariationReviewPrecontentComponent>;
  let router: Router;

  const mockRoute = mockClass(ActivatedRoute);
  const mockRouter: jest.Mocked<Partial<Router>> = { navigate: jest.fn().mockReturnValue(of(null)) };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementVariationReviewPrecontentComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), RequestTaskStore, provideRouter([])],
    })
      .overrideProvider(Router, { useValue: mockRouter })
      .overrideProvider(ActivatedRoute, { useValue: mockRoute })
      .compileComponents();

    const store = TestBed.inject(RequestTaskStore);
    store.setState(mockUNAReviewRequestTaskState);
    store.setState({ ...store.state, isEditable: true });

    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(UnderlyingAgreementVariationReviewPrecontentComponent);
    fixture.detectChanges();
  });

  it('should render notify button and navigate to correct url', async () => {
    expect(getByText('Notify operator of decision')).toBeTruthy();

    const spy = jest.spyOn(router, 'navigate');

    click(getByText('Notify operator of decision'));
    expect(spy).toHaveBeenCalledWith(['underlying-agreement-variation-review', 'notify-operator'], {
      relativeTo: mockRoute,
    });
  });

  it('should render peer review button and navigate to correct url', async () => {
    expect(getByText('Send for peer review')).toBeTruthy();

    const spy = jest.spyOn(router, 'navigate');

    click(getByText('Send for peer review'));
    expect(spy).toHaveBeenCalledWith(['underlying-agreement-variation-review', 'send-for-peer-review'], {
      relativeTo: mockRoute,
    });
  });
});
