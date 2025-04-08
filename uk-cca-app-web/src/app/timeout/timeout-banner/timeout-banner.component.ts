import { AsyncPipe, DOCUMENT } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  ElementRef,
  Inject,
  Input,
  OnInit,
  Renderer2,
  viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { PageHeadingComponent } from '@netz/common/components';
import { ButtonDirective } from '@netz/govuk-components';
import { SecondsToMinutesPipe } from '@shared/pipes';

import { TimeoutBannerService } from './timeout-banner.service';

@Component({
  selector: 'cca-timeout-banner',
  standalone: true,
  templateUrl: './timeout-banner.component.html',
  styleUrl: './timeout-banner.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent, AsyncPipe, SecondsToMinutesPipe, ButtonDirective],
})
export class TimeoutBannerComponent implements OnInit {
  @Input() timeOffsetSeconds: number;
  readonly modal = viewChild<ElementRef<HTMLDialogElement>>('modal');

  private overlayClass = 'govuk-timeout-warning-overlay';
  private lastFocusedElement = null;

  constructor(
    @Inject(DOCUMENT) private readonly document: Document,
    readonly timeoutBannerService: TimeoutBannerService,
    private readonly renderer: Renderer2,
    private readonly destroy$: DestroyRef,
  ) {}

  ngOnInit(): void {
    this.timeoutBannerService.isVisible$.pipe(takeUntilDestroyed(this.destroy$)).subscribe((isVisible) => {
      isVisible ? this.showDialog() : this.hideDialog();
    });
  }
  isDialogOpen(): boolean {
    return this.modal().nativeElement && this.modal().nativeElement.getAttribute('open') === '';
  }

  showDialog(): void {
    if (!this.isDialogOpen()) {
      this.renderer.addClass(this.document.body, this.overlayClass);
      this.saveLastFocusedElement();
      this.modal().nativeElement.showModal();
      this.modal().nativeElement.setAttribute('tabindex', '-1');
      this.modal().nativeElement.focus();
    }
  }

  hideDialog(): void {
    if (this.isDialogOpen()) {
      this.renderer.removeClass(this.document.body, this.overlayClass);
      this.modal().nativeElement.removeAttribute('tabindex');
      this.modal().nativeElement.close();
      this.setFocusOnLastFocusedElement();
    }
  }

  saveLastFocusedElement(): void {
    this.lastFocusedElement =
      this.document.activeElement && this.document.activeElement !== this.document.body
        ? this.document.activeElement
        : this.document.querySelector(':focus');
  }

  setFocusOnLastFocusedElement(): void {
    if (this.lastFocusedElement) {
      this.lastFocusedElement.focus();
    }
  }

  continue(): void {
    this.timeoutBannerService.extendSession();
  }

  signOut(): void {
    this.timeoutBannerService.signOut();
  }
}
