import { Component, input, output } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'cca-email-sent',
  templateUrl: './email-sent.component.html',
  imports: [RouterModule],
})
export class EmailSentComponent {
  protected readonly email = input<string>(undefined);
  protected readonly retry = output();
}
