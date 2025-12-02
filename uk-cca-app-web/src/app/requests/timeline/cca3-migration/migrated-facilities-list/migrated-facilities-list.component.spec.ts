import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { cca3MigrationCompletedPayload } from '../testing/mock-data';
import { MigratedFacilitiesListComponent } from './migrated-facilities-list.component';

describe('MigratedFacilitiesListComponent', () => {
  let component: MigratedFacilitiesListComponent;
  let fixture: ComponentFixture<MigratedFacilitiesListComponent>;
  let componentRef: ComponentRef<MigratedFacilitiesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MigratedFacilitiesListComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(MigratedFacilitiesListComponent);
    component = fixture.componentInstance;

    componentRef = fixture.componentRef;
    componentRef.setInput('migratedFacilities', cca3MigrationCompletedPayload.facilityMigrationDataList);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
