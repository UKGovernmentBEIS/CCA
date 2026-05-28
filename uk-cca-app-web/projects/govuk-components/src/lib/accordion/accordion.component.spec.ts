import { Component, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccordionComponent } from './accordion.component';
import { AccordionItemComponent } from './accordion-item/accordion-item.component';
import { accordionFactory } from './accordion';

describe('AccordionComponent', () => {
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: `
      <govuk-accordion [id]="id()" [openIndexes]="openIndexes()">
        <govuk-accordion-item header="One">
          <p>Item One</p>
        </govuk-accordion-item>
        <govuk-accordion-item header="Two">
          <p>Item Two</p>
        </govuk-accordion-item>
      </govuk-accordion>
    `,
    imports: [AccordionComponent, AccordionItemComponent],
  })
  class TestComponent {
    id = signal('test-accordion');
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

  const getSections = () => fixture.nativeElement.querySelectorAll('.govuk-accordion__section');

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render accordion with ID', () => {
    const el = fixture.nativeElement.querySelector('.govuk-accordion');
    expect(el.id).toBe('test-accordion');
  });

  it('should initialize with open indexes', () => {
    const buttons = getButtons();
    expect(buttons[0].getAttribute('aria-expanded')).toBe('false');
    expect(buttons[1].getAttribute('aria-expanded')).toBe('true');
  });

  it('should toggle all sections', () => {
    const toggle = fixture.nativeElement.querySelector('.govuk-accordion__show-all');
    toggle.click();
    fixture.detectChanges();

    getSections().forEach((section: HTMLElement) => {
      expect(section.classList).toContain('govuk-accordion__section--expanded');
    });
  });

  it('should expand & collapse individual sections', () => {
    const buttons = getButtons();

    buttons[0].click();
    fixture.detectChanges();
    expect(buttons[0].getAttribute('aria-expanded')).toBe('true');

    buttons[1].click();
    fixture.detectChanges();
    expect(buttons[1].getAttribute('aria-expanded')).toBe('false');
  });
});
