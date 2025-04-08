import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CheckboxComponent, CheckboxesComponent, SelectComponent } from '@netz/govuk-components';
import {
  NOTIFY_OPERATOR_OF_DECISION_FORM,
  NotifyOperatorOfDecisionFormModel,
  NotifyOperatorOfDecisionFormProvider,
  toDecisionNotification,
  transform,
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

import { UnderlyingAgreementVariationReviewTaskService } from '../services/underlying-agreement-variation-review-task.service';

@Component({
  selector: 'cca-notify-operator-variation',
  standalone: true,
  templateUrl: './notify-operator-variation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    ReturnToTaskOrActionPageComponent,
    CheckboxComponent,
    CheckboxesComponent,
    SelectComponent,
    NoticeRecipientsTypePipe,
  ],
  providers: [NotifyOperatorOfDecisionFormProvider],
})
export class NotifyOperatorVariationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly caExternalContactsService = inject(CaExternalContactsService);
  private readonly noticeRecipientsService = inject(NoticeRecipientsService);
  private readonly tasksService = inject(TasksService);
  private readonly regulatorAuthoritiesService = inject(RegulatorAuthoritiesService);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly form = inject<NotifyOperatorOfDecisionFormModel>(NOTIFY_OPERATOR_OF_DECISION_FORM);

  private readonly accountId = this.requestTaskStore.select(requestTaskQuery.selectRequestInfo)()?.accountId;
  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
  private readonly defaultNoticeRecipients = toSignal(this.tasksService.getDefaultNoticeRecipients(this.taskId));

  private readonly sectorContact = computed(() =>
    this.defaultNoticeRecipients()?.find((nr) => nr.type === 'SECTOR_CONTACT'),
  );

  readonly externalContacts = toSignal(
    this.caExternalContactsService.getCaExternalContacts().pipe(map((r) => r.caExternalContacts)),
  );

  readonly regulatorAuthorities = toSignal(
    this.regulatorAuthoritiesService.getCaRegulators().pipe(
      map((r) =>
        r?.caUsers.map((rua) => ({
          text: `${rua.firstName} ${rua.lastName}`,
          value: rua.userId,
        })),
      ),
    ),
  );

  readonly additionalUsers = toSignal(this.noticeRecipientsService.getAdditionalNoticeRecipients(this.accountId));

  readonly defaultUsers = computed(() => {
    const recipients: NoticeRecipientDTO[] = [];

    const tudDecision = this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'),
    )()?.type;

    const accountReferenceData = this.requestTaskStore.select(underlyingAgreementQuery.selectAccountReferenceData)();

    const targetUnitDetails = this.requestTaskStore.select(
      underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
    )();

    const originalResponsiblePerson = transform(accountReferenceData).responsiblePersonDetails;

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

    recipients.push({
      email: accountReferenceData.targetUnitAccountDetails.administrativeContactDetails.email,
      firstName: accountReferenceData.targetUnitAccountDetails.administrativeContactDetails.firstName,
      lastName: accountReferenceData.targetUnitAccountDetails.administrativeContactDetails.lastName,
      type: 'ADMINISTRATIVE_CONTACT',
    });

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
    (this.taskService as UnderlyingAgreementVariationReviewTaskService)
      .notifyOperator(toDecisionNotification(this.form.value))
      .subscribe(() => {
        this.router.navigate(['./confirmation'], { relativeTo: this.activatedRoute });
      });
  }
}
