import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { GovukDatePipe } from '@netz/common/pipes';
import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent, SummaryFactory } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { NonComplianceAppealOutcomeDetails, NonComplianceAppealOutcomeSubmittedRequestActionPayload } from 'cca-api';

const TRIBUNAL_DECISION_LABELS: Record<NonComplianceAppealOutcomeDetails['tribunalDecision'], string> = {
  APPEAL_ALLOWED: 'Yes',
  APPEAL_DISMISSED: 'No',
};

@Component({
  selector: 'cca-appeal-outcome-provided',
  template: `<cca-summary [data]="data()" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppealOutcomeProvidedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(requestActionQuery.selectActionPayload);

  protected readonly data = computed(() => {
    const payload = this.actionPayload() as NonComplianceAppealOutcomeSubmittedRequestActionPayload;
    const datePipe = new GovukDatePipe();

    return new SummaryFactory()
      .addSection('Appeal outcome details')
      .addRow('Was the appeal successful?', TRIBUNAL_DECISION_LABELS[payload.appealOutcome?.tribunalDecision])
      .addRow('Date of appeal outcome', datePipe.transform(payload.appealOutcome?.appealOutcomeDate, 'date'))
      .addFileListRow(
        'Uploaded files',
        fileUtils.toDownloadableFiles(
          fileUtils.extractAttachments([payload.appealOutcome?.file], payload.nonComplianceAttachments ?? {}),
          './file-download',
        ),
      )
      .addTextAreaRow('Comments', payload.appealOutcome?.comments ?? '')
      .create();
  });
}
