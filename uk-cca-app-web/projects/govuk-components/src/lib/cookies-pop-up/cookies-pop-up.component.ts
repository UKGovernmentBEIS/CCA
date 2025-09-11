import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'govuk-cookies-pop-up',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './cookies-pop-up.component.html',
  styleUrl: './cookies-pop-up.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CookiesPopUpComponent {
  readonly cookiesExpirationTime = input<string>();
  readonly cookiesAccepted = input<boolean>();
  readonly areBrowserCookiesEnabled = input<boolean>();

  readonly cookiesAcceptedEmitter = output<string>();

  show = false;

  cookiesNotAccepted() {
    return this.cookiesAccepted() === false;
  }

  acceptCookies() {
    this.show = true;
    this.cookiesAcceptedEmitter.emit(this.cookiesExpirationTime());
  }

  hideCookieMessage() {
    this.show = false;
  }

  goToSetPreferences() {
    location.href = '/cookies';
  }
}
