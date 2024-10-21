import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { first, switchMap, tap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { AuthStore, selectUserId } from '@netz/common/auth';
import { UserFullNamePipe } from '@netz/common/pipes';
import { ButtonDirective, LinkDirective, PanelComponent, WarningTextComponent } from '@netz/govuk-components';
import { PendingButtonDirective } from '@shared/directives';

import { RegulatorAuthoritiesService } from 'cca-api';

import { saveNotFoundRegulatorError } from '../../errors/business-error';
import { DetailsStore } from '../details/details.store';

@Component({
  selector: 'cca-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    WarningTextComponent,
    PanelComponent,
    UserFullNamePipe,
    ButtonDirective,
    LinkDirective,
    RouterLink,
    PendingButtonDirective,
  ],
  standalone: true,
})
export class DeleteComponent {
  private readonly store = inject(DetailsStore);
  private readonly authStore = inject(AuthStore);
  private readonly authService = inject(AuthService);
  private readonly regulatorAuthoritiesService = inject(RegulatorAuthoritiesService);
  private readonly route = inject(ActivatedRoute);
  private readonly businessErrorService = inject(BusinessErrorService);

  regulator = this.store.state.user;
  isConfirmationDisplayed = signal(false);

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
