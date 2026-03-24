import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

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
          transactionId: 'CCATM01206',
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
    expect(getByText('Received amount updated', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Return to: Target unit MoA CCATM01206', fixture.nativeElement)).toBeTruthy();
  });
});
