import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'govuk-footer',
  templateUrl: './footer.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FooterComponent {}
