import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { BuyOutSurplusBatchRunCompletedComponent } from './buy-out-surplus-batch-run-completed.component';
import { buyoutSurplusBatchRunCompletedActionStateMock } from './tests/mock-data';

describe('BuyOutSurplusBatchRunCompletedComponent', () => {
  let component: BuyOutSurplusBatchRunCompletedComponent;
  let fixture: ComponentFixture<BuyOutSurplusBatchRunCompletedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BuyOutSurplusBatchRunCompletedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(buyoutSurplusBatchRunCompletedActionStateMock);

    fixture = TestBed.createComponent(BuyOutSurplusBatchRunCompletedComponent);
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
        [
          'Status',
          'Total target units',
          'Total buy-out transactions',
          'Total refund transactions',
          'Batch run summary report',
        ],
        ['Completed', '1', '1', '0', 'BOS-TP6003 Buy-out and surplus summary report.csv'],
      ],
    ]);
  });
});
