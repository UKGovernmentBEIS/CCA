import { CommonModule, KeyValuePipe, NgTemplateOutlet, PercentPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ButtonDirective, LinkDirective } from 'govuk-components';

import { FileUploadEvent } from '../file-input/file-upload-event';

@Component({
  selector: 'cca-file-upload-list',
  templateUrl: './file-upload-list.component.html',
  styleUrls: ['../multiple-file-input/multiple-file-input.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgTemplateOutlet, RouterLink, PercentPipe, KeyValuePipe, ButtonDirective, LinkDirective, CommonModule],
  standalone: true,
})
export class FileUploadListComponent {
  @Input() headerSize: 'm' | 's' = 'm';
  @Input() listTitle: string;
  @Input() files: FileUploadEvent[] = [];
  @Input() isDisabled = false;
  @Output() readonly fileDelete = new EventEmitter<number>();
}
