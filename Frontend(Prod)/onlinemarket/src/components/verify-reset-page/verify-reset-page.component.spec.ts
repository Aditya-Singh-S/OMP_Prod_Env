import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifyResetPageComponent } from './verify-reset-page.component';

describe('VerifyResetPageComponent', () => {
  let component: VerifyResetPageComponent;
  let fixture: ComponentFixture<VerifyResetPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifyResetPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VerifyResetPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
