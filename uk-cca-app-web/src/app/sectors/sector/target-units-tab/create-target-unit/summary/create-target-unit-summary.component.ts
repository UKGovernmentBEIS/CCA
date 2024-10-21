import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { ButtonDirective, LinkDirective } from '@netz/govuk-components';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';
import { toTargetUnitCreateSummaryData } from '@shared/utils';

import { CcaRequestsService } from 'cca-api';

import { targetUnitCreationError } from '../../error/business-error';
import { CreateTargetUnitStore } from '../create-target-unit.store';

@Component({
  selector: 'cca-target-unit-creation-summary',
  templateUrl: './create-target-unit-summary.component.html',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, ButtonDirective, RouterLink, LinkDirective, PendingButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateTargetUnitSummaryComponent {
  private readonly ccaRequestsService = inject(CcaRequestsService);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly store = inject(CreateTargetUnitStore);
  readonly summaryData = toTargetUnitCreateSummaryData(this.store.state);

  onSubmitTargetUnitAccountCreation() {
    this.ccaRequestsService
      .processCcaRequestCreateAction(+this.route.snapshot.paramMap.get('sectorId'), {
        requestType: 'TARGET_UNIT_ACCOUNT_CREATION',
        requestCreateActionPayload: this.store.getSubmitPayload(),
      })
      .pipe(
        catchBadRequest([ErrorCodes.FORM1001, ErrorCodes.NOTFOUND1001], () =>
          this.businessErrorService.showError(targetUnitCreationError),
        ),
      )
      .subscribe(() =>
        this.router.navigate(['../confirmation'], {
          relativeTo: this.route,
          replaceUrl: true,
        }),
      );
  }
}
