import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getAllByTestId, getByTestId, getByText, getSummaryListData } from '@testing';

import { ReceivedAmountComponent } from './received-amount.component';
import { ReceivedAmountStore } from './received-amount.store';
import { mockReceivedAmountStoreState } from './testing/mock-data';

describe('ReceivedAmountComponent', () => {
  let component: ReceivedAmountComponent;
  let fixture: ComponentFixture<ReceivedAmountComponent>;
  let router: Router;
  let receivedAmountStore: ReceivedAmountStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReceivedAmountComponent],
      providers: [
        ReceivedAmountStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ moaId: 1 }) },
      ],
    }).compileComponents();

    receivedAmountStore = TestBed.inject(ReceivedAmountStore);
    receivedAmountStore.setState(mockReceivedAmountStoreState);

    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(ReceivedAmountComponent);
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
        ['Target unit ID', 'Operator', 'Transaction ID', 'Current total amount (GBP)', 'Current received amount (GBP)'],
        ['ADS_52', 'Aerospace_52', 'CCATM01206', '370', '5,100'],
      ],
      [[], []],
    ]);
  });

  it('should contain 2 history entries', () => {
    click(getByTestId('history-details', fixture.nativeElement));

    fixture.detectChanges();

    expect(getAllByTestId('amount-header', fixture.nativeElement)).toHaveLength(2);
  });

  it('should navigate to next step on continue', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    click(getByText('Continue', fixture.nativeElement));
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['check-your-answers'], expect.anything());
  });
});
