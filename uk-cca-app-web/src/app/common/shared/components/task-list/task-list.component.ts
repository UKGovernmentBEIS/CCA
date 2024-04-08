import { NgForOf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { TaskSection } from '@common/shared/model/task-list';

import { TaskSectionComponent } from '../task-section';

@Component({
  selector: 'cca-task-list',
  templateUrl: './task-list.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [TaskSectionComponent, RouterLink, NgForOf],
})
export class TaskListComponent {
  @Input() sections: TaskSection[];
}
