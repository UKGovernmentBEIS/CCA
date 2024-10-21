import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { mockAdminTerminationFinalDecisionPayload } from '../../mocks/mock-admin-termination-final-decision-payload';
import ConfirmationComponent from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmationComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'TEST_TYPE' as any } });
    store.setPayload(mockAdminTerminationFinalDecisionPayload);

    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct banner and content', () => {
    expect(screen.getByText('Admin termination final decision notice sent to operator')).toBeInTheDocument();
    expect(screen.getByText('The admin termination agreement has been terminated.')).toBeInTheDocument();
    expect(
      screen.getByText('The selected users will receive an email notification of your decision.'),
    ).toBeInTheDocument();
    expect(screen.getByText('Return to: Dashboard')).toBeInTheDocument();
  });
});
