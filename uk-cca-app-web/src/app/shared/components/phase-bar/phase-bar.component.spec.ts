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
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
