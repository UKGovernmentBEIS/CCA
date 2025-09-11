import { KeyValuePipe, NgTemplateOutlet, PercentPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ButtonDirective } from '@netz/govuk-components';

import { FileUploadEvent } from '../file-input/file-upload-event';

@Component({
  selector: 'cca-file-upload-list',
  templateUrl: './file-upload-list.component.html',
  standalone: true,
  imports: [NgTemplateOutlet, RouterLink, PercentPipe, KeyValuePipe, ButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FileUploadListComponent {
  protected readonly headerSize = input<'m' | 's'>('m');
  protected readonly listTitle = input<string>();
  protected readonly files = input<FileUploadEvent[]>([]);
  protected readonly isDisabled = input<boolean>(false);

  protected readonly fileDelete = output<number>();
}
