import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { DocumentTemplateDTO } from 'cca-api';

@Component({
  selector: 'cca-document-template-overview',
  templateUrl: './document-template-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DocumentTemplateOverviewComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  documentTemplate$: Observable<DocumentTemplateDTO> = this.route.data.pipe(map((data) => data?.documentTemplate));

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}
}
