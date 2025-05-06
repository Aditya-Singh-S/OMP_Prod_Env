import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { HttpClientModule } from '@angular/common/http';
import * as XLSX from 'xlsx';
import { AdminUserListPopupComponent } from '../admin-user-list-popup/admin-user-list-popup.component';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-admin-dashboard',
  imports: [FormsModule, CommonModule, HttpClientModule, AdminUserListPopupComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  providers: [ProductService]
})
export class AdminDashboardComponent implements OnInit, OnDestroy {
   // Single Add Product
   productName: string = '';
   productDescription: string = '';
   isActive: boolean = false;
   selectedImageFile: File | null = null;
   imagePreview: string | null = null;
   showAddProductPopup: boolean = false;
 
   // Bulk Upload
   showAddMultipleProductsPopup: boolean = false;
   bulkProductActive: boolean = false;
   bulkFile: File | null = null;
 
   // Update Product
   showUpdatePopup: boolean = false;
   searchId: string = '';
   productFound = false;
   previewImage: string | ArrayBuffer | null = null;
   product = {
     id: '',
     name: '',
     description: '',
     active: false,
     image: null,
     imageUrl: ''
   };
 
   // Get Users Popup
   showGetUsersPopup: boolean = false;
  isAdminLoggedIn: boolean = false; // Renamed for clarity
  isAdminSubscription$: Subscription | undefined;
  isLoading: boolean = true; // To handle potential delays in fetching admin status

  constructor(private productService: ProductService, private userService: UserService, private router: Router) { }

  ngOnInit(): void {
    this.isAdminSubscription$ = this.userService.isAdmin$.subscribe(isAdmin => {
      this.isAdminLoggedIn = isAdmin; // Update the boolean based on the observable
      this.isLoading = false;
      if (!this.isAdminLoggedIn) {
        this.router.navigate(['/home']); // Redirect if not admin
      }
    });
  }

  ngOnDestroy(): void {
    if (this.isAdminSubscription$) {
      this.isAdminSubscription$.unsubscribe();
    }
  }

  // Methods for Add Product popup
  openAddProductPopup() {
    this.showAddProductPopup = true;
  }

  closeAddProductPopup() {
    this.showAddProductPopup = false;
    this.resetAddProductForm();
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedImageFile = file;
      const reader = new FileReader();
      reader.onload = e => this.imagePreview = reader.result as string;
      reader.readAsDataURL(file);
    }
  }

  removeImage() {
    this.selectedImageFile = null;
    this.imagePreview = null;
  }

  submitProduct() {
    if (this.selectedImageFile) {
      this.productService.addProduct(this.productName, this.productDescription, this.selectedImageFile, this.isActive)
        .subscribe(response => {
          alert('Product added successfully');
          this.closeAddProductPopup();
        }, error => {
          console.error('Error adding product:', error);
        });
    }
  }

  resetAddProductForm() {
    this.productName = '';
    this.productDescription = '';
    this.isActive = false;
    this.selectedImageFile = null;
    this.imagePreview = null;
  }

  // Methods for Bulk Upload popup
  openAddMultipleProductsPopup() {
    this.showAddMultipleProductsPopup = true;
  }

  closeAddMultipleProductsPopup() {
    this.showAddMultipleProductsPopup = false;
    this.bulkFile = null;
    this.bulkProductActive = false;
  }

  onBulkFileChange(event: any) {
    this.bulkFile = event.target.files[0];
  }

  submitBulkProducts() {
    if (this.bulkFile) {
      this.productService.uploadMultipleProducts(this.bulkFile)
        .subscribe(response => {
          alert('Multiple products added successfully');
          this.closeAddMultipleProductsPopup();
        }, error => {
          console.error('Error uploading multiple products:', error);
        });
    }
  }

  openUpdateProductPopup() {
    this.showUpdatePopup = true;
    this.searchId = '';
    this.productFound = false;
    this.previewImage = null;
  }
  searchProduct() {
    this.productService.searchProduct(this.product.name)
      .subscribe(response => {
        if (response.length > 0) {
          this.product = response[0]; // Assuming you want the first matching product
          this.productService.getProductImageByName(this.product.name)
            .subscribe(imageBlob => {
              const reader = new FileReader();
              reader.onload = () => {
                this.previewImage = reader.result as string;
              };
              reader.readAsDataURL(imageBlob);
            });
          this.productFound = true;
        } else {
          alert('Product not found!');
          this.productFound = false;
        }
      }, error => {
        alert('Product not found!');
        this.productFound = false;
      });
  }
  onUpdateFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.previewImage = reader.result;
        this.product.image = file;
      };
      reader.readAsDataURL(file);
    }
  }


  updateProduct() {
    const imageFile = this.product.image !== null ? this.product.image : undefined;
    this.productService.updateProduct(this.product.name, this.product.name, this.product.description, imageFile, this.product.active)
      .subscribe(response => {
        alert('Product updated successfully!');
        this.showUpdatePopup = false;
      }, error => {
        console.error('Error updating product:', error);
      });
  }
  closeUpdateProductPopup() {
    this.showUpdatePopup = false;
  }

  // Methods for Get Users popup
  openGetUsersPopup() {
    this.showGetUsersPopup = true;
  }

  closeGetUsersPopup() {
    this.showGetUsersPopup = false;
  }
}