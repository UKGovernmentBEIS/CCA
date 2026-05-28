import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { mockTprRequestTaskStateThroughputTotalsOnly } from '../../../testing/mock-data';
import { TprThroughputDetailsComponent } from './tpr-throughput-details.component';

describe('TprThroughputDetailsComponent', () => {
  let component: TprThroughputDetailsComponent;
  let fixture: ComponentFixture<TprThroughputDetailsComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TprThroughputDetailsComponent],
      providers: [
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Provide target period throughput details' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockTprRequestTaskStateThroughputTotalsOnly);

    fixture = TestBed.createComponent(TprThroughputDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => vi.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set variableEnergyType to totals only', () => {
    expect(getByText(/Totals only/, fixture.nativeElement)).toBeTruthy();
  });
});
