import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SearchfilterComponent } from "../searchfilter/searchfilter.component";
import { ProductService } from '../../services/product.service';
import { Subscription } from 'rxjs';
import { IProductDTO } from '../../model/class/interface/Products'; // Import your interface

@Component({
  selector: 'app-products',
  imports: [CommonModule, RouterModule, FormsModule, SearchfilterComponent, ReactiveFormsModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css',
  providers: [ProductService]
})
export class ProductsComponent implements OnInit, OnDestroy {

  public productList: IProductDTO[] = []; // Use your interface here
  // private searchResults: Subscription | undefined;
  private searchResultsSubscription: Subscription | undefined;
  private invalidSearchSubscription: Subscription | undefined;
  product: any;

  showNoResultsMessage: boolean = false;
  noResultsMessage: string = '';

  constructor(
    private productService: ProductService,
    private router: Router,
  ) { }

  ngOnInit(): void {
    console.log('ProductComponent initialized');
    this.productService.getProductList().subscribe(response => {
      this.productList = response;
      console.log('Product List:', this.productList);
    });

    this.searchResultsSubscription = this.productService.searchResults$.subscribe(
      (results) => {
        this.productList = results;
        this.showNoResultsMessage = results.length === 0;
        this.noResultsMessage = results.length === 0 ? 'No products available according to the search filter.' : '';
        console.log('Search Results Received: ', this.productList);
      }
    );

    this.invalidSearchSubscription = this.productService.invalidSearch$.subscribe(() => {
      this.productList = [];
      this.showNoResultsMessage = true;
      this.noResultsMessage = 'Invalid search input. Please try again.';
    });

  }

  ngOnDestroy(): void {
    // if (this.searchResults) {
    //   this.searchResults.unsubscribe();
    // }

    if (this.searchResultsSubscription) {
      this.searchResultsSubscription.unsubscribe();
    }
    if (this.invalidSearchSubscription) {
      this.invalidSearchSubscription.unsubscribe();
    }
  }
  viewProductDetails(productId: number) {
    this.router.navigate(['/product-details', productId]);
  }
}