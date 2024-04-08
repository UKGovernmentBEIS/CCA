import { ComponentFixture } from '@angular/core/testing';

import { convertToUTCDate } from './date-helpers';
import { changeInputValue, getInputValue } from './input-helpers';

export class BasePage<C> {
  private element: HTMLElement;

  constructor(protected readonly fixture: ComponentFixture<C>) {
    this.element = fixture.nativeElement;
  }

  sanitizeSelector(selector: string): string {
    return selector.replace(/\./g, '\\.');
  }

  query<T extends keyof HTMLElementTagNameMap>(selector: T): HTMLElementTagNameMap[T];
  query<E extends Element = Element>(selector: string): E | null;
  query<T extends keyof HTMLElementTagNameMap, E extends Element = Element>(
    selector: T,
  ): HTMLElementTagNameMap[T] | E | null {
    return this.element.querySelector<T>(selector);
  }

  queryAll<T extends keyof HTMLElementTagNameMap>(selector: T): HTMLElementTagNameMap[T][];
  queryAll<E extends Element = Element>(selector: string): E[];
  queryAll<T extends keyof HTMLElementTagNameMap, E extends Element = Element>(
    selector: T,
  ): HTMLElementTagNameMap[T][] | E[] {
    return Array.from(this.element.querySelectorAll<T>(selector));
  }

  getInputValue<T = string>(selector: string | HTMLInputElement | HTMLSelectElement): T {
    return getInputValue(this.fixture, selector instanceof Element ? selector : this.sanitizeSelector(selector));
  }

  setInputValue(selector: string, value): void {
    changeInputValue(this.fixture, this.sanitizeSelector(selector), value);
  }

  getDateInputValue(selector: string): Date {
    const day = ('0' + this.getInputValue(`${selector}-day`)).slice(-2);
    const month = ('0' + this.getInputValue(`${selector}-month`)).slice(-2);
    const year = this.getInputValue(`${selector}-year`);
    return convertToUTCDate(new Date(`${year}-${month}-${day}`));
  }

  setDateInputValue(selector: string, value: Date) {
    this.setInputValue(`${selector}-day`, value.getDate());
    this.setInputValue(`${selector}-month`, value.getMonth() + 1);
    this.setInputValue(`${selector}-year`, value.getFullYear());
  }
}
