import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap, BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { CookieServiceService } from './cookie-service.service';
import { IUserDetails, IUserIdResponse, IProductDTO, IRatingDTO } from '../model/class/interface/Products';

@Injectable({
  providedIn: 'root'
})

export class UserService {
  private baseUrl = 'https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP';

  private userIdSubject = new BehaviorSubject<number | null>(null);
  userId$ = this.userIdSubject.asObservable();
  private isAdminSubject = new BehaviorSubject<boolean>(false);
  isAdmin$ = this.isAdminSubject.asObservable();
  userDetails: IUserDetails | null = null;
  private _userId: number | null = null; // Private backing field for userId

  constructor(private http: HttpClient, private cookieService: CookieServiceService, private router: Router) {
    this.loadUserIdFromLocalStorage();
    this.loadUserDetailsFromLocalStorage();
    this.userId$.subscribe(userId => {
      if (userId) {
        this.fetchAndUpdateAdminStatus(userId);
      } else {
        this.isAdminSubject.next(false);
      }
    });
  }

  private fetchAndUpdateAdminStatus(userId: number): void {
    this.getUserDetails(userId).subscribe({
      next: (details) => {
        this.isAdminSubject.next(details?.userRole === 'ADMIN');
        this.userDetails = details; // Update userDetails
        localStorage.setItem('userDetails', JSON.stringify(details)); // Keep local storage in sync
      },
      error: () => {
        this.isAdminSubject.next(false);
        this.userDetails = null;
        localStorage.removeItem('userDetails');
      }
    });
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/login`, { email, password });
  }

  getUserIdByEmail(email: string): Observable<IUserIdResponse> {
    const params = new HttpParams().set('emailId', email);
    return this.http.get<IUserIdResponse>(`${this.baseUrl}/getUserIdByEmail`, { params }).pipe(
      tap(response => this.setUserId(response)) // Set userId on successful retrieval
    );
  }

  getUserDetails(userId: number): Observable<IUserDetails> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.get<IUserDetails>(`${this.baseUrl}/myDetails`, { params });
  }

  getProductSubscriptionList(userId: number): Observable<IProductDTO[]> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.get<IProductDTO[]>(`${this.baseUrl}/getProductSubscriptionList`, { params });
  }

  getProductRatingList(userId: number): Observable<IRatingDTO[]> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.get<IRatingDTO[]>(`${this.baseUrl}/reviews/user/` + userId, { params });
  }

  register(formData: FormData): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, formData, {
      headers: {
        'Accept': 'application/json'
      }
    });
  }

  updateUser(userId: any, formData: FormData): Observable<any> {
    return this.http.put(`${this.baseUrl}/updateUser/${userId}`, formData, {
      headers: {
        'Accept': 'application/json'
      }
    });
  }

  addReview(productId: number, userId: number, rating: number, review: string, reviewActiveStatus: boolean): Observable<any> {
    const params = new HttpParams()
      .set('productId', productId.toString())
      .set('userId', userId.toString())
      .set('rating', rating.toString())
      .set('review', review)
      .set('reviewActiveStatus', reviewActiveStatus.toString());

    return this.http.post<any>(`${this.baseUrl}/reviews/createReview`, null, { params: params });
  }

  addSubscription(userId: number, productId: number): Observable<any> {
    const params = new HttpParams()
      .set('userId', userId.toString())
      .set('productId', productId.toString());
    return this.http.post<any>(`${this.baseUrl}/addSubscription`, null, { params: params });
  }

  removeSubscription(userId: number, productId: number): Observable<any> {
    const params = new HttpParams()
      .set('userId', userId.toString())
      .set('productId', productId.toString());
    return this.http.put<any>(`${this.baseUrl}/removeSubscription`, null, { params: params });
  }

  handleLoginSuccess(email: string): void {
    this.cookieService.setCookie(email);
    console.log('Login successful, fetching user ID for:', email);
    this.getUserIdByEmail(email).subscribe({
      next: (response: IUserIdResponse) => {
        this.setUserId(response); // Use the setter
        localStorage.setItem('userId', response.toString());
        console.log('User ID stored in local storage:', response);
        this.router.navigate(['/home']).then(() => {
          window.location.reload(); // Consider if full reload is necessary
        });
      },
      error: (err) => {
        console.error('Error fetching user ID:', err);
      },
    });
  }

  loadUserIdFromLocalStorage(): void {
    const storedUserId = localStorage.getItem('userId');
    if (storedUserId) {
      this.setUserId(parseInt(storedUserId, 10)); // Use the setter
      console.log('User ID loaded from local storage:', this._userId);
    }
  }

  loadUserDetailsFromLocalStorage(): void {
    const storedUserDetails = localStorage.getItem('userDetails');
    if (storedUserDetails) {
      try {
        this.userDetails = JSON.parse(storedUserDetails);
        console.log('User details loaded from local storage:', this.userDetails);
        this.isAdminSubject.next(this.userDetails?.userRole === 'ADMIN'); // Set initial admin status
      } catch (error) {
        console.error('Error parsing user details from local storage:', error);
        localStorage.removeItem('userDetails');
        this.isAdminSubject.next(false);
      }
    } else {
      this.isAdminSubject.next(false);
    }
  }

  saveUserDetailsToLocalStorage(details: IUserDetails): void {
    localStorage.setItem('userDetails', JSON.stringify(details));
    this.userDetails = details;
    console.log('User details saved to local storage:', this.userDetails);
  }

  clearUserDetails(): void {
    this.userDetails = null;
    localStorage.removeItem('userDetails');
  }

  setUserId(userId: number | null): void {
    this.userIdSubject.next(userId);
    this._userId = userId; // Update the backing field
    if (userId) {
      this.fetchAndUpdateAdminStatus(userId); // Fetch admin status when userId is set
    } else {
      this.isAdminSubject.next(false);
    }
  }

  get userId(): number | null {
    return this._userId; // Return the value of the backing field
  }

  watchUserId(): Observable<number | null> {
    return this.userId$;
  }

}