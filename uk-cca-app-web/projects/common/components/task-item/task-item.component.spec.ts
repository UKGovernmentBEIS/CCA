import { NgFor } from '@angular/common';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { TASK_STATUS_TAG_MAP, TaskStatusTagMap } from '@netz/common/pipes';

import { tasks } from '../testing';
import { TaskItemComponent } from './task-item.component';

describe('TaskItemComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  const map: TaskStatusTagMap = { COMPLETED: { text: 'COMPLETED', color: 'blue' } };

  @Component({
    template: `
      <ul class="app-task-list__items">
        <li
          netz-task-item
          *ngFor="let task of taskItems"
          [link]="task.link"
          [linkText]="task.linkText"
          [status]="task.status"
          class="app-task-list__item"
        >
          <div class="custtmpl">tmpl</div>
        </li>
      </ul>
    `,
    standalone: true,
    imports: [TaskItemComponent, NgFor],
  })
  class TestComponent {
    taskItems = tasks;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, TaskItemComponent],
      providers: [{ provide: TASK_STATUS_TAG_MAP, useValue: map }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the task items', () => {
    const element: HTMLElement = fixture.nativeElement;
    const items = element.querySelectorAll<HTMLUListElement>('li');
    const customTmpl = element.querySelector<HTMLDivElement>('div.custtmpl');

    expect(items.length).toEqual(4);
    items.forEach((item, index) => {
      expect(item.querySelector('a').textContent.trim()).toEqual(tasks[index].linkText);
      expect(item.querySelector('a').href).toContain(tasks[index].link);
    });
    expect(items[0].querySelector('strong').classList.contains('govuk-tag--blue')).toBeTruthy();
    expect(items[1].querySelector('strong').classList.contains('govuk-tag--blue')).toBeTruthy();
    expect(items[2].querySelector('strong').classList.contains('govuk-tag--blue')).toBeTruthy();
    expect(customTmpl.textContent.trim()).toContain('tmpl');
  });
});
