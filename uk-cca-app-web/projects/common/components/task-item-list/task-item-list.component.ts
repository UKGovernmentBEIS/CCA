import { NgFor } from '@angular/common';
import { ChangeDetectionStrategy, Component, HostBinding, Input } from '@angular/core';

import { TaskItem } from '@netz/common/model';

import { TaskItemComponent } from '../task-item';

/* eslint-disable @angular-eslint/component-selector */
@Component({
  selector: 'ul[netz-task-item-list]',
  template: `
    <li
      netz-task-item
      *ngFor="let task of taskItems"
      [link]="task.link"
      [linkText]="task.linkText"
      [status]="task.status"
    ></li>
    <ng-content></ng-content>
  `,
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [TaskItemComponent, NgFor],
})
export class TaskItemListComponent {
  @Input() taskItems: TaskItem[];

  @HostBinding('class.app-task-list__items') readonly taskListItems = true;
}
