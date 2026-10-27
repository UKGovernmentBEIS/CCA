import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TASK_STATUS_TAG_MAP, TaskStatusTagMap } from '@netz/common/pipes';
import { ActivatedRouteStub } from '@netz/common/testing';

import { sections } from '../testing';
import { TaskListComponent } from './task-list.component';

describe('TaskListComponent', () => {
  let component: TaskListComponent;
  let componentRef: ComponentRef<TaskListComponent>;
  let fixture: ComponentFixture<TaskListComponent>;
  const map: TaskStatusTagMap = { COMPLETED: { text: 'COMPLETED', color: 'blue' } };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TASK_STATUS_TAG_MAP, useValue: map },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
    componentRef = fixture.componentRef;
    componentRef.setInput('sections', sections);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the sections and tasks', () => {
    const element: HTMLElement = fixture.nativeElement;
    const taskItems = element.querySelectorAll<HTMLLIElement>('.govuk-task-list__item');

    expect(taskItems).toBeTruthy();
    expect(taskItems.length).toEqual(4);
  });

  it('should render task hint when provided', () => {
    componentRef.setInput('sections', [
      {
        title: 'Corrective Actions',
        tasks: [
          {
            linkText: 'Action 1',
            status: 'COMPLETED',
            hint: 'Days remaining: 5 (until 20 January 2026)\nCheck boiler pressure',
          },
        ],
      },
    ]);
    fixture.detectChanges();

    const hintEl = fixture.nativeElement.querySelector('.govuk-task-list__hint');
    expect(hintEl).not.toBeNull();
    expect(hintEl.textContent.trim()).toEqual('Days remaining: 5 (until 20 January 2026)\nCheck boiler pressure');
  });

  it('should safely escape HTML tags and prevent XSS / markup injection', () => {
    const maliciousUserInput = 'Please review <a id="injected-link" href="https://phishing.site">Click here</a>';

    componentRef.setInput('sections', [
      {
        title: 'Corrective Actions',
        tasks: [
          {
            linkText: 'Action 1',
            status: 'COMPLETED',
            hint: `Days remaining: 5\n${maliciousUserInput}`,
          },
        ],
      },
    ]);
    fixture.detectChanges();

    const injectedLink = fixture.nativeElement.querySelector('#injected-link');
    const hintEl = fixture.nativeElement.querySelector('.govuk-task-list__hint');

    expect(injectedLink).toBeNull();
    expect(hintEl.textContent).toContain('<a id="injected-link"');
  });

  it('should preserve text containing < and > symbols without data loss', () => {
    const auditorInput = 'Emissions < 50mg and temperature > 100C';

    componentRef.setInput('sections', [
      {
        title: 'Corrective Actions',
        tasks: [
          {
            linkText: 'Action 1',
            status: 'COMPLETED',
            hint: auditorInput,
          },
        ],
      },
    ]);
    fixture.detectChanges();

    const hintEl = fixture.nativeElement.querySelector('.govuk-task-list__hint');
    expect(hintEl.textContent.trim()).toEqual('Emissions < 50mg and temperature > 100C');
  });

  it('should truncate hints longer than 250 characters', () => {
    const longHint = 'A'.repeat(300);

    componentRef.setInput('sections', [
      {
        title: 'Corrective Actions',
        tasks: [
          {
            linkText: 'Action 1',
            status: 'COMPLETED',
            hint: longHint,
          },
        ],
      },
    ]);
    fixture.detectChanges();

    const hintEl = fixture.nativeElement.querySelector('.govuk-task-list__hint');
    expect(hintEl.textContent.trim()).toEqual(`${'A'.repeat(250)}...`);
  });
});
