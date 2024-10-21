import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { mockReasonForAdminTerminationPayload } from '../../mocks/mock-admin-termination-payload';
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
    store.setPayload(mockReasonForAdminTerminationPayload);

    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct banner and content for regulatory reason', () => {
    expect(screen.getByText('Admin termination notice sent to operator')).toBeInTheDocument();
    expect(document.getElementById('regulatory-reason-content').innerHTML.trim()).toBe(
      'You must allow at least 28 days for the operator to appeal before you can start the final decision for the termination workflow.',
    );
    expect(screen.getByText('What happens next')).toBeInTheDocument();
    expect(
      screen.getByText(
        'A withdrawal task will now appear in your dashboard. You can withdraw the submission of this termination at any time.',
      ),
    ).toBeInTheDocument();
    expect(screen.getByText('Return to: Dashboard')).toBeInTheDocument();
  });
});
