import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { getByText } from '@testing';

import {
  NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload,
  RequestInfoDTO,
  RequestTaskDTO,
} from 'cca-api';

import SummaryPageComponent from './summary.component';

describe('SummaryPageComponent', () => {
  let component: SummaryPageComponent;
  let fixture: ComponentFixture<SummaryPageComponent>;
  let store: RequestTaskStore;

  const route = new ActivatedRouteStub({ taskId: '123' });

  const tasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  const initialPayload: NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload = {
    payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PAYLOAD',
    enforcementResponseNotice: {
      type: 'PENALTY',
      file: 'uuid-1',
      comments: 'Existing comments',
    },
    nonComplianceAttachments: { 'uuid-1': 'existing-notice.pdf' },
    sectionsCompleted: { uploadEnforcementResponseNotice: 'COMPLETED' },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SummaryPageComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideZonelessChangeDetection(),
        { provide: ActivatedRoute, useValue: route },
        {
          provide: Router,
          useValue: {
            navigate: vi.fn(),
            url: '/test/summary',
            events: of({}),
            createUrlTree: () => ({}),
            serializeUrl: () => '',
          },
        },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Upload enforcement response notice' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT',
        payload: initialPayload,
      } as RequestTaskDTO,
      requestInfo: { accountId: 1 } as RequestInfoDTO,
    });
    store.setIsEditable(true);

    fixture = TestBed.createComponent(SummaryPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    tasksApiService.saveRequestTaskAction.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the summary heading and caption', () => {
    expect(getByText('Summary', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Enforcement response notice', fixture.nativeElement)).toBeTruthy();
  });

  it('should render a cca-summary element', () => {
    expect(fixture.nativeElement.querySelector('cca-summary')).toBeTruthy();
  });
});
