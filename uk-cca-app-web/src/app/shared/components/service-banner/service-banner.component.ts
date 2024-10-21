import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map } from 'rxjs';

import { UIConfigurationService } from 'cca-api';

@Component({
  selector: 'cca-service-banner',
  standalone: true,
  templateUrl: './service-banner.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ServiceBannerComponent {
  configurationService = inject(UIConfigurationService);

  notifications = toSignal(this.configurationService.getUIFlags().pipe(map((res) => res.notificationAlerts)));
}
