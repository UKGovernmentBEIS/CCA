import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@netz/common/testing';

import { Configuration, SectorAssociationSchemeService } from 'cca-api';

import { toSectorSchemeSummaryData } from '../scheme-summary-data';
import { SectorDocumentsDownloadComponent } from './sector-documents-download.component';

describe('SectorDocumentsDownloadComponent', () => {
  let component: SectorDocumentsDownloadComponent;
  let fixture: ComponentFixture<SectorDocumentsDownloadComponent>;
  let sectorAssociationSchemeService: jest.Mocked<SectorAssociationSchemeService>;
  let configuration: jest.Mocked<Configuration>;

  beforeEach(async () => {
    configuration = mockClass(Configuration);
    configuration.basePath = 'api';

    sectorAssociationSchemeService = mockClass(SectorAssociationSchemeService);
    sectorAssociationSchemeService.generateGetSectorAssociationSchemeDocumentToken.mockReturnValue(
      of({ token: 'token', tokenExpirationMinutes: 1 }),
    );

    const activatedRoute = new ActivatedRouteStub({ id: 1, uuid: 'uuid' });

    await TestBed.configureTestingModule({
      imports: [SectorDocumentsDownloadComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: SectorAssociationSchemeService, useValue: sectorAssociationSchemeService },
        { provide: Configuration, useValue: configuration },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SectorDocumentsDownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the download link', async () => {
    expect(component.downloadURL()).toEqual('api/v1.0/sector-documents/document/token');
  });

  describe('toSectorSchemeSummaryData', () => {
    it('should sort target periods correctly when given 10 unsorted target periods', () => {
      const mockSectorScheme: any = {
        sectorAssociationSchemeMap: {
          CCA_2: {
            id: 1,
            umbrellaAgreement: null,
            targetSet: {
              targetCurrencyType: 'Relative',
              throughputUnit: 'tonne',
              energyOrCarbonUnit: 'kWh',
              targetCommitments: [
                { targetPeriod: 'TP10', targetImprovement: '0.10' },
                { targetPeriod: 'TP01', targetImprovement: '0.01' },
                { targetPeriod: 'TP05', targetImprovement: '0.05' },
                { targetPeriod: 'TP02', targetImprovement: '0.02' },
                { targetPeriod: 'TP08', targetImprovement: '0.08' },
                { targetPeriod: 'TP03', targetImprovement: '0.03' },
                { targetPeriod: 'TP09', targetImprovement: '0.09' },
                { targetPeriod: 'TP04', targetImprovement: '0.04' },
                { targetPeriod: 'TP06', targetImprovement: '0.06' },
                { targetPeriod: 'TP07', targetImprovement: '0.07' },
              ],
            },
          },
        },
      };

      const summaryData = toSectorSchemeSummaryData(mockSectorScheme, 0);

      // Find the section with Sector commitment
      const sectorCommitmentSection = summaryData.find((section) => section.header === 'Sector commitment');

      // Extract the target period data - the rows are directly in the section
      const targetPeriodRows = sectorCommitmentSection.data;

      // Check that target periods are sorted correctly
      const expectedOrder = ['TP01', 'TP02', 'TP03', 'TP04', 'TP05', 'TP06', 'TP07', 'TP08', 'TP09', 'TP10'];
      const actualOrder = targetPeriodRows.map((row) => row.key);

      expect(actualOrder).toEqual(expectedOrder);
    });
  });
});
