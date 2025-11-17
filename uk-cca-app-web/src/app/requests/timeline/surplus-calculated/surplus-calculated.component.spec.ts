import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { SurplusCalculatedComponent } from './surplus-calculated.component';
import { surplusCalculatedActionStateMock } from './tests/mock-data';

describe('SurplusCalculatedComponent', () => {
  let component: SurplusCalculatedComponent;
  let fixture: ComponentFixture<SurplusCalculatedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SurplusCalculatedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(surplusCalculatedActionStateMock);

    fixture = TestBed.createComponent(SurplusCalculatedComponent);
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
        ['TPR Version', 'Type', 'Transaction ID', 'Refund letter', 'Due date', 'Run ID'],
        [
          'TP6-V4 (Target met)',
          'Secondary',
          'CCA060006',
          'CCA060006 Secondary buy-out MoA.pdf',
          '11 May 2025',
          'BOS-TP6012',
        ],
      ],
      [['Surplus gained (tCO2e)'], ['0']],
      [
        ['Users'],
        [
          'resp1 user, Responsible person, resp1@cca.ukadministr1 user, Administrative contact, administr1@cca.ukFred_1 William_1, Sector contact, fredwilliam_1@agindustries.org.uk',
        ],
      ],
    ]);
  });
});
