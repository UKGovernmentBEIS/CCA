import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { SubsistenceFeesMoaSearchResultInfoDTO } from 'cca-api';

import { SubsistenceFeesTabComponent } from './subsistence-fees-tab.component';

const mockSectorMoas: SubsistenceFeesMoaSearchResultInfoDTO[] = [
  {
    moaId: 1,
    transactionId: 'CCACM1200',
    businessId: 'ADS_1',
    name: 'Aerospace_1',
    paymentStatus: 'AWAITING_PAYMENT',
    markFacilitiesStatus: 'IN_PROGRESS',
    currentTotalAmount: '400',
    outstandingTotalAmount: '300',
    submissionDate: '2025-03-13',
  },
  {
    moaId: 2,
    transactionId: 'CCACM1201',
    businessId: 'ADS_2',
    name: 'Aerospace_2',
    paymentStatus: 'AWAITING_PAYMENT',
    markFacilitiesStatus: 'IN_PROGRESS',
    currentTotalAmount: '400',
    outstandingTotalAmount: '200',
    submissionDate: '2025-03-14',
  },
];

describe('SubsistenceFeesTabComponent', () => {
  let component: SubsistenceFeesTabComponent;
  let fixture: ComponentFixture<SubsistenceFeesTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubsistenceFeesTabComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ sectorId: 1 }) },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SubsistenceFeesTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate with correct number of table rows', () => {
    component.state.set({
      currentPage: 0,
      pageSize: 30,
      subsistenceFeesMoas: mockSectorMoas,
      totalItems: mockSectorMoas.length,
    });

    fixture.detectChanges();

    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(component.state().totalItems + 1);
  });
});
