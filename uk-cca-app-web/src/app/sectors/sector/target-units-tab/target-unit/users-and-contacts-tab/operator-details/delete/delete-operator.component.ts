import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PendingButtonDirective } from '@netz/common/directives';
import { UserFullNamePipe } from '@netz/common/pipes';
import { PendingRequestService } from '@netz/common/services';
import { ButtonDirective, PanelComponent, WarningTextComponent } from '@netz/govuk-components';

import { OperatorAuthoritiesService } from 'cca-api';

import { saveNotFoundUserError } from '../../../../../../error/business-error';
import { ActiveOperatorStore } from '../active-operator.store';

@Component({
  selector: 'cca-delete-operator',
  templateUrl: './delete-operator.component.html',
  standalone: true,
  imports: [
    WarningTextComponent,
    PanelComponent,
    RouterLink,
    UserFullNamePipe,
    ButtonDirective,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteOperatorComponent {
  private readonly pendingRequestService = inject(PendingRequestService);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly operatorAuthoritiesService = inject(OperatorAuthoritiesService);
  private readonly route = inject(ActivatedRoute);
  private readonly store = inject(ActiveOperatorStore);

  private readonly accountId = this.route.snapshot.paramMap.get('targetUnitId');
  private readonly userId = this.route.snapshot.paramMap.get('userId');

  readonly operator = this.store.state.details;

  isConfirmationDisplayed = signal(false);

  onDeleteOperator() {
    this.operatorAuthoritiesService
      .deleteAccountOperatorAuthority(+this.accountId, this.userId)
      .pipe(
        this.pendingRequestService.trackRequest(),
        catchBadRequest(ErrorCodes.AUTHORITY1004, () => this.businessErrorService.showError(saveNotFoundUserError)),
      )
      .subscribe(() => this.isConfirmationDisplayed.set(true));
  }
}
