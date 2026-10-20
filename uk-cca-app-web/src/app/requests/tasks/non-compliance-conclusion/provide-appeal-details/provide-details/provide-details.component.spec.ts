import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { RequestTaskFileService } from '@shared/services';
import { getByText, queryByText } from '@testing';

import { NonComplianceConclusionSubmitRequestTaskPayload, RequestInfoDTO, RequestTaskDTO } from 'cca-api';

import { ProvideAppealDetailsStore } from '../+state';
import { ProvideAppealDetailsComponent } from './provide-details.component';
import { ProvideAppealDetailsFormProvider } from './provide-details-form.provider';

describe('ProvideAppealDetailsComponent', () => {
  let component: ProvideAppealDetailsComponent;
  let fixture: ComponentFixture<ProvideAppealDetailsComponent>;
  let store: RequestTaskStore;
  let appealDetailsStore: ProvideAppealDetailsStore;
  let router: Router;

  const route = new ActivatedRouteStub({ taskId: '123' });

  const requestTaskFileService = {
    buildFormControl: vi.fn().mockReturnValue(new FormControl([])),
  };

  const initialPayload: NonComplianceConclusionSubmitRequestTaskPayload = {
    payloadType: 'NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD',
    nonComplianceConclusion: {
      details: {
        complianceRestored: false,
        complianceRestoredDate: null,
        penaltyPaid: false,
        penaltyPaymentDate: null,
        comment: 'Some comments',
        penaltyOutcome: 'NONE',
      },
      withdrawNotice: null,
    },
    nonComplianceAttachments: {},
    sectionsCompleted: { 'provide-conclusion': 'IN_PROGRESS' },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProvideAppealDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        ProvideAppealDetailsStore,
        ProvideAppealDetailsFormProvider,
        { provide: ActivatedRoute, useValue: route },
        { provide: RequestTaskFileService, useValue: requestTaskFileService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Provide conclusion of non-compliance' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    appealDetailsStore = TestBed.inject(ProvideAppealDetailsStore);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    store.setRequestTaskItem({
      requestTask: {
        id: 123,
        type: 'NON_COMPLIANCE_CONCLUSION_SUBMIT',
        payload: initialPayload,
      } as RequestTaskDTO,
      requestInfo: { accountId: 1 } as RequestInfoDTO,
    });
    store.setIsEditable(true);

    fixture = TestBed.createComponent(ProvideAppealDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    requestTaskFileService.buildFormControl.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the provide appeal details step content', () => {
    expect(queryByText('Provide conclusion of non-compliance', fixture.nativeElement)).toBeNull();
    expect(getByText('Provide appeal details', fixture.nativeElement)).toBeTruthy();
    expect(getByText('When was the appeal registered?', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Upload appeal file (optional)', fixture.nativeElement)).toBeTruthy();
    expect(getByText('Comments (optional)', fixture.nativeElement)).toBeTruthy();
  });

  it('should reject appeal registration dates in the future', () => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);

    component['form'].controls.registrationDate.setValue(tomorrow);

    expect(component['form'].controls.registrationDate.errors?.invalidDate).toEqual(
      'The date must be today or in the past',
    );
  });

  it('should store appeal details and navigate to check your answers', () => {
    component['form'].setValue({
      registrationDate: new Date(2026, 0, 1),
      files: [{ uuid: 'uuid-1', file: { name: 'appeal.pdf' } as File }],
      comments: 'Appeal comments',
    });

    component.onSubmit();

    expect(appealDetailsStore.state).toEqual({
      appealDetails: {
        registrationDate: '2026-01-01',
        files: ['uuid-1'],
        comments: 'Appeal comments',
      },
      attachments: { 'uuid-1': 'appeal.pdf' },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });
});
