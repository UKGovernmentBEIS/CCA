import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { FileType, FileUploadEvent } from '@shared/components';
import { Mocked } from 'vitest';

import {
  SectorAssociationSchemeDetailsUpdateService,
  SectorAssociationSchemesDTO,
  SectorSchemeDocumentUploadService,
} from 'cca-api';

import { UmbrellaAgreementComponent } from './umbrella-agreement.component';
import { UmbrellaAgreementFormModel } from './umbrella-agreement-form.provider';

describe('UmbrellaAgreementComponent', () => {
  let component: UmbrellaAgreementComponent;
  let fixture: ComponentFixture<UmbrellaAgreementComponent>;
  let route: ActivatedRouteStub;
  let router: Mocked<Router>;
  let detailsUpdateService: Mocked<SectorAssociationSchemeDetailsUpdateService>;
  let documentUploadService: Mocked<SectorSchemeDocumentUploadService>;

  const sectorScheme: SectorAssociationSchemesDTO = {
    sectorAssociationSchemeMap: {
      CCA_3: {
        id: 10,
        umbrellaAgreement: {
          id: 1,
          uuid: 'umbrella-uuid',
          fileName: 'umbrella-agreement.pdf',
          fileSize: 20,
          fileType: 'pdf',
        },
        umaDate: '2026-01-01',
        sectorDefinition: 'Current sector definition',
        schemeVersion: 'CCA_3',
        editable: true,
      },
    },
  };

  beforeEach(async () => {
    route = new ActivatedRouteStub({ sectorId: 1 }, null, { sectorScheme });
    router = {
      navigate: vi.fn().mockResolvedValue(true),
    } as unknown as Mocked<Router>;
    detailsUpdateService = {
      updateSectorAssociationSchemeDetails: vi.fn().mockReturnValue(of(null)),
    } as unknown as Mocked<SectorAssociationSchemeDetailsUpdateService>;
    documentUploadService = {
      uploadSectorSchemeDocumentFile: vi.fn(),
    } as unknown as Mocked<SectorSchemeDocumentUploadService>;

    await TestBed.configureTestingModule({
      imports: [UmbrellaAgreementComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: Router, useValue: router },
        { provide: SectorAssociationSchemeDetailsUpdateService, useValue: detailsUpdateService },
        { provide: SectorSchemeDocumentUploadService, useValue: documentUploadService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UmbrellaAgreementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should prefill the form from the resolved CCA3 sector scheme', () => {
    const form = getForm();

    expect(form.controls.file.value.uuid).toBe('umbrella-uuid');
    expect(form.controls.file.value.file.name).toBe('umbrella-agreement.pdf');
    expect(form.controls.umaDate.value.toISOString().substring(0, 10)).toBe('2026-01-01');
    expect(form.controls.sectorDefinition.value).toBe('Current sector definition');
  });

  it('should validate required file, PDF type, future date and required sector definition', async () => {
    const form = getForm();

    form.controls.file.setValue(null);
    form.controls.file.updateValueAndValidity();
    await fixture.whenStable();

    expect(form.controls.file.errors).toEqual({ required: 'Select a file' });

    form.controls.file.setValue({
      file: new File(['text'], 'umbrella-agreement.txt', { type: 'text/plain' }),
    } as FileUploadEvent);
    form.controls.file.updateValueAndValidity();

    expect(form.controls.file.errors).toEqual({
      'validContentTypes-0': 'umbrella-agreement.txt must be a PDF',
    });

    form.controls.umaDate.setValue(new Date('2099-01-01T00:00:00.000Z'));
    form.controls.umaDate.updateValueAndValidity();

    expect(form.controls.umaDate.errors).toEqual({ invalidDate: 'The date cannot be in the future.' });

    form.controls.sectorDefinition.setValue(null);
    form.controls.sectorDefinition.updateValueAndValidity();

    expect(form.controls.sectorDefinition.errors).toEqual({ required: 'Enter the sector definition.' });
  });

  it('should submit the details update DTO and return to the scheme tab', () => {
    const form = getForm();
    form.controls.file.setValue({
      uuid: 'new-file-uuid',
      file: new File(['pdf'], 'new-agreement.pdf', { type: FileType.PDF }),
    });
    form.controls.umaDate.setValue(new Date('2026-02-03T00:00:00.000Z'));
    form.controls.sectorDefinition.setValue('Updated sector definition');

    component.onSubmit();

    expect(detailsUpdateService.updateSectorAssociationSchemeDetails).toHaveBeenCalledWith(10, {
      umbrellaAgreementUuid: 'new-file-uuid',
      umaDate: '2026-02-03',
      sectorDefinition: 'Updated sector definition',
    });
    expect(router.navigate).toHaveBeenCalledWith(['..'], { relativeTo: route, fragment: 'scheme' });
  });

  function getForm(): UmbrellaAgreementFormModel {
    return (component as unknown as { form: UmbrellaAgreementFormModel }).form;
  }
});
