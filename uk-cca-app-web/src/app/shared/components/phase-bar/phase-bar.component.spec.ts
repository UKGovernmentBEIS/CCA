import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AuthStore } from '@netz/common/auth';

import { PhaseBarComponent } from './phase-bar.component';

describe('PhaseBarComponent', () => {
  let component: PhaseBarComponent;
  let fixture: ComponentFixture<PhaseBarComponent>;
  let authStore: AuthStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PhaseBarComponent, RouterTestingModule],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserProfile({ firstName: 'Gimli', lastName: 'Gloin' });
  });

  beforeEach(() => {
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

    const userProfileText = compiled.querySelector('.logged-in-user')?.textContent;
    expect(userProfileText).toContain('You are logged in as: Gimli Gloin');

    fixture.componentRef.setInput('isUserLoggedIn', false);
    fixture.detectChanges();

    expect(phaseText).toContain('');
    expect(userProfileText).toContain('');
  });
});
