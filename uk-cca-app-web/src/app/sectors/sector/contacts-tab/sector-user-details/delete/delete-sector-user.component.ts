import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Observable, tap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { AuthStore, selectUserId } from '@netz/common/auth';
import { UserFullNamePipe } from '@netz/common/pipes';
import { ButtonDirective, LinkDirective, PanelComponent, WarningTextComponent } from '@netz/govuk-components';
import { PendingButtonDirective } from '@shared/directives';

import { SectorAssociationAuthoritiesService } from 'cca-api';

import { saveNotFoundUserError } from 'src/app/sectors/error/business-error';

import { SectorRouteData } from '../../../types';

@Component({
  selector: 'cca-delete-sector-user',
  templateUrl: './delete-sector-user.component.html',
  standalone: true,
  imports: [
    WarningTextComponent,
    PanelComponent,
    UserFullNamePipe,
    ButtonDirective,
    LinkDirective,
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
  readonly sectorId = +this.route.snapshot.paramMap.get('sectorId');

  sectorUser = (this.route.snapshot.data as SectorRouteData)?.sectorUserDetails;

  isConfirmationDisplayed = signal(false);

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
