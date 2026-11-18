import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'cca-email-sent',
  templateUrl: './email-sent.component.html',
  imports: [RouterModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailSentComponent {
  protected readonly email = input<string>(undefined);
  protected readonly retry = output();
}
