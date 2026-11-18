import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { Mocked } from 'vitest';

import {
  SectorAssociationSchemeDetailsUpdateService,
  SectorAssociationSchemesDTO,
  SubsectorAssociationSchemeDetailsUpdateService,
  SubsectorAssociationSchemesDTO,
} from 'cca-api';

import { SectorCommitmentComponent } from './sector-commitment.component';
import { SectorCommitmentFormModel } from './sector-commitment-form.provider';

describe('SectorCommitmentComponent', () => {
  let component: SectorCommitmentComponent;
  let fixture: ComponentFixture<SectorCommitmentComponent>;
  let route: ActivatedRouteStub;
  let router: Mocked<Router>;
  let detailsUpdateService: Mocked<SectorAssociationSchemeDetailsUpdateService>;
  let subsectorDetailsUpdateService: Mocked<SubsectorAssociationSchemeDetailsUpdateService>;

  const sectorScheme: SectorAssociationSchemesDTO = {
    sectorAssociationSchemeMap: {
      CCA_3: {
        id: 20,
        umbrellaAgreement: null,
        umaDate: '2026-01-01',
        sectorDefinition: 'Definition',
        schemeVersion: 'CCA_3',
        editable: true,
        targetSet: {
          id: 30,
          targetCurrencyType: 'Relative',
          energyOrCarbonUnit: 'kWh',
          targetCommitments: [
            { id: 3, targetPeriod: 'TP9 (2029)', targetImprovement: '0.00' },
            { id: 1, targetPeriod: 'TP7 (2026)', targetImprovement: '0.0125' },
            { id: 2, targetPeriod: 'TP8 (2027)', targetImprovement: '-0.025' },
          ],
        },
      },
    },
  };

  const subSector: SubsectorAssociationSchemesDTO = {
    name: 'Subsector',
    subsectorAssociationSchemeMap: {
      CCA_3: {
        id: 200,
        editable: true,
        targetSet: {
          id: 300,
          targetCurrencyType: 'Relative',
          energyOrCarbonUnit: 'kWh',
          targetCommitments: [
            { id: 13, targetPeriod: 'TP9 (2029)', targetImprovement: '0.00' },
            { id: 11, targetPeriod: 'TP7 (2026)', targetImprovement: '0.0125' },
            { id: 12, targetPeriod: 'TP8 (2027)', targetImprovement: '-0.025' },
          ],
        },
      },
    },
  };

  beforeEach(async () => {
    router = {
      navigate: vi.fn().mockResolvedValue(true),
    } as unknown as Mocked<Router>;
    detailsUpdateService = {
      updateSectorAssociationSchemeTargetCommitments: vi.fn().mockReturnValue(of(null)),
    } as unknown as Mocked<SectorAssociationSchemeDetailsUpdateService>;
    subsectorDetailsUpdateService = {
      updateSubsectorAssociationSchemeTargetCommitments: vi.fn().mockReturnValue(of(null)),
    } as unknown as Mocked<SubsectorAssociationSchemeDetailsUpdateService>;

    await TestBed.configureTestingModule({
      imports: [SectorCommitmentComponent],
      providers: [
        { provide: ActivatedRoute, useFactory: () => route },
        { provide: Router, useValue: router },
        { provide: SectorAssociationSchemeDetailsUpdateService, useValue: detailsUpdateService },
        { provide: SubsectorAssociationSchemeDetailsUpdateService, useValue: subsectorDetailsUpdateService },
      ],
    }).compileComponents();
  });

  it('should create', async () => {
    await createComponent({ sectorScheme });

    expect(component).toBeTruthy();
  });

  it('should prefill TP7 to TP9 commitments in target period order', async () => {
    await createComponent({ sectorScheme });

    const form = getForm();

    expect(form.controls.commitments.value).toEqual(['1.25', '-2.5', '0']);
    expect(document.body.textContent).toContain('TP7 (2026)');
    expect(document.body.textContent).toContain('TP8 (2027)');
    expect(document.body.textContent).toContain('TP9 (2029)');
  });

  it('should prefill whole-number percentages without floating point precision artifacts', async () => {
    const cca3Scheme = sectorScheme.sectorAssociationSchemeMap.CCA_3;

    await createComponent({
      sectorScheme: {
        sectorAssociationSchemeMap: {
          CCA_3: {
            ...cca3Scheme,
            targetSet: {
              ...cca3Scheme.targetSet!,
              targetCommitments: [{ id: 1, targetPeriod: 'TP7 (2026)', targetImprovement: '0.56000' }],
            },
          },
        },
      },
    });

    expect(getForm().controls.commitments.value).toEqual(['56']);
  });

  it('should validate commitment values with the shared message', async () => {
    await createComponent({ sectorScheme });

    const control = getForm().controls.commitments.at(0);
    const requiredMessage = 'Enter a target commitment';
    const message = 'Enter a numerical value, between - 100 and 100 with up to 3 decimal places';
    const targetCommitmentError = { numberInExclusiveRangeWithMaxDecimals: message };

    control.setValue(null);
    control.updateValueAndValidity();
    expect(control.errors).toEqual({ required: requiredMessage });

    control.setValue(150 as unknown as string);
    control.updateValueAndValidity();
    expect(control.errors).toEqual(targetCommitmentError);

    control.setValue(-200 as unknown as string);
    control.updateValueAndValidity();
    expect(control.errors).toEqual(targetCommitmentError);

    control.setValue('1.1234');
    control.updateValueAndValidity();
    expect(control.errors).toEqual(targetCommitmentError);

    control.setValue('150.1234');
    control.updateValueAndValidity();
    expect(control.errors).toEqual(targetCommitmentError);

    control.setValue('-100');
    control.updateValueAndValidity();
    expect(control.errors).toEqual(targetCommitmentError);

    control.setValue('100');
    control.updateValueAndValidity();
    expect(control.errors).toEqual(targetCommitmentError);

    control.setValue('-99.999');
    control.updateValueAndValidity();
    expect(control.errors).toBeNull();

    control.setValue('0');
    control.updateValueAndValidity();
    expect(control.errors).toBeNull();

    control.setValue('99.999');
    control.updateValueAndValidity();
    expect(control.errors).toBeNull();
  });

  it('should submit the commitments update DTO and return to the scheme tab', async () => {
    await createComponent({ sectorScheme });

    const form = getForm();
    form.controls.commitments.setValue(['3.125', '-4.5', '0']);

    component.onSubmit();

    expect(detailsUpdateService.updateSectorAssociationSchemeTargetCommitments).toHaveBeenCalledWith(20, {
      targetCommitments: [
        { id: 1, targetImprovement: '3.125' },
        { id: 2, targetImprovement: '-4.5' },
        { id: 3, targetImprovement: '0' },
      ],
    });
    expect(subsectorDetailsUpdateService.updateSubsectorAssociationSchemeTargetCommitments).not.toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['..'], { relativeTo: route, fragment: 'scheme' });
  });

  it('should submit subsector commitments to the subsector scheme endpoint', async () => {
    await createComponent({ subSector }, { sectorId: 1, subId: 10 });

    const form = getForm();
    form.controls.commitments.setValue(['58.888', '25.999', '0']);

    component.onSubmit();

    expect(subsectorDetailsUpdateService.updateSubsectorAssociationSchemeTargetCommitments).toHaveBeenCalledWith(200, {
      targetCommitments: [
        { id: 11, targetImprovement: '58.888' },
        { id: 12, targetImprovement: '25.999' },
        { id: 13, targetImprovement: '0' },
      ],
    });
    expect(detailsUpdateService.updateSectorAssociationSchemeTargetCommitments).not.toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['..'], { relativeTo: route });
  });

  async function createComponent(
    resolves: Record<string, unknown>,
    params: Record<string, unknown> = { sectorId: 1 },
  ): Promise<void> {
    route = new ActivatedRouteStub(params, null, resolves);
    fixture = TestBed.createComponent(SectorCommitmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  }

  function getForm(): SectorCommitmentFormModel {
    return (component as unknown as { form: SectorCommitmentFormModel }).form;
  }
});
