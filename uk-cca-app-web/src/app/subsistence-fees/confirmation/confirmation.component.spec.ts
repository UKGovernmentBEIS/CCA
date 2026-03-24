import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { ConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmationComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, { referenceCode: 'S2501' }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain appropirate content', () => {
    expect(getByText('Your reference code is: S2501')).toBeTruthy();

    expect(
      getByText('Your payment requests for subsistence fees is in progress. It may take several minutes to complete.'),
    ).toBeTruthy();

    expect(getByText('What happens next')).toBeTruthy();

    expect(
      getByText(
        'Once the payment requests for subsistence fees is complete you will be able to view and monitor outstanding amounts through the details of the "Subsistence fees" tab (dedicated tab in subsistence fees main screen).',
      ),
    ).toBeTruthy();

    expect(getByText('Select "S2501" from your list of sent subsistence fees to find the report.')).toBeTruthy();

    expect(getByText('Return to: Subsistence fees')).toBeTruthy();
  });
});
