import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { accordionFactory } from '../accordion';
import { AccordionComponent } from '../accordion.component';
import { AccordionItemSummaryDirective } from '../directives/accordion-item-summary.directive';
import { AccordionItemComponent } from './accordion-item.component';

describe('AccordionItemComponent', () => {
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    imports: [AccordionComponent, AccordionItemComponent, AccordionItemSummaryDirective],
    template: `
      <govuk-accordion id="test-accordion" [openIndexes]="openIndexes()">
        <govuk-accordion-item header="First item">
          <div govukAccordionItemSummary>First item summary</div>
          <p>Content</p>
        </govuk-accordion-item>
        <govuk-accordion-item header="Second item">
          <p>Content</p>
        </govuk-accordion-item>
      </govuk-accordion>
    `,
  })
  class TestComponent {
    openIndexes = signal([2]);
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [accordionFactory],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();
  });

  const getButtons = () => fixture.nativeElement.querySelectorAll('.govuk-accordion__section-button');

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should have auto generated ids', () => {
    const content = fixture.nativeElement.querySelector('.govuk-accordion__section-content');
    expect(content.id).toEqual('test-accordion-content-1');

    const summary = fixture.nativeElement.querySelector('.govuk-accordion__section-summary');
    expect(summary.id).toEqual('test-accordion-summary-1');
  });

  it('should contain button with attributes', () => {
    const buttons = getButtons();
    const firstLabel = buttons[0].getAttribute('aria-label');

    expect(firstLabel).toContain('First item, Show this section');

    buttons[0].click();
    fixture.detectChanges();
    expect(buttons[0].getAttribute('aria-label')).toContain('First item, Hide this section');
  });

  it('should initialize with open indexes', async () => {
    fixture.detectChanges();
    await fixture.whenStable();
    const buttons = getButtons();
    expect(buttons[0].getAttribute('aria-expanded')).toBe('false');
    expect(buttons[1].getAttribute('aria-expanded')).toBe('true');
  });

  it('should expand/collapse sections via clicks', async () => {
    const buttons = getButtons();

    buttons[0].click();
    fixture.detectChanges();
    await fixture.whenStable();
    expect(buttons[0].getAttribute('aria-expanded')).toBe('true');
    expect(buttons[1].getAttribute('aria-expanded')).toBe('true');

    buttons[1].click();
    fixture.detectChanges();
    await fixture.whenStable();
    expect(buttons[0].getAttribute('aria-expanded')).toBe('true');
    expect(buttons[1].getAttribute('aria-expanded')).toBe('false');
  });

  it('should toggle chevrons and text', () => {
    const buttons = getButtons();

    const chevrons = Array.from(buttons).map((btn: HTMLElement) => btn.querySelector('.govuk-accordion-nav__chevron'));

    expect(chevrons[0].classList).toContain('govuk-accordion-nav__chevron--down');

    buttons[0].click();
    fixture.detectChanges();

    expect(chevrons[0].classList).not.toContain('govuk-accordion-nav__chevron--down');
  });
});
