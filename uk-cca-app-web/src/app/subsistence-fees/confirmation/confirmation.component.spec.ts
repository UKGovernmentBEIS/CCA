import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

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
    expect(screen.getByText('Your reference code is: S2501')).toBeInTheDocument();

    expect(
      screen.getByText(
        'Your payment requests for subsistence fees is in progress. It may take several minutes to complete.',
      ),
    ).toBeInTheDocument();

    expect(screen.getByText('What happens next')).toBeInTheDocument();

    expect(
      screen.getByText(
        'Once the payment requests for subsistence fees is complete you will be able to view and monitor outstanding amounts through the details of the "Subsistence fees" tab (dedicated tab in subsistence fees main screen).',
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText('Select "S2501" from your list of sent subsistence fees to find the report.'),
    ).toBeInTheDocument();

    expect(screen.getByText('Return to: Subsistence fees')).toBeInTheDocument();
  });
});
