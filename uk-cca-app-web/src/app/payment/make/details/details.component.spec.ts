import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { Observable } from 'rxjs';

import { RelatedActionsComponent } from '@shared/components/related-actions/related-actions.component';
import { TaskHeaderInfoComponent } from '@shared/components/task-header-info/task-header-info.component';
import { TimelineComponent } from '@shared/components/timeline/timeline.component';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';

import { RequestInfoDTO, RequestTaskDTO } from 'cca-api';

import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;

  @Component({
    selector: 'cca-make-payment-help',
    template: `<div class="help">
      <p class="competentAuthority">{{ competentAuthority$ | async }}</p>
      <p class="requestType">{{ requestType$ | async }}</p>
      <p class="requestTaskType">{{ requestTaskType$ | async }}</p>
      <p class="default">{{ default }}</p>
    </div>`,
  })
  class MockPaymentHelpComponent {
    @Input() competentAuthority$: Observable<RequestInfoDTO['competentAuthority']>;
    @Input() requestType$: Observable<RequestInfoDTO['type']>;
    @Input() requestTaskType$: Observable<RequestTaskDTO['type']>;
    default: string;
    @Input() set defaultHelp(defaultHelp: string) {
      this.default = defaultHelp;
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SharedModule,
        RouterTestingModule,
        GovukDatePipe,
        PageHeadingComponent,
        TaskHeaderInfoComponent,
        TimelineComponent,
        RelatedActionsComponent,
      ],
      declarations: [DetailsComponent, MockPaymentHelpComponent],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
