import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule, UntypedFormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError } from 'rxjs';

import { BusinessError } from '@error/business-error/business-error';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective } from '@netz/govuk-components';
import {
  commonFileValidators,
  FileInputComponent,
  FileType,
  FileUploadEvent,
  FileValidators,
  requiredFileValidator,
  SummaryComponent,
} from '@shared/components';

import { DocumentTemplatesService } from 'cca-api';

import { toDocumentTemplateSummary } from '../document-template-summary';

@Component({
  selector: 'cca-document-edit',
  templateUrl: './document-edit.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    PageHeadingComponent,
    FileInputComponent,
    SummaryComponent,
    ButtonDirective,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DocumentEditComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly documentTemplatesService = inject(DocumentTemplatesService);

  readonly fileType = FileType.DOCX;

  protected readonly documentTemplate = toSignal(
    this.documentTemplatesService.getDocumentTemplateById(this.activatedRoute.snapshot.params.templateId).pipe(
      catchError(() => {
        throw new BusinessError('Could not get template details');
      }),
    ),
  );

  readonly data = computed(() => toDocumentTemplateSummary(this.documentTemplate(), true));

  readonly form = computed(
    () =>
      new FormGroup({
        documentTemplate: new UntypedFormControl(
          this.documentTemplate().fileUuid
            ? ({
                uuid: this.documentTemplate().fileUuid,
                file: { name: this.documentTemplate().filename } as File,
              } as Pick<FileUploadEvent, 'file' | 'uuid'>)
            : null,
          {
            validators: commonFileValidators.concat(
              requiredFileValidator,
              FileValidators.validContentTypes([FileType.DOCX]),
            ),
            updateOn: 'change',
          },
        ),
      }),
  );

  getDownloadUrl(uuid: string) {
    return ['../file-download', 'attachment', uuid];
  }

  onSubmit() {
    if (this.form().invalid) return;

    this.documentTemplatesService
      .updateDocumentTemplate(this.documentTemplate().id, this.form().controls.documentTemplate.value.file)
      .pipe(
        catchError(() => {
          throw new BusinessError('Could not upload document template file');
        }),
      )
      .subscribe(() =>
        this.router.navigate(['..'], {
          relativeTo: this.activatedRoute,
          replaceUrl: true,
          state: { notification: true },
        }),
      );
  }
}
