import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { Mock } from 'vitest';

import { BuyOutAndSurplusCostInfoService, TargetPeriodBuyOutDetailsDTO } from 'cca-api';

import { CostTabComponent } from './cost-tab.component';

describe('CostTabComponent', () => {
  let fixture: ComponentFixture<CostTabComponent>;
  let mockService: { getBuyOutCosts: Mock };

  const buyOutCosts: TargetPeriodBuyOutDetailsDTO[] = [
    { id: 9, businessId: 'TP9' },
    { id: 8, businessId: 'TP8', buyOutCost: 38 },
    { id: 7, businessId: 'TP7', buyOutCost: 37 },
  ];

  beforeEach(async () => {
    mockService = {
      getBuyOutCosts: vi.fn().mockReturnValue(of(buyOutCosts)),
    };

    await TestBed.configureTestingModule({
      imports: [CostTabComponent],
      providers: [{ provide: BuyOutAndSurplusCostInfoService, useValue: mockService }, provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(CostTabComponent);
    fixture.detectChanges();
  });

  it('retrieves the CCA3 buy-out costs', () => {
    expect(mockService.getBuyOutCosts).toHaveBeenCalledWith('CCA_3');
  });

  it('displays the target period costs', () => {
    const rows = fixture.nativeElement.querySelectorAll('tbody tr');

    expect(rows).toHaveLength(3);
    expect(rows[0].textContent).toContain('TP7');
    expect(rows[0].textContent).toContain('£37 / tCO2e');
    expect(rows[1].textContent).toContain('TP8');
    expect(rows[1].textContent).toContain('£38 / tCO2e');
    expect(rows[2].textContent).toContain('TP9');
    expect(rows[2].textContent).toContain('Not yet defined');
    expect(rows[2].querySelector('td div').classList.contains('govuk-!-text-align-right')).toBe(true);
  });

  it('links each Change action to its target period cost page', () => {
    const changeLinks = fixture.nativeElement.querySelectorAll('a');

    expect(changeLinks[0].getAttribute('href')).toBe('/cost/TP7');
    expect(changeLinks[1].getAttribute('href')).toBe('/cost/TP8');
    expect(changeLinks[2].getAttribute('href')).toBe('/cost/TP9');
  });
});
