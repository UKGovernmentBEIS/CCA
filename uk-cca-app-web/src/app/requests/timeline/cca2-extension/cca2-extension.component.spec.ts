import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { Cca2ExtensionComponent } from './cca2-extension.component';
import { cca2ExtensionActionStateMock } from './tests/mock-data';

describe('Cca2ExtensionComponent', () => {
  let component: Cca2ExtensionComponent;
  let fixture: ComponentFixture<Cca2ExtensionComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Cca2ExtensionComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(cca2ExtensionActionStateMock);

    fixture = TestBed.createComponent(Cca2ExtensionComponent);
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
