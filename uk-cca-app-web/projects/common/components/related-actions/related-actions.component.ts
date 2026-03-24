import { ChangeDetectionStrategy, Component, inject, input, OnChanges, SimpleChanges } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { RequestTaskDTO, RequestTaskItemDTO } from 'cca-api';

import { RelatedActionsMap, TASK_RELATED_ACTIONS_MAP } from './related-actions.providers';

@Component({
  selector: 'netz-related-actions',
  template: `
    <aside class="app-related-items" role="complementary">
      <h2 class="govuk-heading-m" id="subsection-title">Related actions</h2>
      <nav role="navigation" aria-labelledby="subsection-title">
        <ul class="govuk-list govuk-!-font-size-16">
          @for (action of relatedActions; track action) {
            <li>
              <a [routerLink]="action.link" class="govuk-link" [relativeTo]="route">{{ action.text }}</a>
            </li>
          }
        </ul>
      </nav>
    </aside>
  `,
  styleUrl: './related-actions.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
})
export class RelatedActionsComponent implements OnChanges {
  private readonly actionsMap = inject<RelatedActionsMap>(TASK_RELATED_ACTIONS_MAP);
  protected readonly route = inject(ActivatedRoute);

  protected readonly allowedRequestTaskActions = input.required<RequestTaskItemDTO['allowedRequestTaskActions']>();
  protected readonly taskId = input.required<RequestTaskDTO['id']>();
  protected readonly showReassignAction = input(false);
  protected readonly reassignAction = input({ text: 'Reassign task', link: ['change-assignee'] });

  protected relatedActions: { text: string; link: string[] }[];

  ngOnChanges(changes: SimpleChanges): void {
    if ('allowedRequestTaskActions' in changes) {
      const actions = changes.allowedRequestTaskActions.currentValue as RequestTaskItemDTO['allowedRequestTaskActions'];
      this.relatedActions = actions
        .filter((action) => action in this.actionsMap)
        .map((action) => {
          const path = this.actionsMap[action].path;
          return {
            text: this.actionsMap[action].text,
            link: typeof path === 'function' ? path(this.taskId()) : path,
          };
        });

      if (this.showReassignAction()) {
        const { text, link } = this.reassignAction();
        this.relatedActions = [{ text, link }, ...this.relatedActions];
      }
    }
  }
}
