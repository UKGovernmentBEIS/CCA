import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { TargetPeriodReportComponent } from './target-period/target-period-report.component';

@Component({
  selector: 'cca-facility-reports-tab-component',
  templateUrl: './facility-reports-tab.component.html',
  imports: [TargetPeriodReportComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityReportsTabComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  currentSection = 'target-period'; // Default section

  constructor() {
    this.activatedRoute.queryParamMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      this.currentSection = params.get('section') || 'target-period';
    });
  }

  updateSection(event: Event, section: 'target-period' | 'pat') {
    event.preventDefault();
    this.router.navigate([], {
      queryParams: { section: section },
      queryParamsHandling: 'merge',
      fragment: 'reports',
      relativeTo: this.activatedRoute,
    });
  }
}
