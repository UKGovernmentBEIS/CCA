import { inject, Pipe, PipeTransform } from '@angular/core';

import { TASK_STATUS_TAG_MAP, TaskStatusTagMap } from '../status-tag.providers';

@Pipe({ name: 'statusTagText', pure: true })
export class StatusTagTextPipe implements PipeTransform {
  private readonly statusMap = inject<TaskStatusTagMap>(TASK_STATUS_TAG_MAP);

  transform(status: string): string {
    return this.statusMap[status].text;
  }
}
