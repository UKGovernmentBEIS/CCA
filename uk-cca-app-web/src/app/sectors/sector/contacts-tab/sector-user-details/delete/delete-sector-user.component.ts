import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Observable, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { AuthStore, selectUserId } from '@netz/common/auth';
import { PendingButtonDirective } from '@netz/common/directives';
import { UserFullNamePipe } from '@netz/common/pipes';
import { ButtonDirective, PanelComponent, WarningTextComponent } from '@netz/govuk-components';
import { AuthService } from '@shared/services';

import { SectorAssociationAuthoritiesService } from 'cca-api';

import { saveNotFoundUserError } from '../../../../error/business-error';
import { SectorRouteData } from '../../../types';

@Component({
  selector: 'cca-delete-sector-user',
  templateUrl: './delete-sector-user.component.html',
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
export class DeleteSectorUserComponent {
  private readonly authStore = inject(AuthStore);
  private readonly authService = inject(AuthService);
  private readonly sectorAssociationAuthoritiesService = inject(SectorAssociationAuthoritiesService);
  private readonly route = inject(ActivatedRoute);
  private readonly businessErrorService = inject(BusinessErrorService);

  private readonly sectorUserId = this.route.snapshot.paramMap.get('sectorUserId');
  protected readonly sectorId = +this.route.snapshot.paramMap.get('sectorId');

  protected readonly sectorUser = (this.route.snapshot.data as SectorRouteData)?.sectorUserDetails;

  protected readonly isConfirmationDisplayed = signal(false);

  onDeleteSectorUser(): void {
    let sectorAssociationAuthoritiesObs: Observable<void>;

    if (this.authStore.select(selectUserId)() === this.sectorUserId) {
      sectorAssociationAuthoritiesObs = this.sectorAssociationAuthoritiesService
        .deleteCurrentSectorUser(this.sectorId)
        .pipe(tap(() => this.authService.logout()));
    } else {
      sectorAssociationAuthoritiesObs = this.sectorAssociationAuthoritiesService.deleteSectorUser(
        this.sectorUserId,
        this.sectorId,
      );
    }

    sectorAssociationAuthoritiesObs
      .pipe(catchBadRequest(ErrorCodes.SECTOR1001, () => this.businessErrorService.showError(saveNotFoundUserError)))
      .subscribe(() => this.isConfirmationDisplayed.set(true));
  }
}
