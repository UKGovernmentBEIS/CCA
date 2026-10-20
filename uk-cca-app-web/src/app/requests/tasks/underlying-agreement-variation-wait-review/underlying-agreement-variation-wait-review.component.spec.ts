import { ComponentFixture, TestBed } from '@angular/core/testing';

import { getByText } from '@testing';

import { UnderlyingAgreementVariationWaitReviewComponent } from './underlying-agreement-variation-wait-review.component';

describe('UnderlyingAgreementVariationWaitReviewComponent', () => {
  let component: UnderlyingAgreementVariationWaitReviewComponent;
  let fixture: ComponentFixture<UnderlyingAgreementVariationWaitReviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementVariationWaitReviewComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UnderlyingAgreementVariationWaitReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(getByText('Waiting for the regulator to complete the review', fixture.nativeElement)).toBeTruthy();
  });
});
