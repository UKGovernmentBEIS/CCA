import { KeyValuePipe, NgTemplateOutlet, PercentPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ButtonDirective } from '@netz/govuk-components';

import { FileUploadEvent, UNKNOWN_FILE_NAME, UploadedFileRef } from '../file-input/file-upload-event';

/**
 * A file entry prepared for rendering. The file reference cannot be assumed, because the list is
 * also fed with values taken straight from form controls and API stores.
 */
interface FileUploadRow extends FileUploadEvent {
  /** Display name of the file, never empty. */
  name: string;
  /** Stable identity for `@for` tracking, unique even for entries without a file. */
  key: File | UploadedFileRef | string | number;
}

@Component({
  selector: 'cca-file-upload-list',
  templateUrl: './file-upload-list.component.html',
  imports: [NgTemplateOutlet, RouterLink, PercentPipe, KeyValuePipe, ButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FileUploadListComponent {
  protected readonly headerSize = input<'m' | 's'>('m');
  protected readonly listTitle = input<string>();
  protected readonly files = input<FileUploadEvent[]>([]);
  protected readonly isDisabled = input<boolean>(false);

  protected readonly fileDelete = output<number>();

  protected readonly rows = computed<FileUploadRow[]>(() => {
    const files = this.files();

    // `Array.from` also turns holes in a sparse list into entries, so every row has a key.
    return Array.from(Array.isArray(files) ? files : []).map((file: FileUploadEvent, index) => ({
      ...file,
      name: file?.file?.name || UNKNOWN_FILE_NAME,
      key: file?.file ?? file?.uuid ?? index,
    }));
  });
}
