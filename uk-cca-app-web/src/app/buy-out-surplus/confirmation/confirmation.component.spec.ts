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
          useValue: new ActivatedRouteStub(null, { referenceCode: 'BS-TP6001' }),
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
    expect(screen.getByText('Your reference code is: BS-TP6001')).toBeInTheDocument();

    expect(
      screen.getByText(
        'You can navigate away from this page. The batch run progress can be monitored via the "Workflow history" section, where you can check its status at any time and review any failed TUs if applicable.',
      ),
    ).toBeInTheDocument();

    expect(screen.getByText('What happens next')).toBeInTheDocument();

    expect(
      screen.getByText(
        'Once the batch is completed you will be able to view and monitor outstanding amounts through the details of the "Transactions tab" (dedicated tab in buy-out and surplus main screen).',
      ),
    ).toBeInTheDocument();

    expect(
      screen.getByText('Select "BS-TP6001" from your "Workflow history" tab to find the report.'),
    ).toBeInTheDocument();

    expect(screen.getByText('Return to: Buy-out and surplus')).toBeInTheDocument();
  });
});
