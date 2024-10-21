import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@netz/common/testing';

import { Configuration, SectorAssociationSchemeService } from 'cca-api';

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
});
