import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { AgreementDetailsComponent } from './agreement-details.component';

describe('AgreementDetailsComponent', () => {
  let component: AgreementDetailsComponent;
  let componentRef: ComponentRef<AgreementDetailsComponent>;
  let fixture: ComponentFixture<AgreementDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgreementDetailsComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(AgreementDetailsComponent);
    component = fixture.componentInstance;

    componentRef = fixture.componentRef;
    componentRef.setInput('mainColumnClass', 'govuk-grid-column-two-thirds');
    componentRef.setInput('title', 'CCA3 underlying agreement');
    componentRef.setInput('files', [
      { fileName: 'ADS_1-T00002 CCA3 Underlying Agreement v1.pdf', downloadUrl: './file-download' },
    ]);
    componentRef.setInput('mainColumnClass', 'govuk-grid-column-two-thirds');
    componentRef.setInput('agreement', {
      ['CCA_3']: {
        activationDate: '2025-09-30',
        fileDocument: {
          name: 'ADS_1-T00002 CCA3 Underlying Agreement v1.pdf',
          uuid: '3a4ac334-73d0-492d-9639-a7e2f4bdd648',
        },
      },
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
