import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { SummaryComponent } from '@shared/components';

import { FacilityInfoViewService } from 'cca-api';

import { toCertificationStatusSummaryData } from './certification-status-summary-data';

@Component({
  selector: 'cca-certification-status-tab',
  template: `<cca-summary [data]="data()" />`,
  imports: [SummaryComponent],
})
export class CertificationStatusTabComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly facilityInfoViewService = inject(FacilityInfoViewService);
  private readonly authStore = inject(AuthStore);

  private readonly facilityId = +this.activatedRoute.snapshot.paramMap.get('facilityId');
  private readonly roleType = this.authStore.select(selectUserRoleType);

  private readonly certifications = toSignal(this.facilityInfoViewService.getFacilityCertifications(this.facilityId), {
    initialValue: [],
  });

  protected readonly data = computed(() => toCertificationStatusSummaryData(this.certifications(), this.roleType()));
}
