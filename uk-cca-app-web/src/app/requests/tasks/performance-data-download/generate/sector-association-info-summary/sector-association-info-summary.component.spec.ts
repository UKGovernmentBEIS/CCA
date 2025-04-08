import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SectorAssociationInfoSummaryComponent } from './sector-association-info-summary.component';

describe('SectorAssociationInfoSummaryComponent', () => {
  let component: SectorAssociationInfoSummaryComponent;
  let fixture: ComponentFixture<SectorAssociationInfoSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorAssociationInfoSummaryComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SectorAssociationInfoSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
