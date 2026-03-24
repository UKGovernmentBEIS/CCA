import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { ReceivedAmountStore } from '../received-amount.store';
import { mockReceivedAmountStoreState } from '../testing/mock-data';
import { CheckYourAnswersComponent } from './check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: CheckYourAnswersComponent;
  let fixture: ComponentFixture<CheckYourAnswersComponent>;
  let receivedAmountStore: ReceivedAmountStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckYourAnswersComponent],
      providers: [
        ReceivedAmountStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ moaId: 1 }) },
      ],
    }).compileComponents();

    receivedAmountStore = TestBed.inject(ReceivedAmountStore);
    receivedAmountStore.setState(mockReceivedAmountStoreState);

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
          'Target unit ID',
          'Operator',
          'Transaction ID',
          'Added payment (GBP)',
          'New received payment (GBP)',
          'Comments',
          'Uploaded evidence',
        ],
        ['ADS_52', 'Aerospace_52', 'CCATM01206', '350', '5,450', 'mplah mplah', 'No files provided'],
      ],
    ]);
  });
});
