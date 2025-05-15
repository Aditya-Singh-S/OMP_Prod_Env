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

  public productList: IProductDTO[] = []; // The original full product list
  public filteredList: IProductDTO[] = []; // The list after applying filters
  public isFilterActive: boolean = false; // Flag to track if a filter is active

  private searchResultsSubscription: Subscription | undefined;
  private invalidSearchSubscription: Subscription | undefined;

  showInvalidSearchMessage: boolean = false;
  noResultsMessage: string = '';

  constructor(
    private productService: ProductService,
    private router: Router,
  ) { }

  ngOnInit(): void {
    console.log('ProductComponent initialized');
    this.loadProducts();

    this.searchResultsSubscription = this.productService.searchResults$.subscribe(
      (results) => {
        this.filteredList = results;
        this.isFilterActive = true; // A search has been performed, so a filter is active
        this.showInvalidSearchMessage = false;
        this.noResultsMessage = results.length === 0 ? 'No products available according to the search filter.' : '';
        console.log('Search Results Received: ', this.filteredList);
      }
    );

    this.invalidSearchSubscription = this.productService.invalidSearch$.subscribe(() => {
      this.filteredList = [];
      this.isFilterActive = true; // Treat invalid search as a filter attempt
      this.showInvalidSearchMessage = true;
      this.noResultsMessage = 'Invalid search input. Please try again.';
    });
  }

  ngOnDestroy(): void {
    if (this.searchResultsSubscription) {
      this.searchResultsSubscription.unsubscribe();
    }
    if (this.invalidSearchSubscription) {
      this.invalidSearchSubscription.unsubscribe();
    }
  }

  loadProducts() {
    this.productService.getProductList().subscribe(response => {
      this.productList = response;
      this.filteredList = [...response]; // Initially, filtered list is the same as the product list
      this.isFilterActive = false; // No filter active on initial load
      this.showInvalidSearchMessage = false;
      console.log('Product List:', this.productList);
    },
    (error) => {
      console.error('Error loading products:', error);
      this.productList = [];
      this.filteredList = [];
      this.isFilterActive = false; // Ensure isFilterActive is false on error too
    });
  
  }

  viewProductDetails(productId: number) {
    this.router.navigate(['/product-details', productId]);
  }

}