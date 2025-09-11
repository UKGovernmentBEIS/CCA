import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'govuk-footer-meta-info',
  templateUrl: './meta-info.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MetaInfoComponent {}
