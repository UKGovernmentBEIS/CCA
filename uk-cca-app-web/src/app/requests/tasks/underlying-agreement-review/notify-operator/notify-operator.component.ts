import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY, map } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CheckboxComponent, CheckboxesComponent, ErrorSummaryComponent, SelectComponent } from '@netz/govuk-components';
import {
  API_ERROR_FORM,
  ApiErrorFormModel,
  ApiErrorFormProvider,
  NOTIFY_OPERATOR_OF_DECISION_FORM,
  NotifyOperatorOfDecisionFormModel,
  NotifyOperatorOfDecisionFormProvider,
  setApiErrors,
  TasksApiService,
  toDecisionNotification,
  transformAccountReferenceData,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { NoticeRecipientsTypePipe } from '@shared/pipes';

import {
  CaExternalContactsService,
  NoticeRecipientDTO,
  NoticeRecipientsService,
  RegulatorAuthoritiesService,
  TasksService,
} from 'cca-api';

import { createNotifyOperatorActionDTO } from '../transform';
import { createProposedUnderlyingAgreementPayload } from '../utils';

@Component({
  selector: 'cca-notify-operator',
  templateUrl: './notify-operator.component.html',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    ReturnToTaskOrActionPageComponent,
    CheckboxComponent,
    CheckboxesComponent,
    ErrorSummaryComponent,
    SelectComponent,
    NoticeRecipientsTypePipe,
  ],
  providers: [NotifyOperatorOfDecisionFormProvider, ApiErrorFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotifyOperatorComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly caExternalContactsService = inject(CaExternalContactsService);
  private readonly noticeRecipientsService = inject(NoticeRecipientsService);
  private readonly tasksService = inject(TasksService);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly regulatorAuthoritiesService = inject(RegulatorAuthoritiesService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<NotifyOperatorOfDecisionFormModel>(NOTIFY_OPERATOR_OF_DECISION_FORM);
  protected readonly errorForm = inject<ApiErrorFormModel>(API_ERROR_FORM);
  protected readonly isErrorSummaryDisplayed = signal(false);

  private readonly requestInfo = this.store.select(requestTaskQuery.selectRequestInfo);
  private readonly resourceType = computed(() => this.requestInfo()?.resourceType);
  private readonly resource = computed(() => this.requestInfo()?.resources?.[this.resourceType()]);

  private readonly taskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
  private readonly defaultNoticeRecipients = toSignal(this.tasksService.getDefaultNoticeRecipients(this.taskId));

  private readonly sectorContact = computed(() =>
    this.defaultNoticeRecipients()?.find((nr) => nr.type === 'SECTOR_CONTACT'),
  );

  private readonly administrativeContact = computed(() =>
    this.defaultNoticeRecipients()?.find((nr) => nr.type === 'ADMINISTRATIVE_CONTACT'),
  );

  protected readonly externalContacts = toSignal(
    this.caExternalContactsService.getCaExternalContacts().pipe(map((r) => r.caExternalContacts)),
  );

  protected readonly regulatorAuthorities = toSignal(
    this.regulatorAuthoritiesService.getCaRegulators().pipe(
      map((r) =>
        r?.caUsers.map((rua) => ({
          text: `${rua.firstName} ${rua.lastName}`,
          value: rua.userId,
        })),
      ),
    ),
  );

  protected readonly additionalUsers = toSignal(
    this.noticeRecipientsService.getAdditionalNoticeRecipients(+this.resource()),
  );

  protected readonly defaultUsers = computed(() => {
    const recipients: NoticeRecipientDTO[] = [];

    const tudDecision = this.store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'))()
      ?.type;

    const accountReferenceData = this.store.select(underlyingAgreementQuery.selectAccountReferenceData)();

    const targetUnitDetails = this.store.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)();

    const originalResponsiblePerson = transformAccountReferenceData(accountReferenceData).responsiblePersonDetails;

    if (tudDecision === 'REJECTED') {
      recipients.push({
        email: originalResponsiblePerson.email,
        firstName: originalResponsiblePerson.firstName,
        lastName: originalResponsiblePerson.lastName,
        type: 'RESPONSIBLE_PERSON',
      });
    } else {
      recipients.push({
        email: targetUnitDetails.responsiblePersonDetails.email,
        firstName: targetUnitDetails.responsiblePersonDetails.firstName,
        lastName: targetUnitDetails.responsiblePersonDetails.lastName,
        type: 'RESPONSIBLE_PERSON',
      });
    }

    if (this.administrativeContact()) {
      recipients.push({
        email: this.administrativeContact().email,
        firstName: this.administrativeContact().firstName,
        lastName: this.administrativeContact().lastName,
        type: 'ADMINISTRATIVE_CONTACT',
      });
    }

    if (this.sectorContact()) {
      recipients.push({
        email: this.sectorContact().email,
        firstName: this.sectorContact().firstName,
        lastName: this.sectorContact().lastName,
        type: 'SECTOR_CONTACT',
      });
    }

    return recipients;
  });

  onSubmit() {
    const payload = this.store.select(underlyingAgreementQuery.selectPayload)();
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const notification = toDecisionNotification(this.form.value);
    const proposedUnderlyingAgreement = createProposedUnderlyingAgreementPayload(payload);

    const dto = createNotifyOperatorActionDTO(requestTaskId, notification, proposedUnderlyingAgreement);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .pipe(
        catchBadRequest([ErrorCodes.UNA1001, ErrorCodes.UNA1002], (res) => {
          setApiErrors(this.errorForm, res);
          this.isErrorSummaryDisplayed.set(true);
          return EMPTY;
        }),
      )
      .subscribe(() => {
        this.router.navigate(['./confirmation'], { relativeTo: this.activatedRoute });
      });
  }
}
