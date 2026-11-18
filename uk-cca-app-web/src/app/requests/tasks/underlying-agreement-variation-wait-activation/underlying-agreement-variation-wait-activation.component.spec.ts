import { ComponentFixture, TestBed } from '@angular/core/testing';

import { getByText } from '@testing';

import { UnderlyingAgreementVariationWaitActivationComponent } from './underlying-agreement-variation-wait-activation.component';

describe('UnderlyingAgreementVariationWaitActivationComponent', () => {
  let component: UnderlyingAgreementVariationWaitActivationComponent;
  let fixture: ComponentFixture<UnderlyingAgreementVariationWaitActivationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementVariationWaitActivationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UnderlyingAgreementVariationWaitActivationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(getByText('Waiting for the regulator to make a determination', fixture.nativeElement)).toBeTruthy();
  });
});
