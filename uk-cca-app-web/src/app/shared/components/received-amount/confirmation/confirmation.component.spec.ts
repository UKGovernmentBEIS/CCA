import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { SubsistenceFeesMoAViewService } from 'cca-api';

import { ConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;
  let subsistenceFeesMoAViewService: jest.Mocked<Partial<SubsistenceFeesMoAViewService>>;

  beforeEach(async () => {
    subsistenceFeesMoAViewService = {
      getSubsistenceFeesMoaDetailsById: jest.fn().mockReturnValue(
        of({
          moaId: 1,
          transactionId: 'CCACM1200',
          businessId: 'ADS_1',
          name: 'Aerospace_1',
          paymentStatus: 'AWAITING_PAYMENT',
          markFacilitiesStatus: 'IN_PROGRESS',
          currentTotalAmount: '400',
          outstandingTotalAmount: '300',
          submissionDate: '2025-03-13',
        }),
      ),
    };

    await TestBed.configureTestingModule({
      imports: [ConfirmationComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: SubsistenceFeesMoAViewService, useValue: subsistenceFeesMoAViewService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct content', () => {
    expect(screen.getByText('Received amount updated')).toBeInTheDocument();
    expect(screen.getByText('Return to: Sector MoA CCACM1200')).toBeInTheDocument();
  });
});
