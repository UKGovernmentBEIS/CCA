import { DOCUMENT, Location, ViewportScroller } from '@angular/common';
import { Inject, Injectable } from '@angular/core';
import { Router, Scroll } from '@angular/router';

import { filter } from 'rxjs';

export interface ScrollState {
  scrollSkip: boolean;
}

@Injectable({ providedIn: 'root' })
export class ScrollService {
  constructor(
    private readonly router: Router,
    private readonly viewportScroller: ViewportScroller,
    private readonly location: Location,
    @Inject(DOCUMENT) private readonly document: Document,
  ) {
    this.initialize();
  }

  private initialize(): void {
    this.router.events.pipe(filter((event) => event instanceof Scroll)).subscribe((event: Scroll) => {
      const state = this.location.getState() as ScrollState;
      if (!state?.scrollSkip && !event.anchor) {
        if (event.position) {
          // backward navigation
          this.viewportScroller.scrollToPosition(event.position);
        } else {
          // forward navigation
          this.viewportScroller.scrollToPosition([0, 0]);
        }
      }
    });
  }
}
