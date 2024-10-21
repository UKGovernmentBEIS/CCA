import { NgForOf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { TaskSection } from '@netz/common/model';

import { TaskSectionComponent } from '../task-section';

@Component({
  selector: 'netz-task-list',
  templateUrl: './task-list.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [TaskSectionComponent, RouterLink, NgForOf],
})
export class TaskListComponent {
  @Input() sections: TaskSection[];
}
