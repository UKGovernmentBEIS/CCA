import { inject, Pipe, PipeTransform } from '@angular/core';

import { TagColor } from '@netz/govuk-components';

import { TASK_STATUS_TAG_MAP, TaskStatusTagMap } from '../status-tag.providers';

@Pipe({ name: 'statusTagColor', pure: true })
export class StatusTagColorPipe implements PipeTransform {
  private readonly statusMap = inject<TaskStatusTagMap>(TASK_STATUS_TAG_MAP);

  transform(status: string): TagColor {
    return this.statusMap[status].color;
  }
}
