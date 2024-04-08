import { NgModule } from '@angular/core';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { SummaryHeaderComponent } from '@shared/summary-header/summary-header.component';
import { UserInputComponent } from '@shared/user-input/user-input.component';

import { SharedModule } from '../shared/shared.module';
import { PasswordComponent } from '../shared-user/password/password.component';
import { ChoosePasswordComponent } from './choose-password/choose-password.component';
import { ContactDetailsComponent } from './contact-details/contact-details.component';
import { EmailComponent } from './email/email.component';
import { EmailConfirmedComponent } from './email-confirmed/email-confirmed.component';
import { InvalidEmailLinkComponent } from './invalid-email-link/invalid-email-link.component';
import { InvalidInvitationLinkComponent } from './invalid-invitation-link/invalid-invitation-link.component';
import { InvitationComponent } from './invitation/invitation.component';
import { RegistrationRoutingModule } from './registration-routing.module';
import { StartProcessComponent } from './start-process/start-process.component';
import { SuccessComponent } from './success/success.component';
import { UserRegistrationComponent } from './user-registration/user-registration.component';
import { VerificationSentComponent } from './verification-sent/verification-sent.component';

@NgModule({
  declarations: [
    ChoosePasswordComponent,
    ContactDetailsComponent,
    EmailComponent,
    EmailConfirmedComponent,
    InvalidEmailLinkComponent,
    InvalidInvitationLinkComponent,
    InvitationComponent,
    StartProcessComponent,
    SuccessComponent,
    UserRegistrationComponent,
    VerificationSentComponent,
  ],
  imports: [
    PageHeadingComponent,
    PasswordComponent,
    RegistrationRoutingModule,
    SharedModule,
    SummaryHeaderComponent,
    UserInputComponent,
  ],
})
export class RegistrationModule {}
