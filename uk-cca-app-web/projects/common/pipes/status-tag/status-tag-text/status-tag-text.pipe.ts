import { Inject, Pipe, PipeTransform } from '@angular/core';

import { TASK_STATUS_TAG_MAP, TaskStatusTagMap } from '../status-tag.providers';

@Pipe({
  name: 'statusTagText',
  standalone: true,
  pure: true,
})
export class StatusTagTextPipe implements PipeTransform {
  constructor(@Inject(TASK_STATUS_TAG_MAP) private statusMap: TaskStatusTagMap) {}

  transform(status: string): string {
    return this.statusMap[status].text;
  }
}
