import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { targetUnitMoaGeneratedActionStateMock } from './testing/mock-data';
import { TuMoaGeneratedComponent } from './tu-moa-generated.component';

describe('TuMoaGeneratedComponent', () => {
  let component: TuMoaGeneratedComponent;
  let fixture: ComponentFixture<TuMoaGeneratedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TuMoaGeneratedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(targetUnitMoaGeneratedActionStateMock);

    fixture = TestBed.createComponent(TuMoaGeneratedComponent);
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
        ['Payment request ID', 'Charging year', 'Transaction ID', 'Payment requests notice'],
        ['S2515', '2025', 'CCATM01203', '2025 Target Unit MoA - ADS_52-T00001 - CCATM01203.pdf'],
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
