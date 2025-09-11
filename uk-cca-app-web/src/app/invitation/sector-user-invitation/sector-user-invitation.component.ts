import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { BreadcrumbService } from '@netz/common/navigation';
import {
  ButtonDirective,
  ErrorSummaryComponent,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { PhoneInputComponent } from '@shared/components';

import { SectorUserInvitationStore } from './sector-user-invitation.store';
import {
  SECTOR_USER_INVITATION_FORM,
  SectorUserInvitationFormProvider,
  SectorUserInviteFormModel,
} from './sector-user-invitation-form.provider';

@Component({
  selector: 'cca-sector-user-invitation',
  templateUrl: './sector-user-invitation.component.html',
  standalone: true,
  imports: [
    PageHeadingComponent,
    ErrorSummaryComponent,
    ReactiveFormsModule,
    ButtonDirective,
    TextInputComponent,
    PhoneInputComponent,
    RadioComponent,
    RadioOptionComponent,
    PendingButtonDirective,
  ],
  providers: [SectorUserInvitationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorUserInvitationComponent implements OnInit {
  private readonly breadcrumbsService = inject(BreadcrumbService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(SectorUserInvitationStore);

  readonly form = inject<FormGroup<SectorUserInviteFormModel>>(SECTOR_USER_INVITATION_FORM);

  protected readonly isErrorSummaryDisplayed = signal(false);

  protected readonly contactTypeOptions: { text: string; value: 'SECTOR_ASSOCIATION' | 'CONSULTANT' }[] = [
    {
      text: 'Sector association',
      value: 'SECTOR_ASSOCIATION',
    },
    { text: 'Consultant', value: 'CONSULTANT' },
  ];

  ngOnInit() {
    this.breadcrumbsService.show([
      {
        text: 'Home',
        link: ['dashboard'],
      },
    ]);
  }

  onSubmitSectorUserInvitationDetails() {
    if (this.form.invalid) {
      this.isErrorSummaryDisplayed.set(true);
    } else {
      this.store.updateState({ ...this.store.state, ...this.form.value });

      const change = this.activatedRoute.snapshot.queryParamMap.get('change') === 'true';
      this.router.navigate(change ? ['summary'] : ['create-password'], { relativeTo: this.activatedRoute });
    }
  }
}
