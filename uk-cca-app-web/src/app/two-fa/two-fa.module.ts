import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { BackToTopComponent } from '@shared/back-to-top/back-to-top.component';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { PendingButtonDirective } from '@shared/pending-button.directive';
import { SharedModule } from '@shared/shared.module';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { Change2faComponent } from './change-2fa/change-2fa.component';
import { Delete2faComponent } from './delete-2fa/delete-2fa.component';
import { InvalidCodeComponent } from './invalid-code/invalid-code.component';
import { RequestTwoFaResetComponent } from './request-two-fa-reset/request-two-fa-reset.component';
import { ResetTwoFaComponent } from './reset-two-fa/reset-two-fa.component';
import { TwoFaRoutingModule } from './two-fa-routing.module';

@NgModule({
  declarations: [
    Change2faComponent,
    Delete2faComponent,
    InvalidCodeComponent,
    RequestTwoFaResetComponent,
    ResetTwoFaComponent,
  ],
  imports: [
    BackToTopComponent,
    CommonModule,
    PageHeadingComponent,
    PendingButtonDirective,
    SharedModule,
    TwoFaRoutingModule,
    WizardStepComponent,
  ],
})
export class TwoFaModule {}
