import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { BuyOutAndSurplusInfoService } from 'cca-api';

import { NewBatchComponent } from './new-batch.component';

describe('NewBatchComponent', () => {
  let component: NewBatchComponent;
  let fixture: ComponentFixture<NewBatchComponent>;
  let buyOutAndSurplusInfoService: Partial<jest.Mocked<BuyOutAndSurplusInfoService>>;

  const mockExcludedAccounts = [
    { accountId: 1, businessId: 'ADS_1-T00001', name: 'tu1-oper1' },
    { accountId: 2, businessId: 'ADS_1-T00002', name: 'tu1-oper2' },
  ];

  beforeEach(async () => {
    buyOutAndSurplusInfoService = {
      getExcludedAccountsForBuyOutSurplusRun: jest.fn().mockReturnValue(of(mockExcludedAccounts)),
    };

    await TestBed.configureTestingModule({
      imports: [NewBatchComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: BuyOutAndSurplusInfoService, useValue: buyOutAndSurplusInfoService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NewBatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain appropirate content', () => {
    expect(getByText('New buy-out and surplus batch')).toBeTruthy();

    expect(getByText('Target units on hold')).toBeTruthy();

    expect(
      getByText(
        'Target units below will not be included in this batch. In case you want to change their status, you can do so from the respective target unit account page on the "Buy-out and surplus" tab.',
      ),
    ).toBeTruthy();

    expect(
      getByText(
        'This list represents a current snapshot. Any immediate changes to the "On Hold" status before you click the "Send buy-out and surplus batch button" will not be automatically reflected in this list, but they will take effect during the batch processing.',
      ),
    ).toBeTruthy();

    expect(getByText('Send buy-out and surplus batch')).toBeTruthy();
  });

  it('should populate with correct data', () => {
    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(mockExcludedAccounts.length + 1);
  });
});
