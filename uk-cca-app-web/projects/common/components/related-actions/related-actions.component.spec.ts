import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Route } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestTaskActionProcessDTO } from 'cca-api';

import { BasePage } from '../../testing';
import { RelatedActionsComponent } from './related-actions.component';
import { RelatedActionsMap, TASK_RELATED_ACTIONS_MAP } from './related-actions.providers';

describe('RelatedActionsComponent', () => {
  let component: RelatedActionsComponent;
  let testComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;
  const actionsMap: RelatedActionsMap = {
    RFI_SUBMIT: { text: 'Request for information', path: ['rfi'] },
    RDE_SUBMIT: { text: 'Request deadline extension', path: ['rde'] },
  };

  @Component({
    template: `
      <netz-related-actions
        [allowedRequestTaskActions]="allowedActions"
        [showReassignAction]="isAssignable"
        [taskId]="taskId"
      ></netz-related-actions>
    `,
    imports: [RelatedActionsComponent],
  })
  class TestComponent {
    taskId: number;
    isAssignable: boolean;
    allowedActions: Array<RequestTaskActionProcessDTO['requestTaskActionType']>;
  }

  class Page extends BasePage<TestComponent> {
    get links() {
      return this.queryAll<HTMLLinkElement>('li > a');
    }
  }

  const setupTestingModule = async (withChangeAssigneeRoute = false) => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, TestComponent],
      providers: [
        { provide: TASK_RELATED_ACTIONS_MAP, useValue: actionsMap },
        { provide: ActivatedRoute, useValue: constructRoute(withChangeAssigneeRoute) },
      ],
    }).compileComponents();
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    testComponent = fixture.componentInstance;
    testComponent.isAssignable = true;
    testComponent.taskId = 1;
    testComponent.allowedActions = [];
    component = fixture.debugElement.query(By.directive(RelatedActionsComponent)).componentInstance;
    page = new Page(fixture);
  };

  it('should create', async () => {
    await setupTestingModule();
    createComponent();
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should display the links', async () => {
    await setupTestingModule();
    createComponent();
    fixture.detectChanges();

    expect(page.links.map((el) => [el.href, el.textContent])).toEqual([['http://localhost/', 'Reassign task']]);
  });
});

function constructRoute(withChangeAssignee = false): Partial<ActivatedRoute> {
  return {
    snapshot: {
      get routeConfig() {
        return { path: '' };
      },
      get parent(): any {
        return {
          get routeConfig(): Route | null {
            return {
              path: 'parent',
              children: [{ path: withChangeAssignee ? 'change-assignee' : '' }],
            };
          },
        };
      },
    } as any,
  };
}
