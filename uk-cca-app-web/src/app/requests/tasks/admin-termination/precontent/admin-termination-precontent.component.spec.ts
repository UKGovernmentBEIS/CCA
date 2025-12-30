import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { mockReasonForAdminTerminationPayload } from '../testing/mock-data';
import { AdminTerminationPrecontentComponent } from './admin-termination-precontent.component';

describe('AdminTerminationPrecontentComponent', () => {
  let component: AdminTerminationPrecontentComponent;
  let fixture: ComponentFixture<AdminTerminationPrecontentComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTerminationPrecontentComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'TEST_TYPE' } });
    store.setPayload(mockReasonForAdminTerminationPayload);
    store.setState({ ...store.state, isEditable: true });

    fixture = TestBed.createComponent(AdminTerminationPrecontentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and content', () => {
    expect(screen.getByText('Admin termination details updated')).toBeInTheDocument();

    expect(document.getElementById('notification-content').innerHTML.trim()).toBe(
      'Once you notify the operator, you must allow at least 28 days for the operator to appeal the decision to terminating their agreement. After this time window you may start the final decision for the termination workflow.',
    );

    expect(screen.getByText('Notify operator of decision')).toBeInTheDocument();
  });
});
