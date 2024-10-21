import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { ItemLinkPipe, ItemNamePipe } from '@netz/common/pipes';

import { ItemDTO } from 'cca-api';

/**
 * Marked for refactor
 * Split floats to grid
 */
@Component({
  selector: 'netz-related-tasks',
  standalone: true,
  templateUrl: './related-tasks.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgIf, NgFor, RouterLink, ItemNamePipe, ItemLinkPipe],
})
export class RelatedTasksComponent {
  @Input() items: ItemDTO[];
  @Input() heading = 'Related tasks';
  @Input() noBorders = false;

  constructor(public readonly router: Router) {}
}
