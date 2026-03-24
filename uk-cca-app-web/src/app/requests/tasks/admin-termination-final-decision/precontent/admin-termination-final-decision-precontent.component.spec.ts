import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { mockAdminTerminationFinalDecisionPayload } from '../testing/mock-data';
import { AdminTerminationFinalDecisionPrecontentComponent } from './admin-termination-final-decision-precontent.component';

describe('AdminTerminationFinalDecisionPrecontentComponent', () => {
  let component: AdminTerminationFinalDecisionPrecontentComponent;
  let fixture: ComponentFixture<AdminTerminationFinalDecisionPrecontentComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTerminationFinalDecisionPrecontentComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'TEST_TYPE' } });
    store.setPayload(mockAdminTerminationFinalDecisionPayload);
    store.setState({ ...store.state, isEditable: true });

    fixture = TestBed.createComponent(AdminTerminationFinalDecisionPrecontentComponent);
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
