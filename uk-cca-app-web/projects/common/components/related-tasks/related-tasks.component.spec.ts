import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { BasePage } from '../../testing';
import { RelatedTasksComponent } from './related-tasks.component';

describe('RelatedTasksComponent', () => {
  let component: RelatedTasksComponent;
  let componentRef: ComponentRef<RelatedTasksComponent>;
  let fixture: ComponentFixture<RelatedTasksComponent>;
  let page: Page;

  class Page extends BasePage<RelatedTasksComponent> {
    get heading() {
      return this.query('h2');
    }
    get items() {
      return this.queryAll('.govuk-heading-s').map((el) => el.textContent.trim());
    }
    get daysRemaining() {
      return this.queryAll('p[data-testid="days-remaining"]').map((el) => el.textContent.trim());
    }
    get links() {
      return this.queryAll<HTMLLinkElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(RelatedTasksComponent);
    component = fixture.componentInstance;
    componentRef = fixture.componentRef;
    componentRef.setInput('items', [
      {
        requestType: 'DUMMY_REQUEST_TYPE',
        taskType: 'DUMMY_REQUEST_TASK_TYPE2',
        taskId: 1,
        daysRemaining: 13,
      },
    ]);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display items', () => {
    expect(page.heading.textContent).toEqual('Related tasks');
    expect(page.daysRemaining).toEqual(['Days Remaining: 13']);
  });
});
