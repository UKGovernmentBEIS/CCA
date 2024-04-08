import { NgModule } from '@angular/core';

import { FileInputComponent } from '@shared/file-input/file-input.component';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { PendingButtonDirective } from '@shared/pending-button.directive';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';

import { DocumentTemplateDetailsTemplateComponent } from './document/document-template-details-template.component';
import { DocumentTemplateOverviewComponent } from './document/document-template-overview.component';
import { DocumentTemplateComponent } from './document/edit/document-template.component';
import { TemplateFileDownloadComponent } from './file-download/template-file-download.component';
import { TemplatesComponent } from './templates.component';
import { TemplatesRoutingModule } from './templates-routing.module';

@NgModule({
  declarations: [
    DocumentTemplateComponent,
    DocumentTemplateDetailsTemplateComponent,
    DocumentTemplateOverviewComponent,
    TemplateFileDownloadComponent,
    TemplatesComponent,
  ],
  imports: [
    FileInputComponent,
    GovukDatePipe,
    PageHeadingComponent,
    PendingButtonDirective,
    SharedModule,
    TemplatesRoutingModule,
  ],
})
export class TemplatesModule {}
