import { KeyValuePipe, NgTemplateOutlet, PercentPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ButtonDirective, LinkDirective } from '@netz/govuk-components';

import { FileUploadEvent } from '../file-input/file-upload-event';

@Component({
  selector: 'cca-file-upload-list',
  templateUrl: './file-upload-list.component.html',
  styleUrl: '../multiple-file-input/multiple-file-input.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgTemplateOutlet, RouterLink, PercentPipe, KeyValuePipe, ButtonDirective, LinkDirective],
  standalone: true,
})
export class FileUploadListComponent {
  headerSize = input<'m' | 's'>('m');
  listTitle = input<string>();
  files = input<FileUploadEvent[]>([]);
  isDisabled = input<boolean>(false);

  fileDelete = output<number>();
}
