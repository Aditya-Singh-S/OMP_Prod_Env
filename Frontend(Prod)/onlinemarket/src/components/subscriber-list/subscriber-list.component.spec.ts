import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubscriberListComponent } from './subscriber-list.component';

describe('SubscriberListComponent', () => {
  let component: SubscriberListComponent;
  let fixture: ComponentFixture<SubscriberListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriberListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubscriberListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
