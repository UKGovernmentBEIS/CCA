import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map } from 'rxjs';

import { UIConfigurationService } from 'cca-api';

@Component({
  selector: 'cca-service-banner',
  templateUrl: './service-banner.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ServiceBannerComponent {
  private readonly configurationService = inject(UIConfigurationService);

  protected readonly notifications = toSignal(
    this.configurationService.getUIFlags().pipe(map((res) => res.notificationAlerts)),
  );
}
