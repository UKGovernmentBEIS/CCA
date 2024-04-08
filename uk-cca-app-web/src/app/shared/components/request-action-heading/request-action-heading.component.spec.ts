import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { BasePage } from '@testing';

import { RequestActionHeadingComponent } from './request-action-heading.component';

describe('RequestActionHeadingComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <cca-request-action-heading [headerText]="headerText" [timelineCreationDate]="timelineCreationDate">
        <div>New content</div>
      </cca-request-action-heading>
    `,
    standalone: true,
    imports: [RequestActionHeadingComponent],
  })
  class TestComponent {
    headerText = 'Cessation completed';
    timelineCreationDate = new Date().toISOString();
  }

  class Page extends BasePage<TestComponent> {
    get heading() {
      return this.query<HTMLElement>('cca-page-heading h1.govuk-heading-l');
    }

    get creationDate() {
      return this.query<HTMLElement>('p');
    }

    get newContent() {
      return this.query<HTMLElement>('div');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render header text, creation date and other content', () => {
    const govukDate = new GovukDatePipe();

    expect(page.heading.textContent.trim()).toEqual('Cessation completed');
    expect(page.creationDate.textContent.trim()).toEqual(
      govukDate.transform(component.timelineCreationDate, 'datetime'),
    );
    expect(page.newContent.textContent.trim()).toEqual('New content');
  });
});
