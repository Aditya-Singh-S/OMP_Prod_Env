import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { IProductDTO, IReview } from '../model/class/interface/Products';


interface ProductView {
  productid: number;
  name: string;
  description: string;
  avg_rating: number;
  subscription_count: number;
}
interface UserDetail {
  firstName: string;
  lastName: string;
  email: string;
  addressLine1: string;
  addressLine2: string;
  dateOfBirth: string;
  postalCode: number;
  userId: number;

}

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private baseUrl = 'http://localhost:9090/OMP'; // Update with your backend URL
  http: HttpClient;

  private searchResultsSource = new Subject<any[]>();
  searchResults$ = this.searchResultsSource.asObservable();
  private invalidSearchSubject = new Subject<void>(); // New Subject for invalid searches
  public invalidSearch$ = this.invalidSearchSubject.asObservable();
  
  token :any = localStorage.getItem('authToken');
  // console.log("Token = ", token);
    authHeaders = new HttpHeaders({
    Authorization: `Basic ${this.token}`
  });

  constructor(http: HttpClient) {
    this.http = http;
  }

  getTopSubscribedProducts(): Observable<IProductDTO[]> {
    return this.http.get<IProductDTO[]>(`${this.baseUrl}/topSubscribedProduct`);
  }

  getTopRatedProducts(): Observable<IProductDTO[]> {
    return this.http.get<IProductDTO[]>(`${this.baseUrl}/topRatedProducts`);
  }

  getProductList(): Observable<any> {
    return this.http.get(`${this.baseUrl}/viewAllProducts`);
  }

  

  addProduct(name: string, description: string, imageFile: File, isActive: boolean): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('name', name);
    formData.append('description', description);
    formData.append('imageFile', imageFile);
    formData.append('isActive',isActive.toString());

    return this.http.post(`${this.baseUrl}/admin/addProduct`, formData, {headers:this.authHeaders});
  }

  updateProduct(name: string, upName: string, description: string, imageFile?: File, isActive?: boolean): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('name', name);
    if (upName) formData.append('upName', upName);
    if (description) formData.append('description', description);
    if (imageFile) {
      formData.append('file', imageFile, imageFile.name);
    }
    if (isActive !== undefined) formData.append('isActive', isActive.toString());
 
    return this.http.put(`${this.baseUrl}/admin/updateProduct/${name}`, formData, {headers : this.authHeaders});
  }

  uploadMultipleProducts(file: File,bulkProductisactive : boolean): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    const params = new HttpParams().set('bulkProductisactive', bulkProductisactive);
    return this.http.post(`${this.baseUrl}/admin/uploadMultipleRecords`, formData,{headers : this.authHeaders, params :params});
  }

  getProductImageByName(name: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/product/imageByName/${name}`,{headers : this.authHeaders, responseType: 'blob'});
  }


  // private searchResultsSource = new BehaviorSubject<any[]>([]);
  // searchResults$ = this.searchResultsSource.asObservable();
  
  signalInvalidSearch() {
    this.invalidSearchSubject.next();
  }

  setSearchResults(results: any[]) {
    this.searchResultsSource.next(results);
  }
 
  searchProduct(productName: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/searchProductByName`, { headers: this.authHeaders,params: { productName } });
  }


  searchProductByName(productName: string): Observable<ProductView[]>{
    return this.http.get<[]>(`${this.baseUrl}/searchProductByName?productName=${productName}`);
  }
 
  searchProductBySubsCount(count: number) {
    return this.http.get<[]>(`${this.baseUrl}/searchProductBySubsCount?count=${count}`);
  }
 
  searchProductByRating(rating: number) {
    return this.http.get<[]>(`${this.baseUrl}/searchProductByRating?rating=${rating}`);
  }
 
  searchProductBySubsCountAndRating(count: number, rating: number) {
    return this.http.get<[]>(`${this.baseUrl}/searchProductBySubsCountAndRating?count=${count}&rating=${rating}`);
  }
 
  searchProductByNameAndSubsCount(productName: string, count: number) {
    return this.http.get<[]>(`${this.baseUrl}/searchProductByNameAndSubsCount?name=${productName}&count=${count}`);
  }
 
  searchProductByNameAndRating(productName: string, rating: number) {
    return this.http.get<[]>(`${this.baseUrl}/searchProductByNameAndRating?name=${productName}&rating=${rating}`);
  }
 
  searchProductByNameSubsRating(productName: string, rating: number, count: number) {
    return this.http.get<[]>(`${this.baseUrl}/searchProductByNameSubsRating?name=${productName}&rating=${rating}&count=${count}`);
  }
  viewProductDetails(productId: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/viewProductDetails/${productId}`);
  }
 
  getProductSubscriptionList(productId: number): Observable<UserDetail[]> {
    return this.http.get<UserDetail[]>(`${this.baseUrl}/viewUsersSubscribedToProduct?productId=${productId}`,{ headers: this.authHeaders});
  }

  updateUserReviews(userId: number, reviews: IReview[]): Observable<any> {
    const url = `${this.baseUrl}/reviews/updateReview`; // Adjust your backend API endpoint
    return this.http.put(url,reviews,{ headers : this.authHeaders}); // Send the array of updated reviews in the request body
  }

  updateReviewStatus(reviewId: number, userId: number | null, isActive: boolean): Observable<any> {
    const params = new HttpParams()
      .set('ratingId', reviewId.toString())
      .set('userId', userId ? userId.toString() : '')
      .set('reviewActiveStatus', isActive.toString());

    return this.http.put(`${this.baseUrl}/reviews/updateReview`, null, { headers : this.authHeaders,params : params }); // Reusing your existing update endpoint
  }

  registerUser(formData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/admin/register`, formData, {headers : this.authHeaders});
  }

}