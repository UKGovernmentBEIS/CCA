import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PerformanceReportComponent } from './performance-report/performance-report.component';

@Component({
  selector: 'cca-tu-reports-tab-component',
  templateUrl: './tu-reports-tab.component.html',
  standalone: true,
  imports: [PerformanceReportComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TuReportsTabComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  currentSection = 'performance'; // Default section

  ngOnInit() {
    this.activatedRoute.queryParamMap.subscribe((params) => {
      this.currentSection = params.get('section') || 'performance';
    });
  }

  updateSection(event, section: string) {
    event.preventDefault();
    this.router.navigate([], {
      queryParams: { section: section },
      queryParamsHandling: 'merge',
      fragment: 'reports',
    });
  }
}
