import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { BehaviorSubject, first, map, Observable, switchMap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { LinkDirective, PanelComponent, WarningTextComponent } from 'govuk-components';

import { CaExternalContactDTO, CaExternalContactsService } from 'cca-api';

import { saveNotFoundExternalContactError } from '../../errors/business-error';

@Component({
  selector: 'cca-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [AsyncPipe, PanelComponent, RouterLink, WarningTextComponent, LinkDirective],
})
export class DeleteComponent {
  contact$: Observable<CaExternalContactDTO> = this.route.data.pipe(map((x) => x?.contact));
  isConfirmationDisplayed$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly externalContactsService: CaExternalContactsService,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  deleteExternalContact(): void {
    this.contact$
      .pipe(
        first(),
        switchMap((contact) => this.externalContactsService.deleteCaExternalContactById(contact.id)),
        catchBadRequest(ErrorCodes.EXTCONTACT1000, () =>
          this.businessErrorService.showError(saveNotFoundExternalContactError),
        ),
      )
      .subscribe(() => this.isConfirmationDisplayed$.next(true));
  }
}
