import { ChangeDetectorRef, Directive, inject, input, OnChanges, TemplateRef } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

@Directive({ standalone: true })
export abstract class TabBaseDirective implements OnChanges {
  readonly cdRef = inject(ChangeDetectorRef);
  readonly templateRef = inject(TemplateRef<void>);

  readonly id = input<string>();
  readonly label = input<string>();
  readonly badgeNumber = input(0);

  readonly isSelected = new BehaviorSubject<boolean>(false);

  ngOnChanges() {
    this.isSelected.next(this.isSelected.getValue());
  }
}
