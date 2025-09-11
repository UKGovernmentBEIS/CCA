import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'cca-email-sent',
  templateUrl: './email-sent.component.html',
  standalone: true,
  imports: [RouterModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailSentComponent {
  @Input() email: string;
  @Output() readonly retry = new EventEmitter();
}
