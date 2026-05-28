import { ChangeDetectionStrategy, Component, computed, contentChildren, effect, inject, input } from '@angular/core';
import { AccordionItemComponent } from './accordion-item/accordion-item.component';
import { ACCORDION, Accordion, accordionFactory } from './accordion';

@Component({
  selector: 'govuk-accordion',
  template: `
    <div class="govuk-accordion" [id]="id()" data-module="govuk-accordion">
      <div class="govuk-accordion__controls">
        <button
          type="button"
          class="govuk-accordion__show-all"
          [attr.aria-expanded]="areExpanded() ? 'true' : 'false'"
          (click)="toggleAllSections()"
        >
          <span class="govuk-accordion-nav__chevron" [class.govuk-accordion-nav__chevron--down]="!areExpanded()"></span>
          <span class="govuk-accordion__show-all-text">
            {{ areExpanded() ? 'Hide all sections' : 'Show all sections' }}
          </span>
        </button>
      </div>

      <ng-content select="govuk-accordion-item" />
    </div>
  `,
  providers: [accordionFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccordionComponent {
  private accordion = inject<Accordion>(ACCORDION);

  readonly id = input<string>();
  readonly openIndexes = input<number[]>();
  readonly cacheDisabled = input<boolean>();

  readonly accordionItems = contentChildren(AccordionItemComponent);

  readonly areExpanded = computed(() => {
    const items = this.accordionItems();
    return items.length > 0 && items.every((item) => item.isExpanded());
  });

  constructor() {
    effect(() => {
      this.accordion.id = this.id();
      this.accordion.openIndexes = this.openIndexes();
      this.accordion.cacheDisabled = this.cacheDisabled();

      const openIndexes = this.accordion.openIndexes;
      const items = this.accordionItems();
      items.forEach((item, idx) => {
        const oneBasedIndex = idx + 1;

        item.itemIndex.set(oneBasedIndex);

        if (Array.isArray(openIndexes)) {
          item.isExpanded.set(openIndexes.includes(oneBasedIndex));
        }
      });
    });
  }

  toggleAllSections() {
    const current = this.areExpanded();
    this.accordionItems().forEach((item) => item.isExpanded.set(!current));
  }
}
