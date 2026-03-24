import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { SectorMoasReceivedAmountStore } from '../received-amount.store';
import { mockReceivedAmountStoreState } from '../testing/mock-data';
import { CheckYourAnswersComponent } from './check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: CheckYourAnswersComponent;
  let fixture: ComponentFixture<CheckYourAnswersComponent>;
  let sectorMoasReceivedAmountStore: SectorMoasReceivedAmountStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckYourAnswersComponent],
      providers: [
        SectorMoasReceivedAmountStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ moaId: 1 }) },
      ],
    }).compileComponents();

    sectorMoasReceivedAmountStore = TestBed.inject(SectorMoasReceivedAmountStore);
    sectorMoasReceivedAmountStore.setState(mockReceivedAmountStoreState);

    fixture = TestBed.createComponent(CheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct amount data', () => {
    const detailsValues = getSummaryListData(fixture.nativeElement);

    expect(detailsValues).toEqual([
      [
        [
          'Sector',
          'Transaction ID',
          'Added payment (GBP)',
          'New received payment (GBP)',
          'Comments',
          'Uploaded evidence',
        ],
        ['ADS_52-Aerospace_52', 'CCACM01201', '350', '5,450', 'mplah mplah', 'No files provided'],
      ],
    ]);
  });
});
