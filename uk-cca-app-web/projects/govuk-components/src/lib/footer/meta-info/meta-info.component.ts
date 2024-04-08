import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'govuk-footer-meta-info',
  standalone: true,
  templateUrl: './meta-info.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MetaInfoComponent {}
