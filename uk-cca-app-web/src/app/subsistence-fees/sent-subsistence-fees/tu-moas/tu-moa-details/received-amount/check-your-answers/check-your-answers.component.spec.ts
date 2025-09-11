import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

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
    const detailsValues = screen
      .getAllByText((_, el) => el.tagName.toLowerCase() === 'dl')
      .map((el) => [
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
        Array.from(el.querySelectorAll('dd'))
          .filter((dt) => dt.textContent.trim() !== 'Change')
          .map((dt) => dt.textContent.trim()),
      ]);

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
