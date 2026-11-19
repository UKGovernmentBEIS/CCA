import { ChangeDetectorRef, DestroyRef, Directive, ElementRef, inject, OnInit, Renderer2 } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationSkipped,
  NavigationStart,
  Router,
} from '@angular/router';

import { combineLatest, filter, map, Observable, of, startWith } from 'rxjs';

import { PendingRequestService } from '@netz/common/services';

@Directive({ selector: 'button[netzPendingButton]' })
export class PendingButtonDirective implements OnInit {
  private readonly pendingRequest = inject(PendingRequestService, { optional: true });
  private readonly router = inject(Router, { optional: true });
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  private readonly renderer = inject(Renderer2);
  private readonly elementRef = inject(ElementRef);
  private readonly destroy$ = inject(DestroyRef);

  ngOnInit(): void {
    if (this.pendingRequest) {
      // A save usually navigates on success. Stay disabled until that navigation settles, so a second click
      // cannot post again while the next page loads. `router.navigate()` emits `NavigationStart` synchronously,
      // before the request's `finalize` clears the pending state.
      combineLatest([this.pendingRequest.isRequestPending$, this.isNavigating()])
        .pipe(
          map(([isRequestPending, isNavigating]) => isRequestPending || isNavigating),
          takeUntilDestroyed(this.destroy$),
        )
        .subscribe((isDisabled) => {
          if (isDisabled) {
            this.renderer.setAttribute(this.elementRef.nativeElement, 'disabled', 'true');
          } else {
            this.renderer.removeAttribute(this.elementRef.nativeElement, 'disabled');
          }

          this.changeDetectorRef.markForCheck();
        });
    }
  }

  private isNavigating(): Observable<boolean> {
    if (!this.router) return of(false);

    return this.router.events.pipe(
      filter(
        (event) =>
          event instanceof NavigationStart ||
          event instanceof NavigationEnd ||
          event instanceof NavigationCancel ||
          event instanceof NavigationError ||
          event instanceof NavigationSkipped,
      ),
      map((event) => event instanceof NavigationStart),
      startWith(this.router.currentNavigation() !== null),
    );
  }
}
