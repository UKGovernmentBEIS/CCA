import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { combineLatest, first, map, switchMap, tap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore, selectUserId } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';

import { ButtonDirective, LinkDirective, PanelComponent, WarningTextComponent } from 'govuk-components';

import { RegulatorAuthoritiesService } from 'cca-api';

import { saveNotFoundRegulatorError } from '../errors/business-error';

@Component({
  selector: 'cca-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [WarningTextComponent, PanelComponent, UserFullNamePipe, ButtonDirective, LinkDirective, RouterLink],
  standalone: true,
})
export class DeleteComponent {
  regulator = toSignal(this.route.data.pipe(map(({ user }) => user)));
  isConfirmationDisplayed = signal(false);

  constructor(
    private readonly authStore: AuthStore,
    private readonly authService: AuthService,
    private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  deleteRegulator(): void {
    combineLatest([this.authStore.pipe(selectUserId), this.route.paramMap])
      .pipe(
        first(),
        switchMap(([userId, paramMap]) =>
          userId === paramMap.get('userId')
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
