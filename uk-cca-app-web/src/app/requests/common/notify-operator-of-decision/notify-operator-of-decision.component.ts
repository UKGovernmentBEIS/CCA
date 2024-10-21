import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CheckboxComponent, CheckboxesComponent, SelectComponent } from '@netz/govuk-components';
import { existingControlContainer } from '@shared/providers';

import { CaExternalContactsService, NoticeRecipientsService, RegulatorAuthoritiesService, TasksService } from 'cca-api';

import { NoticeRecipientsTypePipe } from '../pipes';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'cca-notify-operator-of-decision',
  templateUrl: './notify-operator-of-decision.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, CheckboxComponent, CheckboxesComponent, SelectComponent, NoticeRecipientsTypePipe],
  viewProviders: [existingControlContainer],
})
export class NotifyOperatorOfDecisionComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly caExternalContactsService = inject(CaExternalContactsService);
  private readonly noticeRecipientsService = inject(NoticeRecipientsService);
  private readonly tasksService = inject(TasksService);
  private readonly regulatorAuthoritiesService = inject(RegulatorAuthoritiesService);

  private readonly accountId = this.requestTaskStore.select(requestTaskQuery.selectRequestInfo)()?.accountId;
  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

  protected readonly defaultUsers = toSignal(this.tasksService.getDefaultNoticeRecipients(this.taskId));

  protected readonly additionalUsers = toSignal(
    this.noticeRecipientsService.getAdditionalNoticeRecipients(this.accountId),
  );

  private readonly externalContacts = toSignal(this.caExternalContactsService.getCaExternalContacts());
  protected readonly caExternalContacts = computed(() => this.externalContacts()?.caExternalContacts);

  private readonly regulatorAuthorities = toSignal(this.regulatorAuthoritiesService.getCaRegulators());
  protected readonly transformedRegulatorAuthorities = computed(() =>
    this.regulatorAuthorities()?.caUsers.map((rua) => ({
      text: `${rua.firstName} ${rua.lastName}`,
      value: rua.userId,
    })),
  );
}
