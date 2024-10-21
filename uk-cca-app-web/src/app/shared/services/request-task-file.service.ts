import { HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { Observable } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { createCommonFileAsyncValidators, FileUploadEvent, FileUploadService } from '@shared/components';
import { requestTaskReassignedError } from '@shared/errors/request-task-error';

import { FileUuidDTO, RequestTaskAttachmentActionProcessDTO, RequestTaskAttachmentsHandlingService } from 'cca-api';

@Injectable({ providedIn: 'root' })
export class RequestTaskFileService {
  readonly upload = (
    requestTaskId: number,
    requestTaskActionType: RequestTaskAttachmentActionProcessDTO['requestTaskActionType'],
  ) => this.fileUploadService.upload((file) => this.storeUpload(requestTaskId, file, requestTaskActionType));

  readonly uploadMany = (
    requestTaskId: number,
    requestTaskActionType: RequestTaskAttachmentActionProcessDTO['requestTaskActionType'],
  ) => this.fileUploadService.uploadMany((file) => this.storeUpload(requestTaskId, file, requestTaskActionType));

  constructor(
    private readonly fileUploadService: FileUploadService,
    private readonly requestTaskAttachmentsHandlingService: RequestTaskAttachmentsHandlingService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly formBuilder: FormBuilder,
  ) {}

  buildFormControl(
    requestTaskId: number,
    uuid: string | string[],
    attachments: { [key: string]: string },
    requestTaskActionType: RequestTaskAttachmentActionProcessDTO['requestTaskActionType'],
    required = false,
    disabled = false,
  ): FormControl<FileUploadEvent | FileUploadEvent[]> {
    return this.formBuilder.control(
      {
        value: !uuid
          ? null
          : Array.isArray(uuid)
            ? uuid.map((id) => this.buildFileEvent(id, attachments))
            : this.buildFileEvent(uuid, attachments),
        disabled,
      },
      {
        asyncValidators: [
          ...createCommonFileAsyncValidators(required),
          Array.isArray(uuid)
            ? this.uploadMany(requestTaskId, requestTaskActionType)
            : this.upload(requestTaskId, requestTaskActionType),
        ],
        updateOn: 'change',
      },
    );
  }

  private buildFileEvent(uuid: string, attachments: { [key: string]: string }): FileUploadEvent {
    //todo This should be refactored and replace the store with what is absolutely  necessary.
    // currently an assumption is mde that either the state will have
    // the property key property or the store will have a getter for the specific property
    // check common-tasks.store.ts
    return {
      uuid,
      file: { name: attachments?.[uuid] } as File,
    };
  }

  private storeUpload(
    requestTaskId: number,
    file: File,
    requestTaskActionType: RequestTaskAttachmentActionProcessDTO['requestTaskActionType'],
  ): Observable<HttpEvent<FileUuidDTO>> {
    return this.requestTaskAttachmentsHandlingService
      .uploadRequestTaskAttachment(
        {
          requestTaskActionType,
          requestTaskId,
        },
        file,
        'events',
        true,
      )
      .pipe(
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }
}
