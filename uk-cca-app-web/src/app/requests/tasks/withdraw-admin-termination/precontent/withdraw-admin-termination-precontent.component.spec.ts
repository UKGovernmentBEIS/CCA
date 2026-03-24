import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { mockReasonForAdminTerminationWithdrawPayload } from '../testing/mock-data';
import { WithdrawAdminTerminationPrecontentComponent } from './withdraw-admin-termination-precontent.component';

describe('WithdrawAdminTerminationPrecontentComponent', () => {
  let component: WithdrawAdminTerminationPrecontentComponent;
  let fixture: ComponentFixture<WithdrawAdminTerminationPrecontentComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WithdrawAdminTerminationPrecontentComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'TEST_TYPE' } });
    store.setPayload(mockReasonForAdminTerminationWithdrawPayload);
    store.setState({ ...store.state, isEditable: true });

    fixture = TestBed.createComponent(WithdrawAdminTerminationPrecontentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the notify button', () => {
    expect(getByText('Notify operator of decision', fixture.nativeElement)).toBeTruthy();
  });
});
