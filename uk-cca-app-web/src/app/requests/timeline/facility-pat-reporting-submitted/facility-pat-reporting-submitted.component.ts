import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import {
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';
import { FacilityPatReportingTypesPipe } from '@shared/pipes';
import { truncateText } from '@shared/utils';

import { FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload } from 'cca-api';

@Component({
  selector: 'cca-facility-pat-reporting-submitted',
  template: `
    <div class="govuk-!-width-two-thirds">
      <dl govuk-summary-list>
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Reporting year</dt>
          <dd govukSummaryListRowValue>{{ targetPeriodYear() }}</dd>
        </div>
      </dl>

      <h2 class="govuk-heading-m">Actions taken</h2>
      @for (action of savingActions(); track action; let i = $index) {
        <dl govuk-summary-list>
          <div govukSummaryListRow>
            <a class="govuk-link" [routerLink]="['facility-pat-reporting-submitted', i]"> Entry {{ i + 1 }} </a>
            <span>({{ action.actionCategoryType | facilityPatReportingTypes }})</span>
            <p class="pre-line">
              {{
                truncateText(
                  action?.actionCategoryType === 'NO_ACTION' ? action?.notes : action?.savingActionsImplemented,
                  120
                )
              }}
            </p>
          </div>
        </dl>
      } @empty {
        No actions provided
      }
    </div>
  `,
  imports: [
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    RouterLink,
    FacilityPatReportingTypesPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityPATReportingSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly payload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  ) as Signal<FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload>;

  protected readonly targetPeriodYear = computed(() => this.payload().targetPeriodYear);
  protected readonly savingActions = computed(() => this.payload().performanceData.savingActions);

  protected readonly truncateText = truncateText;
}
