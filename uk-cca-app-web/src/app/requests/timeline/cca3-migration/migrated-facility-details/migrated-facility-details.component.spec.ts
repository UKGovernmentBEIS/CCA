import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';

import { cca3MigrationCompletedActionStateMock } from '../testing/mock-data';
import { MigratedFacilityDetailsComponent } from './migrated-facility-details.component';

describe('MigratedFacilityDetailsComponent', () => {
  let component: MigratedFacilityDetailsComponent;
  let fixture: ComponentFixture<MigratedFacilityDetailsComponent>;
  let actionStore: RequestActionStore;

  const route = { snapshot: { params: { facilityId: 'ADS_1-F00001' } } };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MigratedFacilityDetailsComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(cca3MigrationCompletedActionStateMock);

    fixture = TestBed.createComponent(MigratedFacilityDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
