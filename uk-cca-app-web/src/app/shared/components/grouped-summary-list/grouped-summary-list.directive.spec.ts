import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { SummaryListComponent } from '@netz/govuk-components';

import { GroupedSummaryListDirective } from './grouped-summary-list.directive';

describe('GroupedSummaryListDirective', () => {
  let directive: GroupedSummaryListDirective;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    standalone: true,
    template: '<dl govuk-summary-list ccaGroupedSummaryList [details]="[]"></dl>',
    imports: [GroupedSummaryListDirective, SummaryListComponent],
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    directive = fixture.debugElement.query(By.directive(GroupedSummaryListDirective)).componentInstance;
    fixture.detectChanges();
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should apply the edge borders class', () => {
    expect(element.querySelector('.summary-list--edge-border')).toBeTruthy();
  });

  it('should disable other borders', () => {
    expect(element.querySelector('.govuk-summary-list--no-border')).toBeTruthy();
  });
});
