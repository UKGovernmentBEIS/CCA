import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';
import { Mocked } from 'vitest';

import { CaExternalContactsService, RegulatorAuthoritiesService, RequestInfoDTO, TasksService } from 'cca-api';

import {
  mockAdminTerminationNotifyOperatorAdditionalUsers,
  mockAdminTerminationNotifyOperatorDefaultUsers,
  mockAdminTerminationNotifyOperatorExternalContacts,
  mockAdminTerminationNotifyOperatorRegulatorAuthorities,
  mockReasonForAdminTerminationPayload,
} from '../testing/mock-data';
import AdminTerminationNotifyOperatorComponent from './admin-termination-notify-operator.component';

describe('AdminTerminationNotifyOperatorComponent', () => {
  let component: AdminTerminationNotifyOperatorComponent;
  let fixture: ComponentFixture<AdminTerminationNotifyOperatorComponent>;
  let store: RequestTaskStore;

  const tasksService: Partial<Mocked<TasksService>> = {
    getDefaultNoticeRecipients: vi.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorDefaultUsers)),
    getAdditionalNoticeRecipients: vi.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorAdditionalUsers)),
  };

  const caExternalContactsService: Partial<Mocked<CaExternalContactsService>> = {
    getCaExternalContacts: vi.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorExternalContacts)),
  };

  const regulatorAuthoritiesService: Partial<Mocked<RegulatorAuthoritiesService>> = {
    getCaRegulators: vi.fn().mockReturnValue(of(mockAdminTerminationNotifyOperatorRegulatorAuthorities)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTerminationNotifyOperatorComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TasksService, useValue: tasksService },
        { provide: CaExternalContactsService, useValue: caExternalContactsService },
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Admin termination' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({
      requestTask: { type: 'ADMIN_TERMINATION_APPLICATION_SUBMIT' },
      requestInfo: { accountId: 1 } as RequestInfoDTO,
    });
    store.setPayload(mockReasonForAdminTerminationPayload);

    fixture = TestBed.createComponent(AdminTerminationNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(getByText('Notify operator of decision', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Select who should receive the termination notification', fixture.nativeElement)).toBeTruthy();
  });

  it('should contain submit button and "return to" link', () => {
    expect(getByText('Confirm and complete', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Return to: Admin termination', fixture.nativeElement)).toBeTruthy();
  });

  it('should display the correct form fields', () => {
    expect(getByText('Select any additional users you want to notify', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Select the external contacts you want to notify', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText(
        'Select the name and signature that will be shown on the official notice document',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
  });
});
