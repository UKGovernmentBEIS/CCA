import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  input,
  output,
  signal,
  contentChild,
  inject,
} from '@angular/core';

import { ACCORDION, Accordion, isSessionStorageAvailable } from '../accordion';
import { AccordionItemSummaryDirective } from '../directives/accordion-item-summary.directive';

@Component({
  selector: 'govuk-accordion-item',
  templateUrl: './accordion-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccordionItemComponent {
  protected readonly accordion = inject<Accordion>(ACCORDION);

  protected readonly header = input<string>();
  protected readonly caption = input<string>();
  protected readonly expand = output<boolean>();

  protected readonly accordionItemSummaryDirective = contentChild(AccordionItemSummaryDirective);

  protected readonly isFocused = signal(false);
  readonly itemIndex = signal<number | null>(null);
  readonly isExpanded = signal(false);

  protected readonly contentId = computed(() => {
    const index = this.itemIndex();
    const id = this.accordion.id ?? 'accordion';
    return index === null ? '' : `${id}-content-${index}`;
  });

  constructor() {
    effect(() => {
      const index = this.itemIndex();
      if (index === null) return;

      const isExpanded = this.isExpanded();
      this.expand.emit(isExpanded);

      const cacheDisabled = this.accordion.cacheDisabled ?? false;
      if (!cacheDisabled && isSessionStorageAvailable()) {
        sessionStorage.setItem(this.contentId(), String(isExpanded));
      }
    });
  }

  toggle() {
    this.isExpanded.update((v) => !v);
  }

  onSpace(event: Event) {
    event.preventDefault();
    this.toggle();
  }
}
