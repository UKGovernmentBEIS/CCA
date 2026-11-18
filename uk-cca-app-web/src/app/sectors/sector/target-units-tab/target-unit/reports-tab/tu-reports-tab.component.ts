import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';

import { PatReportComponent } from './pat/pat-report.component';
import { PerformanceReportComponent } from './performance-data/performance-report.component';

@Component({
  selector: 'cca-tu-reports-tab-component',
  templateUrl: './tu-reports-tab.component.html',
  imports: [PerformanceReportComponent, PatReportComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TuReportsTabComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  currentSection = 'performance'; // Default section

  constructor() {
    this.activatedRoute.queryParamMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      this.currentSection = params.get('section') || 'performance';
    });
  }

  updateSection(event, section: 'performance' | 'pat') {
    event.preventDefault();
    this.router.navigate([], {
      queryParams: { section: section },
      queryParamsHandling: 'merge',
      fragment: 'reports',
      relativeTo: this.activatedRoute,
    });
  }
}
