import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { first, switchMap, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { AuthStore, selectUserId } from '@netz/common/auth';
import { PendingButtonDirective } from '@netz/common/directives';
import { UserFullNamePipe } from '@netz/common/pipes';
import { ButtonDirective, PanelComponent, WarningTextComponent } from '@netz/govuk-components';
import { AuthService } from '@shared/services';

import { RegulatorAuthoritiesService } from 'cca-api';

import { saveNotFoundRegulatorError } from '../../errors/business-error';
import { DetailsStore } from '../details/details.store';

@Component({
  selector: 'cca-delete',
  templateUrl: './delete.component.html',
  imports: [
    WarningTextComponent,
    PanelComponent,
    UserFullNamePipe,
    ButtonDirective,
    RouterLink,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  private readonly store = inject(DetailsStore);
  private readonly authStore = inject(AuthStore);
  private readonly authService = inject(AuthService);
  private readonly regulatorAuthoritiesService = inject(RegulatorAuthoritiesService);
  private readonly route = inject(ActivatedRoute);
  private readonly businessErrorService = inject(BusinessErrorService);

  protected readonly regulator = this.store.state.user;
  protected readonly isConfirmationDisplayed = signal(false);

  deleteRegulator(): void {
    this.route.paramMap
      .pipe(
        first(),
        switchMap((paramMap) =>
          this.authStore.select(selectUserId)() === paramMap.get('userId')
            ? this.regulatorAuthoritiesService
                .deleteCurrentRegulatorUserByCompetentAuthority()
                .pipe(tap(() => this.authService.logout()))
            : this.regulatorAuthoritiesService.deleteRegulatorUserByCompetentAuthority(paramMap.get('userId')),
        ),
        catchBadRequest(ErrorCodes.AUTHORITY1003, () =>
          this.businessErrorService.showError(saveNotFoundRegulatorError),
        ),
      )
      .subscribe(() => this.isConfirmationDisplayed.set(true));
  }
}
