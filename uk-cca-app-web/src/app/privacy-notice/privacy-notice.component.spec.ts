import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { PrivacyNoticeComponent } from './privacy-notice.component';

describe('PrivacyNoticeComponent', () => {
  let component: PrivacyNoticeComponent;
  let fixture: ComponentFixture<PrivacyNoticeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrivacyNoticeComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrivacyNoticeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
