import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';

import { mockRequestActionState } from '../../testing/mock-data';
import { FacilityComponent } from './facility.component';

describe('FacilityComponent', () => {
  let component: FacilityComponent;
  let fixture: ComponentFixture<FacilityComponent>;
  let store: RequestActionStore;

  const route: any = { snapshot: { params: { facilityId: 'ADS_2-F00029' }, pathFromRoot: [] } };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacilityComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockRequestActionState);

    fixture = TestBed.createComponent(FacilityComponent);
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
