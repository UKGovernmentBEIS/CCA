import { HttpEvent } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { Observable } from 'rxjs';

import {
  createCommonFileAsyncValidators,
  FileType,
  FileUploadEvent,
  FileUploadService,
  FileValidators,
} from '@shared/components';

import { FileUuidDTO, SectorAssociationSchemeDocumentDTO, SectorSchemeDocumentUploadService } from 'cca-api';

@Injectable({ providedIn: 'root' })
export class SectorSchemeDocumentFileService {
  private readonly formBuilder = inject(FormBuilder);
  private readonly fileUploadService = inject(FileUploadService);
  private readonly sectorSchemeDocumentUploadService = inject(SectorSchemeDocumentUploadService);

  buildFormControl(
    document: SectorAssociationSchemeDocumentDTO,
    required = false,
    disabled = false,
  ): FormControl<FileUploadEvent> {
    return this.formBuilder.control(
      {
        value: document ? this.buildFileEvent(document.uuid, document.fileName) : null,
        disabled,
      },
      {
        validators: [FileValidators.validContentTypes([FileType.PDF], 'must be a PDF')],
        asyncValidators: [...createCommonFileAsyncValidators(required), this.upload()],
        updateOn: 'change',
      },
    );
  }

  private buildFileEvent(uuid: string, fileName: string): FileUploadEvent {
    return {
      uuid,
      file: { name: fileName } as File,
    };
  }

  private storeUpload(file: File): Observable<HttpEvent<FileUuidDTO>> {
    return this.sectorSchemeDocumentUploadService.uploadSectorSchemeDocumentFile(file, 'events', true);
  }

  private upload = () => this.fileUploadService.upload((file) => this.storeUpload(file));
}
