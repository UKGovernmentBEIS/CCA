import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PatReportComponent } from './pat/pat-report.component';
import { TargetPeriodReportComponent } from './target-period/target-period-report.component';

@Component({
  selector: 'cca-facility-reports-tab-component',
  templateUrl: './facility-reports-tab.component.html',
  imports: [TargetPeriodReportComponent, PatReportComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityReportsTabComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  currentSection = 'target-period'; // Default section

  constructor() {
    this.activatedRoute.queryParamMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      this.currentSection = params.get('section') || 'target-period';
    });
  }
}
