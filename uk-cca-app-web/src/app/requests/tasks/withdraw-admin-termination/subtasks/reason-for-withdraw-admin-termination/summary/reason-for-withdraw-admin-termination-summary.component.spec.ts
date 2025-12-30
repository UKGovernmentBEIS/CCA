import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockReasonForAdminTerminationWithdrawPayload } from '../../../testing/mock-data';
import ReasonForWithdrawAdminTerminationSummaryComponent from './reason-for-withdraw-admin-termination-summary.component';

describe('ReasonForWithdrawAdminTerminationSummaryComponent', () => {
  let component: ReasonForWithdrawAdminTerminationSummaryComponent;
  let fixture: ComponentFixture<ReasonForWithdrawAdminTerminationSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReasonForWithdrawAdminTerminationSummaryComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'TEST_TYPE' } });
    store.setPayload(mockReasonForAdminTerminationWithdrawPayload);

    fixture = TestBed.createComponent(ReasonForWithdrawAdminTerminationSummaryComponent);
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
