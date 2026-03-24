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
});
