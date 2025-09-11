import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { RequestTaskFileService } from '@shared/services';
import { screen } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';

import { CcaPeerReviewDecision } from 'cca-api';

import { AdminTerminationPeerReviewStore } from '../+state';
import { mockAdminTerminationPeerReviewRequestTaskState } from '../testing/mock-data';
import { PeerReviewDecisionComponent } from './peer-review-decision.component';
import { PeerReviewDecisionFormProvider } from './peer-review-decision-form.provider';

describe('PeerReviewDecisionComponent', () => {
  let component: PeerReviewDecisionComponent;
  let fixture: ComponentFixture<PeerReviewDecisionComponent>;
  let router: Router;
  let store: RequestTaskStore;
  let peerReviewStore: AdminTerminationPeerReviewStore;
  let user: ReturnType<typeof userEvent.setup>;

  const mockRequestTaskFileService = {
    buildFormControl: jest.fn().mockReturnValue(new FormControl([])),
  };

  beforeEach(async () => {
    user = userEvent.setup();

    await TestBed.configureTestingModule({
      imports: [PeerReviewDecisionComponent, ReactiveFormsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        AdminTerminationPeerReviewStore,
        PeerReviewDecisionFormProvider,
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: RequestTaskFileService, useValue: mockRequestTaskFileService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    peerReviewStore = TestBed.inject(AdminTerminationPeerReviewStore);
    store.setState(mockAdminTerminationPeerReviewRequestTaskState);

    fixture = TestBed.createComponent(PeerReviewDecisionComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the form elements', () => {
    const radioButtons = screen.getAllByRole('radio');
    expect(radioButtons).toHaveLength(2);
    expect(radioButtons[0]).toHaveAttribute('value', 'AGREE');
    expect(radioButtons[1]).toHaveAttribute('value', 'DISAGREE');

    expect(screen.getByRole('textbox')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Continue' })).toBeInTheDocument();
  });

  it('should navigate to check-your-answers on valid form submission', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const setStateSpy = jest.spyOn(peerReviewStore, 'setState');
    const activatedRoute = TestBed.inject(ActivatedRoute);

    component['form'].patchValue({
      type: 'AGREE',
      notes: 'I agree with the determination',
      files: [],
    });

    component['form'].markAllAsTouched();
    fixture.detectChanges();

    component.onSubmit();
    fixture.detectChanges();

    expect(setStateSpy).toHaveBeenCalledWith({
      decision: {
        type: 'AGREE',
        notes: 'I agree with the determination',
        files: [],
      } as CcaPeerReviewDecision,
      attachments: {},
    });

    expect(navigateSpy).toHaveBeenCalledWith(['check-your-answers'], {
      relativeTo: activatedRoute,
    });
  });

  it('should have return link', () => {
    const returnLink = screen.getByRole('link', { name: 'Return to: Admin termination peer review' });
    expect(returnLink).toBeInTheDocument();
    expect(returnLink).toHaveAttribute('routerLink', '../');
  });

  it('should update form when decision changes', async () => {
    const agreeRadio = screen.getByLabelText('I agree with the determination');
    const disagreeRadio = screen.getByLabelText('I do not agree with the determination');

    await user.click(disagreeRadio);
    expect(component['form'].value.type).toBe('DISAGREE');

    await user.click(agreeRadio);
    expect(component['form'].value.type).toBe('AGREE');
  });
});
