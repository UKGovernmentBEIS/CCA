import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { NewSubsistenceFeesPaymentRequestComponent } from './new-subsistence-fees-payment-request.component';

describe('NewSubsistenceFeesPaymentRequestComponent', () => {
  let component: NewSubsistenceFeesPaymentRequestComponent;
  let fixture: ComponentFixture<NewSubsistenceFeesPaymentRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewSubsistenceFeesPaymentRequestComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NewSubsistenceFeesPaymentRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain appropirate content', () => {
    expect(getByText('New payment request')).toBeTruthy();

    expect(
      getByText(
        'You are about to send payment requests for subsistence fees to all eligible sectors and target units.',
      ),
    ).toBeTruthy();

    expect(getByText('Send payment requests')).toBeTruthy();
  });
});
