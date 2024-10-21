import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { TaskItem } from '@netz/common/model';

import { TaskItemListComponent } from '../task-item-list';

/* eslint-disable @angular-eslint/component-selector */
@Component({
  selector: 'li[netz-task-section]',
  template: `
    <h2 *ngIf="title" class="app-task-list__section">{{ title }}</h2>
    <ul *ngIf="tasks" netz-task-item-list [taskItems]="tasks"></ul>
    <ng-content></ng-content>
  `,
  standalone: true,
  imports: [NgIf, TaskItemListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskSectionComponent {
  @Input() title: string;
  @Input() tasks: TaskItem[];
}
