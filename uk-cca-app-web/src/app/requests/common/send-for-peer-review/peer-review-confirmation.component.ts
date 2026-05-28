import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

import { AssigneeUserInfoDTO } from 'cca-api';

@Component({
  selector: 'cca-peer-review-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel
          >{{ confirmationPrefix() }} {{ regulatorUser().firstName }} {{ regulatorUser().lastName }} for peer
          review</govuk-panel
        >

        <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true"> Return to: dashboard </a>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewConfirmationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly routeData = signal(this.activatedRoute.snapshot.data);
  private readonly routeParamMap = signal(this.activatedRoute.snapshot.paramMap);
  private readonly parentRouteData = signal(
    this.activatedRoute.parent?.snapshot.data ?? this.activatedRoute.snapshot.data,
  );

  protected readonly confirmationPrefix = computed(() => this.routeData()['confirmationPrefix'] ?? 'Sent to');
  private readonly selectedAssigneeId = computed(() => this.routeParamMap().get('assigneeId'));
  private readonly candidateAssignees = computed(
    () => (this.parentRouteData()['candidateAssignees'] as AssigneeUserInfoDTO[]) ?? [],
  );
  protected readonly regulatorUser = computed(
    () =>
      this.candidateAssignees().find((assignee) => assignee.id === this.selectedAssigneeId()) ||
      ({} as AssigneeUserInfoDTO),
  );
}
