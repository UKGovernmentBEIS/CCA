import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { CaExternalContactsService, NoticeRecipientsService, RegulatorAuthoritiesService, TasksService } from 'cca-api';

import {
  mockAdminTerminationFinalDecisionNotifyOperatorAdditionalUsers,
  mockAdminTerminationFinalDecisionNotifyOperatorDefaultUsers,
  mockAdminTerminationFinalDecisionNotifyOperatorExternalContacts,
  mockAdminTerminationFinalDecisionNotifyOperatorRegulatorAuthorities,
  mockAdminTerminationFinalDecisionPayload,
} from '../testing/mock-data';
import FinalDecisionNotifyOperatorComponent from './final-decision-notify-operator.component';

describe('FinalDecisionNotifyOperatorComponent', () => {
  let component: FinalDecisionNotifyOperatorComponent;
  let fixture: ComponentFixture<FinalDecisionNotifyOperatorComponent>;
  let store: RequestTaskStore;

  const tasksService: Partial<jest.Mocked<TasksService>> = {
    getDefaultNoticeRecipients: jest
      .fn()
      .mockReturnValue(of(mockAdminTerminationFinalDecisionNotifyOperatorDefaultUsers)),
  };

  const noticeRecipientsService: Partial<jest.Mocked<NoticeRecipientsService>> = {
    getAdditionalNoticeRecipients: jest
      .fn()
      .mockReturnValue(of(mockAdminTerminationFinalDecisionNotifyOperatorAdditionalUsers)),
  };

  const caExternalContactsService: Partial<jest.Mocked<CaExternalContactsService>> = {
    getCaExternalContacts: jest
      .fn()
      .mockReturnValue(of(mockAdminTerminationFinalDecisionNotifyOperatorExternalContacts)),
  };

  const regulatorAuthoritiesService: Partial<jest.Mocked<RegulatorAuthoritiesService>> = {
    getCaRegulators: jest.fn().mockReturnValue(of(mockAdminTerminationFinalDecisionNotifyOperatorRegulatorAuthorities)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FinalDecisionNotifyOperatorComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TasksService, useValue: tasksService },
        { provide: NoticeRecipientsService, useValue: noticeRecipientsService },
        { provide: CaExternalContactsService, useValue: caExternalContactsService },
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Admin termination final decision' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_FINAL_DECISION' } });
    store.setPayload(mockAdminTerminationFinalDecisionPayload);

    fixture = TestBed.createComponent(FinalDecisionNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(screen.getByText('Notify operator of decision')).toBeInTheDocument();
    expect(screen.getByText('Select who should receive the termination notice')).toBeInTheDocument();
  });

  it('should contain submit button and "return to" link', () => {
    expect(screen.getByText('Confirm and complete')).toBeInTheDocument();
    expect(screen.getByText('Return to: Admin termination final decision')).toBeInTheDocument();
  });

  it('should display the correct form fields', () => {
    expect(screen.getByText('Select any additional users you want to notify')).toBeInTheDocument();
    expect(screen.getByText('Select the external contacts you want to notify')).toBeInTheDocument();
    expect(
      screen.getByText('Select the name and signature that will be shown on the official notice document'),
    ).toBeInTheDocument();
  });
});
