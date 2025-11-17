import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

import { AssigneeUserInfoDTO } from 'cca-api';

@Component({
  selector: 'cca-peer-review-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>Sent to {{ regulatorUser.firstName }} {{ regulatorUser.lastName }} for peer review</govuk-panel>

        <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true"> Return to: dashboard </a>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewConfirmationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly selectedAssigneeId = this.activatedRoute.snapshot.paramMap.get('assigneeId');
  private readonly candidateAssignees = this.activatedRoute.parent?.snapshot.data[
    'candidateAssignees'
  ] as AssigneeUserInfoDTO[];
  protected readonly regulatorUser =
    this.candidateAssignees?.find((assignee) => assignee.id === this.selectedAssigneeId) || ({} as AssigneeUserInfoDTO);
}
