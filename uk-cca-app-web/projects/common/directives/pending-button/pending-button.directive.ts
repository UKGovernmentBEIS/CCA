import { ChangeDetectorRef, DestroyRef, Directive, ElementRef, OnInit, Renderer2, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { PendingRequestService } from '@netz/common/services';

@Directive({ selector: 'button[netzPendingButton]' })
export class PendingButtonDirective implements OnInit {
  private readonly pendingRequest = inject(PendingRequestService, { optional: true });
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  private readonly renderer = inject(Renderer2);
  private readonly elementRef = inject(ElementRef);
  private readonly destroy$ = inject(DestroyRef);

  ngOnInit(): void {
    if (this.pendingRequest) {
      this.pendingRequest.isRequestPending$?.pipe(takeUntilDestroyed(this.destroy$)).subscribe((isDisabled) => {
        if (isDisabled) {
          this.renderer.setAttribute(this.elementRef.nativeElement, 'disabled', 'true');
        } else {
          this.renderer.removeAttribute(this.elementRef.nativeElement, 'disabled');
        }

        this.changeDetectorRef.markForCheck();
      });
    }
  }
}
