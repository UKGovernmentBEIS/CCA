import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';

import { PhaseBarComponent } from './phase-bar.component';

describe('PhaseBarComponent', () => {
  let component: PhaseBarComponent;
  let fixture: ComponentFixture<PhaseBarComponent>;
  let authStore: AuthStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PhaseBarComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserProfile({ firstName: 'Gimli', lastName: 'Gloin' });

    fixture = TestBed.createComponent(PhaseBarComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('isUserLoggedIn', true);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should only display the phase bar text if the user is logged in', () => {
    const compiled = fixture.nativeElement as HTMLElement;

    const phaseText = compiled.querySelector('govuk-phase-banner')?.textContent;
    expect(phaseText).toContain('This is a new service – your feedback will help us to improve it.');

    const userProfileText = compiled.querySelector('govuk-phase-banner span')?.textContent;
    expect(userProfileText?.trim()).toContain('You are logged in as: Gimli Gloin');

    fixture.componentRef.setInput('isUserLoggedIn', false);
    fixture.detectChanges();

    expect(phaseText).toContain('');
    expect(userProfileText).toContain('');
  });
});
