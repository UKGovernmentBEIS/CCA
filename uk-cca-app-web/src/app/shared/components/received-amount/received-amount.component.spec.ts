import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getAllByTestId, getByTestId, getByText, getSummaryListData } from '@testing';

import { ReceivedAmountComponent } from './received-amount.component';
import { SectorMoasReceivedAmountStore } from './received-amount.store';
import { mockReceivedAmountStoreState } from './testing/mock-data';

describe('ReceivedAmountComponent', () => {
  let component: ReceivedAmountComponent;
  let fixture: ComponentFixture<ReceivedAmountComponent>;
  let router: Router;
  let receivedAmountStore: SectorMoasReceivedAmountStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReceivedAmountComponent],
      providers: [
        SectorMoasReceivedAmountStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ moaId: 1 }) },
      ],
    }).compileComponents();

    receivedAmountStore = TestBed.inject(SectorMoasReceivedAmountStore);
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
        ['Sector', 'Transaction ID', 'Current total amount (GBP)', 'Current received amount (GBP)'],
        ['ADS_52 - Aerospace_52', 'CCACM01201', '370', '5,100'],
      ],
      [[], []],
    ]);
  });

  it('should contain 2 history entries', () => {
    click(getByTestId('history-details'));

    fixture.detectChanges();

    expect(getAllByTestId('amount-header').length).toBe(2);
  });

  it('should not navigate to next step if the form is invalid', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    // Make form invalid by clearing the required transactionAmount field
    component.form.controls.transactionAmount.setValue('');
    fixture.detectChanges();

    click(getByText('Continue'));
    fixture.detectChanges();

    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
