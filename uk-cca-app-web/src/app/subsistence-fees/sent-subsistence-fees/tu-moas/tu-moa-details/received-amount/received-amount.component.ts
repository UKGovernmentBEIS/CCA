import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukDatePipe } from '@netz/common/pipes';
import {
  DetailsComponent,
  GovukSelectOption,
  SelectComponent,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TextareaComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';

import { ReceivedAmountStore } from './received-amount.store';
import {
  RECEIVED_AMOUNT_FORM,
  ReceivedAmountFormModel,
  ReceivedAmountFormProvider,
} from './received-amount-form.provider';

@Component({
  selector: 'cca-received-amount',
  templateUrl: './received-amount.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DecimalPipe,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    PageHeadingComponent,
    DetailsComponent,
    GovukDatePipe,
    RouterLink,
    WizardStepComponent,
    SelectComponent,
    TextInputComponent,
    TextareaComponent,
    MultipleFileInputComponent,
  ],
  providers: [ReceivedAmountFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReceivedAmountComponent implements OnInit {
  protected readonly router = inject(Router);
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly receivedAmountStore = inject(ReceivedAmountStore);

  protected readonly state = this.receivedAmountStore.stateAsSignal;
  protected readonly moaId = +this.activatedRoute.snapshot.paramMap.get('moaId');

  protected readonly changeTypeOptions: GovukSelectOption[] = [
    { text: 'Add', value: 'add' },
    { text: 'Subtract', value: 'subtract' },
  ];

  protected readonly form = inject<ReceivedAmountFormModel>(RECEIVED_AMOUNT_FORM);

  ngOnInit() {
    this.receivedAmountStore.getAndSetReceivedAmount(this.moaId);
  }

  onSubmit() {
    this.receivedAmountStore.updateState({
      changeType: this.form.value.changeType,
      details: {
        transactionAmount:
          this.form.value.changeType === 'subtract'
            ? `-${this.form.value.transactionAmount}`
            : `+${this.form.value.transactionAmount}`,
        comments: this.form.value.comments,
        evidenceFiles: fileUtils.toAttachments(this.form.value.evidenceFiles) ?? {},
      },
    });

    this.router.navigate(['check-your-answers'], { relativeTo: this.activatedRoute });
  }
}
