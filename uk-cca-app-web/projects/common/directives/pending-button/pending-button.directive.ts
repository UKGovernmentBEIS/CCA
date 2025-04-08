import { ChangeDetectorRef, DestroyRef, Directive, ElementRef, OnInit, Optional, Renderer2 } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { PendingRequestService } from '@netz/common/services';

@Directive({ selector: 'button[netzPendingButton]', standalone: true })
export class PendingButtonDirective implements OnInit {
  constructor(
    @Optional() private readonly pendingRequest: PendingRequestService,
    private readonly changeDetectorRef: ChangeDetectorRef,
    private readonly renderer: Renderer2,
    private readonly elementRef: ElementRef,
    private readonly destroy$: DestroyRef,
  ) {}

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
