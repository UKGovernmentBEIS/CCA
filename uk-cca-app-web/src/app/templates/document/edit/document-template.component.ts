import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map, Observable, of, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';

import { DocumentTemplateDTO, DocumentTemplatesService } from 'cca-api';

import { DOCUMENT_TEMPLATE_FORM, DocumentTemplateFormProvider } from './document-template-form.provider';

@Component({
  selector: 'cca-document-template',
  templateUrl: './document-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DocumentTemplateFormProvider, DestroySubject],
})
export class DocumentTemplateComponent implements OnInit {
  documentTemplate$: Observable<DocumentTemplateDTO> = this.route.data.pipe(map((data) => data?.documentTemplate));
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  constructor(
    @Inject(DOCUMENT_TEMPLATE_FORM) readonly form: UntypedFormGroup,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly documentTemplatesService: DocumentTemplatesService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit() {
    this.form.controls.documentFile.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.displayErrorSummary$.next(false));
  }

  onSubmit(): void {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
    } else {
      this.documentTemplate$
        .pipe(
          first(),
          switchMap((documentTemplate) =>
            this.form.dirty
              ? this.documentTemplatesService.updateDocumentTemplate(
                  documentTemplate.id,
                  this.form.controls.documentFile.value.file,
                )
              : of(null),
          ),
        )
        .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route, state: { notification: true } }));
    }
  }

  getDownloadUrl(uuid: string): string | string[] {
    return ['..', 'file-download', uuid];
  }
}
