import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, PanelComponent, WarningTextComponent } from '@netz/govuk-components';

import { CaExternalContactsService } from 'cca-api';

import { saveNotFoundExternalContactError } from '../../errors/business-error';
import { ActiveExternalContactStore } from '../active-external-contact.store';

@Component({
  selector: 'cca-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [ButtonDirective, PanelComponent, RouterLink, WarningTextComponent, PendingButtonDirective],
})
export class DeleteComponent {
  private readonly externalContactsService = inject(CaExternalContactsService);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly store = inject(ActiveExternalContactStore);

  contact = this.store.state;

  isConfirmationDisplayed = signal(false);

  deleteExternalContact(): void {
    this.externalContactsService
      .deleteCaExternalContactById(this.contact.id)
      .pipe(
        catchBadRequest(ErrorCodes.EXTCONTACT1000, () =>
          this.businessErrorService.showError(saveNotFoundExternalContactError),
        ),
      )
      .subscribe(() => this.isConfirmationDisplayed.set(true));
  }
}
