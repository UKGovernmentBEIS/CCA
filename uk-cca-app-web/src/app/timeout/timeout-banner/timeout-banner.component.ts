import { AsyncPipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  DOCUMENT,
  ElementRef,
  inject,
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
  templateUrl: './timeout-banner.component.html',
  styleUrl: './timeout-banner.component.css',
  imports: [PageHeadingComponent, AsyncPipe, SecondsToMinutesPipe, ButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TimeoutBannerComponent implements OnInit {
  private readonly renderer = inject(Renderer2);
  private readonly destroyRef = inject(DestroyRef);
  private readonly document = inject<Document>(DOCUMENT);

  protected readonly timeoutBannerService = inject(TimeoutBannerService);

  readonly modal = viewChild<ElementRef<HTMLDialogElement>>('modal');

  private overlayClass = 'govuk-timeout-warning-overlay';
  private lastFocusedElement = null;

  ngOnInit() {
    this.timeoutBannerService.isVisible$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((isVisible) => {
      isVisible ? this.showDialog() : this.hideDialog();
    });
  }

  isDialogOpen() {
    return this.modal().nativeElement && this.modal().nativeElement.getAttribute('open') === '';
  }

  showDialog() {
    if (!this.isDialogOpen()) {
      this.renderer.addClass(this.document.body, this.overlayClass);
      this.saveLastFocusedElement();
      this.modal().nativeElement.showModal();
      this.modal().nativeElement.setAttribute('tabindex', '-1');
      this.modal().nativeElement.focus();
    }
  }

  hideDialog() {
    if (this.isDialogOpen()) {
      this.renderer.removeClass(this.document.body, this.overlayClass);
      this.modal().nativeElement.removeAttribute('tabindex');
      this.modal().nativeElement.close();
      this.setFocusOnLastFocusedElement();
    }
  }

  saveLastFocusedElement() {
    this.lastFocusedElement =
      this.document.activeElement && this.document.activeElement !== this.document.body
        ? this.document.activeElement
        : this.document.querySelector(':focus');
  }

  setFocusOnLastFocusedElement() {
    if (this.lastFocusedElement) {
      this.lastFocusedElement.focus();
    }
  }

  continue() {
    this.timeoutBannerService.extendSession();
  }

  signOut() {
    this.timeoutBannerService.signOut();
  }
}
