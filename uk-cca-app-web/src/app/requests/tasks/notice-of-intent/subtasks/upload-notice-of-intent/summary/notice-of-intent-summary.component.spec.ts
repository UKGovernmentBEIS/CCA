import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText, queryByText } from '@testing';

import { NonComplianceNoticeOfIntentSubmitRequestTaskPayload } from 'cca-api';

import NoticeOfIntentSummaryComponent from './notice-of-intent-summary.component';

describe('NoticeOfIntentSummaryComponent', () => {
  let component: NoticeOfIntentSummaryComponent;
  let fixture: ComponentFixture<NoticeOfIntentSummaryComponent>;
  let store: RequestTaskStore;

  const initialPayload: NonComplianceNoticeOfIntentSubmitRequestTaskPayload = {
    payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD',
    noticeOfIntent: { file: 'uuid-1', comments: 'Existing comments' },
    nonComplianceAttachments: { 'uuid-1': 'existing-notice.pdf' },
    sectionsCompleted: { uploadNoticeOfIntent: 'COMPLETED' },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoticeOfIntentSummaryComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ taskId: '123' }) },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Upload notice of intent' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT',
        payload: initialPayload,
      } as any,
      requestInfo: { accountId: 1 } as any,
    });

    fixture = TestBed.createComponent(NoticeOfIntentSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render summary content with change links for editable tasks', () => {
    store.setIsEditable(true);
    fixture.detectChanges();

    expect(getByText('Summary', fixture.nativeElement)).toBeTruthy();
    expect(getByText('existing-notice.pdf', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Existing comments', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Change', fixture.nativeElement)).toBeTruthy();
  });

  it('should render summary content without change links for read-only tasks', () => {
    store.setIsEditable(false);
    fixture.detectChanges();

    expect(queryByText('Change', fixture.nativeElement)).toBeNull();
  });
});
