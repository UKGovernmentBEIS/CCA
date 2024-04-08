import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { TagColor } from './tag-color.type';

@Component({
  selector: 'govuk-tag',
  standalone: true,
  imports: [NgClass],
  templateUrl: './tag.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TagComponent {
  @Input() color: TagColor;
}
