import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukDatePipe } from '@netz/common/pipes';
import { StatusPipe } from '@shared/pipes';

import { SubsistenceFeesMoAFacilityViewService } from 'cca-api';

@Component({
  selector: 'cca-marking-history',
  templateUrl: './marking-history.component.html',
  imports: [PageHeadingComponent, GovukDatePipe, StatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MarkingHistoryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly subsistenceFeesMoAFacilityViewService = inject(SubsistenceFeesMoAFacilityViewService);

  private readonly moaFacilityId = +this.activatedRoute.snapshot.paramMap.get('moaFacilityId');

  protected readonly details = toSignal(
    this.subsistenceFeesMoAFacilityViewService.getSubsistenceFeesMoaFacilityMarkingStatusHistoryInfo(
      this.moaFacilityId,
    ),
  );
}
