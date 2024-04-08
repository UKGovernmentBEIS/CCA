import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Observable } from 'rxjs';

import { RequestInfoDTO, RequestTaskDTO } from 'cca-api';

import { MakePaymentHelpComponent } from './make-payment-help.component';

describe('MakePaymentHelpComponent', () => {
  let component: TestWrapperComponent;
  let fixture: ComponentFixture<TestWrapperComponent>;

  @Component({
    selector: 'cca-test-wrapper-component',
    template: `<cca-make-payment-help
      [competentAuthority$]="competentAuthority$"
      [requestType$]="requestType$"
      [requestTaskType$]="requestTaskType$"
      [defaultHelp]="defaultHelp"
    ></cca-make-payment-help>`,
  })
  class TestWrapperComponent {
    competentAuthority$: Observable<RequestInfoDTO['competentAuthority']>;
    requestType$: Observable<RequestInfoDTO['type']>;
    requestTaskType$: Observable<RequestTaskDTO['type']>;
    defaultHelp: string;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestWrapperComponent, MakePaymentHelpComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
