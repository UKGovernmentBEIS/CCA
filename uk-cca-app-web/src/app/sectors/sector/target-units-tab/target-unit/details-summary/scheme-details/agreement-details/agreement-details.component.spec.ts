import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { SchemeVersionEnum } from '@shared/types';

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
      [SchemeVersionEnum.CCA_3]: {
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

  it.each(['', 'T00:00:00.000Z'])('preserves activation and termination calendar dates with suffix %s', (suffix) => {
    componentRef.setInput('agreement', {
      activationDate: `2025-03-30${suffix}`,
      terminatedDate: `2025-10-26${suffix}`,
    });
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    const values = Array.from(element.querySelectorAll('dd.govuk-summary-list__value')).map((value) =>
      value.textContent.trim(),
    );
    expect(values).toContain('30 Mar 2025');
    expect(values).toContain('26 Oct 2025');
  });

  it('should display correct view', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});
