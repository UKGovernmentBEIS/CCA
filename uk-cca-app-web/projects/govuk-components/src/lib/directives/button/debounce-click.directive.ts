import { Directive, HostListener, OnDestroy, OnInit, input, output } from '@angular/core';

import { debounceTime, Subject, Subscription } from 'rxjs';

@Directive({
  selector: 'button[govukDebounceClick]',
  standalone: true,
})
export class DebounceClickDirective implements OnInit, OnDestroy {
  readonly debounceTime = input(500);

  readonly debounceClick = output<MouseEvent>();

  private subscription = new Subscription();
  private clicks = new Subject<MouseEvent>();

  @HostListener('click', ['$event'])
  onClick(event: MouseEvent): void {
    this.clicks.next(event);
  }

  ngOnInit(): void {
    this.subscription = this.clicks
      .pipe(debounceTime(this.debounceTime()))
      .subscribe((e: MouseEvent) => this.debounceClick.emit(e));
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
