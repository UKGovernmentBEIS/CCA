import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SchemeDetailsComponent } from './scheme-details.component';

describe('SchemeDetailsComponent', () => {
  let component: SchemeDetailsComponent;
  let componentRef: ComponentRef<SchemeDetailsComponent>;
  let fixture: ComponentFixture<SchemeDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SchemeDetailsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SchemeDetailsComponent);
    component = fixture.componentInstance;

    componentRef = fixture.componentRef;
    componentRef.setInput('downloadURL', './file-download');
    componentRef.setInput('mainColumnClass', 'govuk-grid-column-two-thirds');
    componentRef.setInput('cca2Details', {
      ['CCA_2']: {
        activationDate: '2024-09-30',
        fileDocument: {
          name: 'ADS_1-T00002 CCA2 Underlying Agreement v1.pdf',
          uuid: '3a4ac334-73d0-492d-9639-a7e2f4bdd647',
        },
        terminatedDate: '2024-10-02',
      },
    });
    componentRef.setInput('cca3Details', {
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
