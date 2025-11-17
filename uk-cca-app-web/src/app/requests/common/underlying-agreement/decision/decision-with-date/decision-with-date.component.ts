import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  CheckboxComponent,
  CheckboxesComponent,
  ConditionalContentDirective,
  DateInputComponent,
  DetailsComponent,
  RadioComponent,
  RadioOptionComponent,
  TextareaComponent,
} from '@netz/govuk-components';
import { MultipleFileInputComponent } from '@shared/components';
import { existingControlContainer } from '@shared/providers';
import { generateDownloadUrl } from '@shared/utils';

import { underlyingAgreementQuery } from '../../+state';

@Component({
  selector: 'cca-decision-with-date',
  templateUrl: './decision-with-date.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    RadioComponent,
    TextareaComponent,
    RadioOptionComponent,
    FormsModule,
    ReactiveFormsModule,
    MultipleFileInputComponent,
    ConditionalContentDirective,
    CheckboxComponent,
    CheckboxesComponent,
    DateInputComponent,
    CheckboxesComponent,
    DetailsComponent,
  ],
  viewProviders: [existingControlContainer],
})
export class DecisionWithDateComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
  protected readonly downloadUrl = generateDownloadUrl(this.taskId.toString());
  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly isNewFacility = computed(
    () => this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))().status === 'NEW',
  );
}
