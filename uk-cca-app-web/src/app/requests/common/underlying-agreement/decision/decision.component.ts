import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { RadioComponent, RadioOptionComponent, TextareaComponent } from '@netz/govuk-components';
import { MultipleFileInputComponent } from '@shared/components';
import { existingControlContainer } from '@shared/providers';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-decision',
  template: `
    <h2 class="govuk-heading-m">What is your decision on the information submitted?</h2>
    <span class="govuk-hint">Use your judgement to make minor changes within the details if needed</span>
    <div govuk-radio formControlName="type">
      <govuk-radio-option label="Accepted" value="ACCEPTED" />
      <govuk-radio-option label="Rejected" value="REJECTED" />
    </div>
    <div govuk-textarea label="Notes (optional)" formControlName="notes"></div>
    <cca-multiple-file-input
      formControlName="files"
      [baseDownloadUrl]="downloadUrl"
      label="Upload evidence (optional)"
    />
  `,
  imports: [
    RadioComponent,
    TextareaComponent,
    RadioOptionComponent,
    FormsModule,
    ReactiveFormsModule,
    MultipleFileInputComponent,
  ],
  viewProviders: [existingControlContainer],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DecisionComponent {
  private readonly store = inject(RequestTaskStore);

  private readonly taskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
  protected readonly downloadUrl = generateDownloadUrl(this.taskId.toString());
}
