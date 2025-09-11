import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';
import UserEvent from '@testing-library/user-event';

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
        ['Target unit ID', 'Operator', 'Transaction ID', 'Current total amount (GBP)', 'Current received amount (GBP)'],
        ['ADS_52', 'Aerospace_52', 'CCATM01206', '370', '5,100'],
      ],
      [[], []],
    ]);
  });

  it('should contain 2 history entries', () => {
    const user = UserEvent.setup();
    user.click(screen.getByTestId('history-details'));

    fixture.detectChanges();

    expect(screen.getAllByTestId('amount-header')).toHaveLength(2);
  });

  it('should not navigate to next step if the form is invalid', async () => {
    const user = UserEvent.setup();
    const navigateSpy = jest.spyOn(router, 'navigate');

    user.click(screen.getByText('Continue'));
    fixture.detectChanges();

    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
