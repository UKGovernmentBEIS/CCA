import { Inject, Pipe, PipeTransform } from '@angular/core';

import { TagColor } from 'govuk-components';

import { TASK_STATUS_TAG_MAP, TaskStatusTagMap } from '../status-tag.providers';

@Pipe({
  name: 'statusTagColor',
  pure: true,
  standalone: true,
})
export class StatusTagColorPipe implements PipeTransform {
  constructor(@Inject(TASK_STATUS_TAG_MAP) private statusMap: TaskStatusTagMap) {}

  transform(status: string): TagColor {
    return this.statusMap[status].color;
  }
}
