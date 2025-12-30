import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockAdminTerminationFinalDecisionPayload } from '../../../testing/mock-data';
import FinalDecisionReasonSummaryComponent from './final-decision-reason-summary.component';

describe('FinalDecisionReasonSummaryComponent', () => {
  let component: FinalDecisionReasonSummaryComponent;
  let fixture: ComponentFixture<FinalDecisionReasonSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FinalDecisionReasonSummaryComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Admin termination final decision' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_FINAL_DECISION' as any } });
    store.setPayload(mockAdminTerminationFinalDecisionPayload);

    fixture = TestBed.createComponent(FinalDecisionReasonSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
