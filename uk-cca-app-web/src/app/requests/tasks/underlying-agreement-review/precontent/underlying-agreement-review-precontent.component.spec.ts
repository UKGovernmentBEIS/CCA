import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { mockClass } from '@netz/common/testing';
import { mockUNAReviewRequestTaskState } from '@requests/common';
import { click, getByText } from '@testing';
import { Mocked } from 'vitest';

import { UnderlyingAgreementReviewPrecontentComponent } from './underlying-agreement-review-precontent.component';

describe('UnderlyingAgreementReviewPrecontentComponent', () => {
  let fixture: ComponentFixture<UnderlyingAgreementReviewPrecontentComponent>;
  let router: Router;

  const mockRoute = mockClass(ActivatedRoute);
  const mockRouter: Mocked<Partial<Router>> = { navigate: vi.fn().mockReturnValue(of(null)) };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementReviewPrecontentComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), RequestTaskStore, provideRouter([])],
    })
      .overrideProvider(Router, { useValue: mockRouter })
      .overrideProvider(ActivatedRoute, { useValue: mockRoute })
      .compileComponents();

    fixture = TestBed.createComponent(UnderlyingAgreementReviewPrecontentComponent);
    const store = TestBed.inject(RequestTaskStore);
    store.setState(mockUNAReviewRequestTaskState);
    store.setState({ ...store.state, isEditable: true });
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should render notify button', () => {
    expect(getByText('Notify operator of decision')).toBeTruthy();
  });

  it('should navigate to correct url', async () => {
    const spy = vi.spyOn(router, 'navigate');

    await click(getByText('Notify operator of decision'));
    expect(spy).toHaveBeenCalledWith(['underlying-agreement-review', 'notify-operator'], { relativeTo: mockRoute });
  });
});
