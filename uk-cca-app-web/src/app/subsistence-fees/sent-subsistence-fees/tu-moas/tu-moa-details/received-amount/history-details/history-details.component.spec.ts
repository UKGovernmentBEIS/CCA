import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { ReceivedAmountStore } from '../received-amount.store';
import { mockReceivedAmountStoreState } from '../testing/mock-data';
import { HistoryDetailsComponent } from './history-details.component';

describe('HistoryDetailsComponent', () => {
  let component: HistoryDetailsComponent;
  let fixture: ComponentFixture<HistoryDetailsComponent>;
  let receivedAmountStore: ReceivedAmountStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoryDetailsComponent],
      providers: [
        ReceivedAmountStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ detailsId: 1 }) },
      ],
    }).compileComponents();

    receivedAmountStore = TestBed.inject(ReceivedAmountStore);
    receivedAmountStore.setState(mockReceivedAmountStoreState);

    fixture = TestBed.createComponent(HistoryDetailsComponent);
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
        ['Amount added (GBP)', 'Comments', 'Uploaded evidence'],
        ['20', 'asdasdasdsad', 'sample_profile1.pngsample_profile.bmp'],
      ],
    ]);
  });
});
