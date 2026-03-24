import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { CaExternalContactsService, NoticeRecipientsService, RegulatorAuthoritiesService } from 'cca-api';
import { TasksService } from 'cca-api';

import { mockRequestTaskItemDTO } from '../testing/mock-data';
import UnderlyingAgreementVariationActivationNotifyOperatorComponent from './underlying-agreement-variation-activation-notify-operator.component';

describe('UnderlyingAgreementVariationActivationNotifyOperatorComponent', () => {
  let component: UnderlyingAgreementVariationActivationNotifyOperatorComponent;
  let fixture: ComponentFixture<UnderlyingAgreementVariationActivationNotifyOperatorComponent>;
  let store: RequestTaskStore;

  const tasksService: Partial<jest.Mocked<TasksService>> = {
    getDefaultNoticeRecipients: jest.fn().mockReturnValue(
      of([
        {
          email: 'test@example.com',
          firstName: 'fname',
          lastName: 'lname',
          type: 'OPERATOR',
          userId: 'oper-id1',
        },
      ]),
    ),
  };

  const noticeRecipientsService: Partial<jest.Mocked<NoticeRecipientsService>> = {
    getAdditionalNoticeRecipients: jest.fn().mockReturnValue(
      of([
        {
          email: 'test-add@example.com',
          firstName: 'fname2',
          lastName: 'lname2',
          type: 'SECTOR_USER',
          userId: 'sec-id2',
        },
      ]),
    ),
  };

  const caExternalContactsService: Partial<jest.Mocked<CaExternalContactsService>> = {
    getCaExternalContacts: jest.fn().mockReturnValue(
      of({
        caExternalContacts: [
          {
            description: 'descr',
            email: 'ext-cont@cca.uk',
            id: 1,
            name: 'John',
          },
        ],
      }),
    ),
  };

  const regulatorAuthoritiesService: Partial<jest.Mocked<RegulatorAuthoritiesService>> = {
    getCaRegulators: jest.fn().mockReturnValue(
      of({
        caUsers: [
          {
            firstName: 'reg-fname',
            lastName: 'reg-lname',
            userId: 'reg-userid',
          },
        ],
      }),
    ),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UnderlyingAgreementVariationActivationNotifyOperatorComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TasksService, useValue: tasksService },
        { provide: NoticeRecipientsService, useValue: noticeRecipientsService },
        { provide: CaExternalContactsService, useValue: caExternalContactsService },
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Upload target unit assent' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(UnderlyingAgreementVariationActivationNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(getByText('Notify operator', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Select who should be notified of the decision', fixture.nativeElement)).toBeTruthy();
  });

  it('should contain submit button and "return to" link', () => {
    expect(getByText('Confirm and complete', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Return to: Upload target unit assent', fixture.nativeElement)).toBeTruthy();
  });

  it('should display the correct form fields', () => {
    expect(getByText('Users automatically notified', fixture.nativeElement)).toBeTruthy();
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
