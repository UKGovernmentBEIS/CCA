import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus } from '@requests/common';

import { underlyingAgreementActivationQuery } from '../una-activation.selectors';

@Component({
  selector: 'cca-underlying-agreement-activation-pre-content',
  template: `
    @if (isTaskCompleted() && isEditable()) {
      <button netzPendingButton govukButton type="button" (click)="onNotifyOperatorOfDecision()">
        Notify operator of decision
      </button>
    }
  `,
  imports: [ButtonDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UnderlyingAgreementActivationPreContentComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly isTaskCompleted = computed(
    () =>
      this.requestTaskStore.select(underlyingAgreementActivationQuery.selectSectionsCompleted)()[
        PROVIDE_EVIDENCE_SUBTASK
      ] === TaskItemStatus.COMPLETED,
  );

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  onNotifyOperatorOfDecision() {
    this.router.navigate(['underlying-agreement-activation', 'notify-operator'], {
      relativeTo: this.activatedRoute,
      replaceUrl: true,
    });
  }
}
