import { HttpEvent } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { Observable } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { createCommonFileAsyncValidators, FileUploadEvent, FileUploadService } from '@shared/components';
import { requestTaskReassignedError } from '@shared/errors';

import { FileEvidencesUploadService, FileInfoDTO, FileUuidDTO } from 'cca-api';

@Injectable({ providedIn: 'root' })
export class FileEvidenceUploadService {
  private readonly formBuilder = inject(FormBuilder);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly fileUploadService = inject(FileUploadService);
  private readonly fileEvidencesUploadService = inject(FileEvidencesUploadService);

  buildFormControl(
    fileInfoDTO: FileInfoDTO | FileInfoDTO[],
    required = false,
    disabled = false,
  ): FormControl<FileUploadEvent | FileUploadEvent[]> {
    return this.formBuilder.control(
      {
        value: !fileInfoDTO
          ? null
          : Array.isArray(fileInfoDTO)
            ? fileInfoDTO.map((fi) => this.buildFileEvent(fi.uuid, fi.name))
            : this.buildFileEvent(fileInfoDTO.uuid, fileInfoDTO.name),
        disabled,
      },
      {
        asyncValidators: [
          ...createCommonFileAsyncValidators(required),
          Array.isArray(fileInfoDTO) ? this.uploadMany() : this.upload(),
        ],
        updateOn: 'change',
      },
    );
  }

  private buildFileEvent(uuid: string, name: string): FileUploadEvent {
    return {
      uuid,
      file: { name } as File,
    };
  }

  private storeUpload(file: File): Observable<HttpEvent<FileUuidDTO>> {
    return this.fileEvidencesUploadService
      .uploadEvidenceFile(file, 'events', true)
      .pipe(
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  upload = () => this.fileUploadService.upload((file) => this.storeUpload(file));

  uploadMany = () => this.fileUploadService.uploadMany((file) => this.storeUpload(file));
}
