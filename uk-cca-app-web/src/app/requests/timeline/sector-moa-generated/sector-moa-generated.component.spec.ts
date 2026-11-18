import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { SectorMoaGeneratedComponent } from './sector-moa-generated.component';
import { sectorMoaGeneratedActionStateMock } from './tests/mock-data';

describe('SectorMoaGeneratedComponent', () => {
  let component: SectorMoaGeneratedComponent;
  let fixture: ComponentFixture<SectorMoaGeneratedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorMoaGeneratedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(sectorMoaGeneratedActionStateMock);

    fixture = TestBed.createComponent(SectorMoaGeneratedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const summaryValues = getSummaryListData(fixture.nativeElement);

    expect(summaryValues).toEqual([
      [
        ['Payment request ID', 'Charging year', 'Transaction ID', 'Payment requests notice'],
        ['S2513', '2025', 'CCACM01204', '2025 Sector MoA - ADS_1 - CCACM01204.pdf'],
      ],
      [
        ['Users'],
        [
          'Fred_1 William_1, Sector contact, fredwilliam_1@agindustries.org.ukFred_2 William_2, Sector contact, fredwilliam_2@agindustries.org.uk',
        ],
      ],
    ]);
  });
});
