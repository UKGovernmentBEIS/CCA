import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'cca-email-sent',
  templateUrl: './email-sent.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [RouterModule],
})
export class EmailSentComponent {
  @Input() email: string;
  @Output() readonly retry = new EventEmitter();
}
