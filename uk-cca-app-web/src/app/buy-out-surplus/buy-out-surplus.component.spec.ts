import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { BuyOutSurplusComponent } from './buy-out-surplus.component';
import { BuyoutSurplusStore } from './buy-out-surplus.store';
import { buyoutSurplusStateMockData } from './testing/mock-data';

describe('BuyOutSurplusComponent', () => {
  let component: BuyOutSurplusComponent;
  let fixture: ComponentFixture<BuyOutSurplusComponent>;
  let buyoutSurplusStore: BuyoutSurplusStore;

  const mockActivatedRoute = {
    snapshot: {
      queryParams: {
        term: 'ABC-123',
        targetPeriodType: 'TP6',
        buyOutSurplusPaymentStatus: 'PAID',
      },
      paramMap: convertToParamMap({}),
      queryParamMap: convertToParamMap({
        term: 'ABC-123',
        targetPeriodType: 'TP6',
        buyOutSurplusPaymentStatus: 'PAID',
      }),
    },
    queryParams: of({
      term: 'ABC-123',
      targetPeriodType: 'TP6',
      buyOutSurplusPaymentStatus: 'PAID',
    }),
    queryParamMap: of(
      convertToParamMap({
        term: 'ABC-123',
        targetPeriodType: 'TP6',
        buyOutSurplusPaymentStatus: 'PAID',
      }),
    ),
    fragment: of('abc'),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BuyOutSurplusComponent],
      providers: [
        BuyoutSurplusStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    buyoutSurplusStore = TestBed.inject(BuyoutSurplusStore);
    buyoutSurplusStore.setState(buyoutSurplusStateMockData);

    fixture = TestBed.createComponent(BuyOutSurplusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display title', () => {
    const title = document.querySelector('h1');
    expect(title.textContent).toBe('Buy-out and surplus');
  });

  it('should display `New buy-out and surplus batch button`', () => {
    const button = document.querySelector('button');
    expect(button.textContent.trim()).toBe('New buy-out and surplus batch');
  });

  it('should display payment request in progress warning', () => {
    buyoutSurplusStore.setState({
      ...buyoutSurplusStore.state,
      runInProgress: true,
    });

    fixture.detectChanges();

    const warning = document.querySelector('strong.govuk-warning-text__text');
    expect(warning.textContent.trim()).toBe(
      'Buy-out and surplus batch is in progress, you cannot initiate a new one until it has finished',
    );
  });
});
