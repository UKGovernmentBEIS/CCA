import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { CaExternalContactsService, NoticeRecipientsService, RegulatorAuthoritiesService } from 'cca-api';
import { TasksService } from 'cca-api';

import { UnderlyingAgreementActivationTaskService } from '../services/underlying-agreement-activation-task.service';
import { mockRequestTaskItemDTO } from '../testing/mock-data';
import UnderlyingAgreementActivationNotifyOperatorComponent from './underlying-agreement-activation-notify-operator.component';

describe('UnderlyingAgreementActivationNotifyOperatorComponent', () => {
  let component: UnderlyingAgreementActivationNotifyOperatorComponent;
  let fixture: ComponentFixture<UnderlyingAgreementActivationNotifyOperatorComponent>;
  let store: RequestTaskStore;
  let page: Page;
  let router: Router;

  const taskService: Partial<jest.Mocked<UnderlyingAgreementActivationTaskService>> = {
    notifyOperator: jest.fn().mockReturnValue(of({})),
  };

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

  class Page extends BasePage<UnderlyingAgreementActivationNotifyOperatorComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UnderlyingAgreementActivationNotifyOperatorComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TaskService, useValue: taskService },
        { provide: TasksService, useValue: tasksService },
        { provide: NoticeRecipientsService, useValue: noticeRecipientsService },
        { provide: CaExternalContactsService, useValue: caExternalContactsService },
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(UnderlyingAgreementActivationNotifyOperatorComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to notify operator', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const taskServiceSpy = jest.spyOn(taskService, 'notifyOperator');

    page.submitButton.click();
    fixture.detectChanges();

    expect(taskServiceSpy).toHaveBeenCalledWith({
      externalContacts: [],
      operators: [],
      sectorUsers: [],
      signatory: '088fe8e5-9eb9-49d0-a6d0-d2f78031fe79',
    });

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['confirmation'], expect.anything());
  });
});
