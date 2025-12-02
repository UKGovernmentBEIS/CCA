import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { cca3MigrationCompletedActionStateMock } from '../testing/mock-data';
import { Cca3MigrationCompletedComponent } from './cca3-migration-completed.component';

describe('Cca3MigrationCompletedComponent', () => {
  let component: Cca3MigrationCompletedComponent;
  let fixture: ComponentFixture<Cca3MigrationCompletedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Cca3MigrationCompletedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(cca3MigrationCompletedActionStateMock);

    fixture = TestBed.createComponent(Cca3MigrationCompletedComponent);
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
