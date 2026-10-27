import { NgClass } from '@angular/common';
import { Component, input } from '@angular/core';

import { TagColor } from './tag-color.type';

@Component({
  selector: 'govuk-tag',
  templateUrl: './tag.component.html',
  imports: [NgClass],
})
export class TagComponent {
  readonly color = input<TagColor>();
}
