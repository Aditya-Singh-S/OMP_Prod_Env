import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { IProductDTO } from '../model/class/interface/Products';


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

  private baseUrl = 'https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP'; // Update with your backend URL
  http: HttpClient;

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

    return this.http.post(`${this.baseUrl}/admin/addProduct`, formData);
  }

  updateProduct(name: string, upName: string, description: string, imageFile?: File, isActive?: boolean): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('name', name);
    if (upName) formData.append('upName', upName);
    if (description) formData.append('description', description);
    if (imageFile) formData.append('imageFile', imageFile);
    if(isActive!= undefined) formData.append('isActive', isActive.toString());

    return this.http.put(`${this.baseUrl}/admin/updateProduct/${name}`, formData);
  }

  uploadMultipleProducts(file: File): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    return this.http.post(`${this.baseUrl}/admin/uploadMultipleRecords`, formData);
  }

  getProductImageByName(name: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/product/imageByName/${name}`, { responseType: 'blob' });
  }


  private searchResultsSource = new BehaviorSubject<any[]>([]);
  searchResults$ = this.searchResultsSource.asObservable();
 
  setSearchResults(results: any[]) {
    this.searchResultsSource.next(results);
  }
 
  searchProduct(productName: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/searchProductByName`, { params: { productName } });
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
    return this.http.get<UserDetail[]>(`${this.baseUrl}/viewUsersSubscribedToProduct?productId=${productId}`);
  }

}