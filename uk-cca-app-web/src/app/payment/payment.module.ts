import { NgModule } from '@angular/core';

import { RelatedActionsComponent } from '@shared/components/related-actions/related-actions.component';
import { RelatedTasksComponent } from '@shared/components/related-tasks/related-tasks.component';
import { TaskHeaderInfoComponent } from '@shared/components/task-header-info/task-header-info.component';
import { TimelineComponent } from '@shared/components/timeline/timeline.component';
import { TimelineItemComponent } from '@shared/components/timeline/timeline-item.component';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { PendingButtonDirective } from '@shared/pending-button.directive';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { TimelineItemLinkPipe } from '@shared/pipes/timeline-item-link.pipe';
import { SharedModule } from '@shared/shared.module';
import { SummaryHeaderComponent } from '@shared/summary-header/summary-header.component';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { CancelledComponent } from './actions/cancelled/cancelled.component';
import { CompletedComponent } from './actions/completed/completed.component';
import { PaidComponent } from './actions/paid/paid.component';
import { ReceivedComponent } from './actions/received/received.component';
import { BankTransferComponent } from './make/bank-transfer/bank-transfer.component';
import { ConfirmationComponent } from './make/confirmation/confirmation.component';
import { DetailsComponent } from './make/details/details.component';
import { MarkPaidComponent } from './make/mark-paid/mark-paid.component';
import { NotSuccessComponent } from './make/not-success/not-success.component';
import { OptionsComponent } from './make/options/options.component';
import { PaymentRoutingModule } from './payment-routing.module';
import { MakePaymentHelpComponent } from './shared/components/make-payment-help/make-payment-help.component';
import { PaymentSummaryComponent } from './shared/components/payment-summary/payment-summary.component';
import { ReturnLinkComponent } from './shared/components/return-link/return-link.component';
import { PaymentMethodDescriptionPipe } from './shared/pipes/payment-method-description.pipe';
import { PaymentStatusPipe } from './shared/pipes/payment-status.pipe';
import { CancelComponent } from './track/cancel/cancel.component';
import { MarkPaidComponent as TrackMarkPaidComponent } from './track/mark-paid/mark-paid.component';
import { TrackComponent } from './track/track.component';

@NgModule({
  declarations: [
    BankTransferComponent,
    CancelComponent,
    CancelledComponent,
    CompletedComponent,
    ConfirmationComponent,
    DetailsComponent,
    MakePaymentHelpComponent,
    MarkPaidComponent,
    NotSuccessComponent,
    OptionsComponent,
    PaidComponent,
    PaymentMethodDescriptionPipe,
    PaymentStatusPipe,
    PaymentSummaryComponent,
    ReceivedComponent,
    ReturnLinkComponent,
    TrackComponent,
    TrackMarkPaidComponent,
  ],
  imports: [
    GovukDatePipe,
    PageHeadingComponent,
    PaymentRoutingModule,
    PendingButtonDirective,
    RelatedActionsComponent,
    RelatedTasksComponent,
    SharedModule,
    SummaryHeaderComponent,
    TaskHeaderInfoComponent,
    TimelineComponent,
    TimelineItemComponent,
    TimelineItemLinkPipe,
    WizardStepComponent,
  ],
})
export class PaymentModule {}
