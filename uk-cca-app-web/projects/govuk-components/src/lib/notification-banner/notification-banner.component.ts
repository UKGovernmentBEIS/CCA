import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  computed,
  input,
  viewChild,
} from '@angular/core';

@Component({
  selector: 'govuk-notification-banner',
  standalone: true,
  templateUrl: './notification-banner.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationBannerComponent implements AfterViewInit {
  readonly type = input<'success' | 'neutral'>('neutral');
  readonly heading = input('', {
    transform: (value: string | undefined) => value ?? (this.type() === 'success' ? 'Success' : 'Important'),
  });

  readonly tabIndex = computed(() => (this.type() === 'success' ? -1 : null));

  private readonly bannerElement = viewChild<ElementRef<HTMLDivElement>>('banner');

  ngAfterViewInit() {
    if (this.tabIndex()) this.bannerElement().nativeElement.focus();
  }
}
