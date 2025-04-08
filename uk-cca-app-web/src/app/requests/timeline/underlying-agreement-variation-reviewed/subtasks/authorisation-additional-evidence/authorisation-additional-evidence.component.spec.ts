import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockAcceptedRequestActionState } from '../../testing/mock-data';
import { AuthorisationAdditionalEvidenceComponent } from './authorisation-additional-evidence.component';

describe('AuthorisationAdditionalEvidenceComponent', () => {
  let component: AuthorisationAdditionalEvidenceComponent;
  let fixture: ComponentFixture<AuthorisationAdditionalEvidenceComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthorisationAdditionalEvidenceComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockAcceptedRequestActionState);

    fixture = TestBed.createComponent(AuthorisationAdditionalEvidenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should match snapshot', () => {
    expect(fixture).toMatchSnapshot();
  });
});
