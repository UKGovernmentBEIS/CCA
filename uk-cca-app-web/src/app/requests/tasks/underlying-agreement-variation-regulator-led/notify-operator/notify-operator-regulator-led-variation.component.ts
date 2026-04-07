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
  toCcaDecisionNotification,
  underlyingAgreementQuery,
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

@Component({
  selector: 'cca-notify-operator-regulator-led-variation',
  templateUrl: './notify-operator-regulator-led-variation.component.html',
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
export class NotifyOperatorRegulatorLedVariationComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly caExternalContactsService = inject(CaExternalContactsService);
  private readonly noticeRecipientsService = inject(NoticeRecipientsService);
  private readonly tasksService = inject(TasksService);
  private readonly regulatorAuthoritiesService = inject(RegulatorAuthoritiesService);
  private readonly tasksApiService = inject(TasksApiService);
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

    const targetUnitDetails = this.store.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)();

    recipients.push({
      email: targetUnitDetails.responsiblePersonDetails.email,
      firstName: targetUnitDetails.responsiblePersonDetails.firstName,
      lastName: targetUnitDetails.responsiblePersonDetails.lastName,
      type: 'RESPONSIBLE_PERSON',
    });

    const administrativeContact = this.defaultNoticeRecipients()?.find(
      (nr: NoticeRecipientDTO) => nr.type === 'ADMINISTRATIVE_CONTACT',
    );
    if (administrativeContact) {
      recipients.push({
        email: administrativeContact.email,
        firstName: administrativeContact.firstName,
        lastName: administrativeContact.lastName,
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
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const notification = toCcaDecisionNotification(this.form.value);
    const dto = createNotifyOperatorActionDTO(requestTaskId, notification);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .pipe(
        catchBadRequest(ErrorCodes.UNAV1004, (res) => {
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
