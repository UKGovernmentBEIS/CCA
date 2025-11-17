import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { BuyOutFeeCalculatedComponent } from './buy-out-fee-calculated.component';
import { buyoutFeeCalculatedActionStateMock } from './tests/mock-data';

describe('BuyOutFeeCalculatedComponent', () => {
  let component: BuyOutFeeCalculatedComponent;
  let fixture: ComponentFixture<BuyOutFeeCalculatedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BuyOutFeeCalculatedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(buyoutFeeCalculatedActionStateMock);

    fixture = TestBed.createComponent(BuyOutFeeCalculatedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const summaryValues = screen
      .getAllByText((_, el) => el.tagName.toLowerCase() === 'dl')
      .map((el) => [
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
        Array.from(el.querySelectorAll('dd'))
          .filter((dt) => dt.textContent.trim() !== 'Change')
          .map((dt) => dt.textContent.trim()),
      ]);

    expect(summaryValues).toEqual([
      [
        ['TPR Version', 'Type', 'Transaction ID', 'Buy-out MoA file', 'Due date', 'Run ID'],
        [
          'TP6-V1 (Buy-out required)',
          'Primary',
          'CCA060001',
          'CCA060001 Primary buy-out MoA.pdf',
          '01 Jul 2025',
          'BOS-TP6003',
        ],
      ],
      [
        ['Amount (tCO2e)', 'Total buy-out fee (GBP)'],
        ['1', '25'],
      ],
      [
        ['Users'],
        [
          'resp1 user, Responsible person, resp1@cca.ukadministr1 user, Administrative contact, administr1@cca.ukFred_52 William_52, Sector contact, fredwilliam_52@agindustries.org.uk',
        ],
      ],
    ]);
  });
});
