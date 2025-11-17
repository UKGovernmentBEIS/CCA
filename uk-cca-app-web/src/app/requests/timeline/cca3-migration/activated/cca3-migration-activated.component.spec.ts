import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { cca3MigrationActivatedActionStateMock } from '../tests/mock-data';
import { Cca3MigrationActivatedComponent } from './cca3-migration-activated.component';

describe('Cca3MigrationActivatedComponent', () => {
  let component: Cca3MigrationActivatedComponent;
  let fixture: ComponentFixture<Cca3MigrationActivatedComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Cca3MigrationActivatedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(cca3MigrationActivatedActionStateMock);

    fixture = TestBed.createComponent(Cca3MigrationActivatedComponent);
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
