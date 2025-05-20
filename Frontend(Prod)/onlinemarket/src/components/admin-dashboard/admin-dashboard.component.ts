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
import { AdminUpdateUserPopupComponent } from '../admin-update-user-popup/admin-update-user-popup.component';
import { AuthService } from '../../services/auth.service';
 
interface IUserDetails {
  userID: string | number;
  email: string;
  isActive: boolean;
  firstName?: string;
  lastName?: string;
  dateOfBirth?: string;
  contactNumber?: string;
  postalCode?: string;
  addressLine1?: string;
  addressLine2?: string;
  [key: string]: any;
}
 
@Component({
  selector: 'app-admin-dashboard',
  imports: [FormsModule, CommonModule, HttpClientModule, AdminUserListPopupComponent, AdminUpdateUserPopupComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
  providers: [ProductService, UserService]
})
export class AdminDashboardComponent implements OnInit, OnDestroy {
  // Single Add Product
  productName: string = '';
  productDescription: string = '';
  isActive: boolean = false;
  selectedImageFile: File | null = null;
  imagePreview: string | null = null;
  showAddProductPopup: boolean = false;
  duplicateProductNameError: boolean = false; // For duplicate name validation
  imageRequiredError: boolean = false;
  invalidFileTypeError: boolean = false;
  productAdded:boolean=false;
  popupMessage:string='';
  popupTitle:string='';
  namePattern = /^[a-zA-Z][a-zA-Z0-9\s]{0,49}$/;
  productNameError:boolean=false;
  descriptionError:boolean=false;
 
  // Bulk Upload
  showAddMultipleProductsPopup: boolean = false;
  bulkProductisactive: boolean = false;
  bulkFile: File | null = null;
  invalidBulkFileTypeError:boolean = false;
  fileRequiredError: boolean = false;
  showSuccessPopup: boolean = false;
  showErrorPopup: boolean = false;
 
  // Update Product
  showUpdatePopup: boolean = false;
  productUpdated:boolean=false;
  searchId: string = '';
  productFound = false;
  previewImage: string | ArrayBuffer | null = null;
  product: {
    id: string;
    name: string;
    upName: string;
    upDescription: string;
    isActive: boolean;
    image: File | null;
    imageUrl: string;
  } = {
    id: '',
    name: '',
    upName: '',
    upDescription: '',
    isActive: undefined as any,
    image: null,
    imageUrl: ''
  };
 
  // Get Users Popup
  showGetUsersPopup: boolean = false;
  isAdminLoggedIn: boolean = false; // Renamed for clarity
  isAdminSubscription$: Subscription | undefined;
  isLoading: boolean = true; // To handle potential delays in fetching admin status
 
  // update user popup code
  isUpdateUserPopupVisible: boolean = false;
  
  showAlertPopup : boolean = false;
  AlertPopupTitle : string = "";
  AlertPopupMessage : string = "";
 
  constructor(private productService: ProductService, private userService: UserService, private authService: AuthService, private router: Router) { }
 
  ngOnInit(): void {
    console.log (this.product.isActive);
    this.isAdminSubscription$ = this.userService.isAdmin$.subscribe(isAdmin => {
      this.isAdminLoggedIn = isAdmin; // Update the boolean based on the observable
      this.isLoading = false;
      // if (!this.isAdminLoggedIn) {
      //   this.router.navigate(['/home']); // Redirect if not admin
      // }
    });
  }
 
  checkDuplicateProductName(name: string): void {
    if (name && name.trim() !== '') {
      this.productService.searchProduct(name).subscribe(
        (products) => {
          this.duplicateProductNameError = products && products.length > 0;
        },
        (error) => {
          console.error('Error checking product name:', error);
          this.duplicateProductNameError = false; // Assume no duplicate on error
        }
      );
    } else {
      this.duplicateProductNameError = false;
    }
  }
 
  ngOnDestroy(): void {
    if (this.isAdminSubscription$) {
      this.isAdminSubscription$.unsubscribe();
    }
  }
 
  openAddProductPopup() {
    this.showAddProductPopup = true;
  }
 
  closeAddProductPopup() {
    this.showAddProductPopup = false;
    this.resetAddProductForm();
    this.duplicateProductNameError = false;
  }
 
  onProductNameInput(event: Event) {
    const inputValue = (event.target as HTMLInputElement).value;
    this.productNameError = !this.namePattern.test(inputValue);
    this.duplicateProductNameError = false;
    this.checkDuplicateProductName(inputValue);
  }
 
  onFileSelected(event: any) {
    const file = event.target.files[0];
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
    if (file ){
      if(allowedTypes.includes(file.type)) {
      this.selectedImageFile = file;
      this.imageRequiredError = false;
      this.invalidFileTypeError = false;
      const reader = new FileReader();
      reader.onload = e => this.imagePreview = reader.result as string;
      reader.readAsDataURL(file);
      }
      else{
        this.invalidFileTypeError = true;
      }
    }
  }
 
  removeImage() {
    this.selectedImageFile = null;
    this.imagePreview = null;
  }
 
  submitProduct() {
   
  this.imageRequiredError = !this.selectedImageFile;
  this.invalidFileTypeError=this.invalidFileTypeError;
 
  this.productNameError = false;
  this.descriptionError = false;
 
  if (!this.productName || !this.productName.trim()) {
    this.productNameError = true;
  }
 
  if (!this.productDescription || this.productDescription.length < 100) {
    this.descriptionError = true;
  }
    if (this.selectedImageFile && !this.duplicateProductNameError) {
      this.productService.addProduct(this.productName, this.productDescription, this.selectedImageFile, this.isActive)
        .subscribe(response => {
          //alert('Product added successfully');
          this.productAdded=true;
          this.popupTitle="Success"
          this.popupMessage="Product added successfully!";
          this.closeAddProductPopup();
        }, error => {
          if (error && error.error && error.error.message && error.error.message.includes("Duplicate entry") && error.error.message.includes("products.name")) {
            this.duplicateProductNameError = true;
          } else {
            console.error('Error adding product:', error);
            //alert('Error adding product. Please try again.');
          this.productAdded=true;
          this.popupTitle="Error"
          this.popupMessage="Error adding product. Please try again.";
          }
        });
    }
}
 
  resetAddProductForm() {
    this.productName = '';
    this.productDescription = '';
    this.isActive = false;
    this.selectedImageFile = null;
    this.imagePreview = null;
    this.duplicateProductNameError = false;
  }
 
  closeAddPopup(){
    this.productAdded=false;
  }
 
  // Methods for Bulk Upload popup
  openAddMultipleProductsPopup() {
    this.showAddMultipleProductsPopup = true;
  }
 
  closeAddMultipleProductsPopup() {
    this.showAddMultipleProductsPopup = false;
    this.bulkFile = null;
    this.bulkProductisactive = false;
  }
 
  onBulkFileChange(event: any) {
    this.bulkFile = event.target.files[0];
    const allowedTypes = [
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' // for .xlsx
    ];
    if(this.bulkFile)
    {
      if(allowedTypes.includes(this.bulkFile.type))
      {
        this.fileRequiredError=false;
        this.invalidBulkFileTypeError=false;
      }
      else{
        this.invalidBulkFileTypeError=true;
      }
    }
  }
 
  submitBulkProducts() {
    this.fileRequiredError = !this.bulkFile;
 
    // Keeping your validation logic unchanged
    if (this.fileRequiredError || this.invalidBulkFileTypeError) {
      return;
    }
 
    if (this.bulkFile) {
      this.productService.uploadMultipleProducts(this.bulkFile, this.bulkProductisactive)
        .subscribe(response => {
          this.closeAddMultipleProductsPopup();
          this.showSuccessPopup = true; // Show success popup
        }, error => {
          console.error('Error uploading multiple products:', error);
          this.showErrorPopup = true; // Trigger error popup
        });
    }
  }
 closeErrorPopup() {
    this.showErrorPopup = false;
  }
 closeSuccessPopup() {
    this.showSuccessPopup = false;
  }
  removeBulkFile() {
    this.bulkFile = null; // Clears the selected file
    this.fileRequiredError = false; // Resets validation errors
    this.invalidBulkFileTypeError = false; // Resets file type error
   
    // Reset the file input field itself
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = ''; // Clears the file input
    }
  }
 
  openUpdateProductPopup() {
    this.showUpdatePopup = true;
    this.searchId = '';
    this.productFound = false;
    this.previewImage = null;
  }
 
  searchProduct() {
    if (this.product.name && this.product.name.trim() !== '') {
      this.productService.searchProduct(this.product.name.trim())
        .subscribe(response => {
          if (response.length > 0 && response[0].name.toLowerCase() === this.product.name.trim().toLowerCase()) {
            this.product = response[0];
            this.product.upName = response[0].name;
            this.product.upDescription = response[0].description;
            this.product.isActive = response[0].isactive;
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
           // alert('Product not found!');
            this.AlertPopupTitle = "Warning";
            this.AlertPopupMessage = "Product not found!";
            this.showAlertPopup = true;
            this.productFound = false;
            this.resetUpdateProductForm();
          }
        }, error => {
          //alert('Error searching for product.');
          this.AlertPopupTitle = "Warning";
          this.AlertPopupMessage = "Error searching for product.";
          this.showAlertPopup = true;
          this.productFound = false;
          this.resetUpdateProductForm(); // Optionally reset the form on error
        });
    } else {
      //alert('Please enter a product name to search.');
      this.AlertPopupTitle = "Warning";
      this.AlertPopupMessage = "Please enter a product name to search !";
      this.showAlertPopup = true;

      this.productFound = false;
      this.resetUpdateProductForm(); // Optionally reset the form if no search term is entered
    }
  }
 
  onUpdateFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      //this.selectedImageFile = file;
      //this.imagePreview = URL.createObjectURL(file);
      //this.imageRequiredError = false;
      const reader = new FileReader();
      reader.onload = () => {
        this.previewImage = reader.result;
        this.product.image = file;
      };
      reader.readAsDataURL(file);
    } else {
      this.product.image = null;
      this.previewImage = this.product.imageUrl;
    }
  }
 
  updateProduct() {
    const imageFile = this.product.image !== null ? this.product.image : undefined;
    this.productService.updateProduct(
      this.product.name,
      this.product.upName,
      this.product.upDescription,
      imageFile,
      this.product.isActive ? true: false
     
    ).subscribe(response => {
      //alert('Product updated successfully!');
     
      this.showUpdatePopup = false;
      this.resetUpdateProductForm();
      if (response && response.imageUrl) {
        this.product.imageUrl = response.imageUrl;
        this.previewImage = response.imageUrl;
      }
 
      this.productUpdated=true;
      this.popupTitle="Success"
      this.popupMessage="Product updated successfully!";
 
    }, error => {
      console.error('Error updating product:', error);
      this.productUpdated=true;
      this.popupTitle="Error"
      this.popupMessage="Error updating product. Please try again.";
    });
  }
 
  closeUpdateProductPopup() {
    this.showUpdatePopup = false;
    this.resetUpdateProductForm();
  }
 
  closeUpdatePopup(){
    this.productUpdated=false;
  }
 
  resetUpdateProductForm() {
    this.product = {
      id: '',
      name: '',
      upName: '',
      upDescription: '',
      isActive: false,
      image: null,
      imageUrl: ''
    };
    this.productFound = false;
    this.previewImage = null;
  }
 
  // Methods for Get Users popup
  openGetUsersPopup() {
    this.showGetUsersPopup = true;
  }
 
  closeGetUsersPopup() {
    this.showGetUsersPopup = false;
  }
  openUpdateUsers() {
    this.showGetUsersPopup = true;
  }
 
  openUpdateUserPopup() {
    console.log('openUpdateUserPopup() called');
    console.log('isUpdateUserPopupVisible:', this.isUpdateUserPopupVisible);
    this.isUpdateUserPopupVisible = true;
  }
 
  closeUpdateUserPopup() {
    this.isUpdateUserPopupVisible = false;
  }
 
  handleUserUpdated(userData: IUserDetails) {
    console.log('User data received for update:', userData);
    //const userId = userData.userID;
    if (userData && userData.userID) {
      const userId = userData.userID;
      console.log("Inside if");
      const formData = new FormData();
      for (const key in userData) {
        if (userData.hasOwnProperty(key)) {
          formData.append(key, userData[key]);
        }
      }
 
      this.userService.updateUser(userData.userID, formData).subscribe({
        next: (response) => {
          console.log('User updated successfully:', response);
          this.isUpdateUserPopupVisible = false;
          // alert('User updated successfully!');
        },
        error: (error) => {
          console.error('Error updating user:', error);
          // alert('Error updating user.');
        }
      });
    } else {
      console.error('User ID is missing in the user data.');
      // alert('Error: Could not update user (ID missing).');
    }
  }
 
  //add user
 
  showAddUserPopup:boolean=false;
  userAdded:boolean=false;
  addUser={
  firstName:'',
  lastName:'',
  nickName:'',
  dob:'',
  contactNo:'',
  addressL1:'',
  addressL2:'',
  email:'',
  postalCode:'',
  imageFile: null as File | null,
  isAdmin: false
}
  
  addUserForm: any;
 
  password:string='Abc@123';
registrationSuccess = false;
registrationError = false;
errorMessage = '';
 
 
openAddUserPopup() {
  console.log('openAddUserPopup');
  this.showAddUserPopup = true;
}
 
submitUser(): void {
  console.log("inside submit user");
  const formData = new FormData();
  formData.append('firstName', this.addUser.firstName);
  formData.append('lastName', this.addUser.lastName);
  formData.append('nickName', this.addUser.nickName);
  formData.append('dateOfBirth', this.addUser.dob); // Ensure this format matches "yyyy-MM-dd"
  formData.append('contactNumber', this.addUser.contactNo);
  formData.append('addressLine1', this.addUser.addressL1);
  formData.append('addressLine2', this.addUser.addressL2);
  formData.append('email', this.addUser.email);
  formData.append('postalCode', this.addUser.postalCode);
  formData.append('isAdmin', this.addUser.isAdmin ? '1' : '0');
  this.password=this.addUser.firstName+this.addUser.dob;
  console.log(this.password);
  if (this.addUser.imageFile) {
    formData.append('imageFile', this.addUser.imageFile);
  }

  this.authService.signUp(this.addUser.email, this.password).then(result => {
    console.log('User registered through admin:', result);
    // alert("Registration Successful! Check for email verification");
    this.AlertPopupTitle = 'Registration Successful!';
      this.AlertPopupMessage = 'Check for email verification';
      this.showAlertPopup = true;

    //this.productService.registerUser(formData)
 
  this.productService.registerUser(formData) // Send FormData
    .subscribe(
      response => {
              //alert('User added successfully!');
              this.resetForm();
              this.showAddUserPopup = false;
              this.userAdded=true;
              this.popupTitle="Success"
              this.popupMessage="User added successfully!";
            },
            error => {
              console.error('Error adding user:', error);
              //alert('Error adding user. Please try again.');
              this.userAdded=true;
              this.popupTitle="Error"
              this.popupMessage="Error adding user. Please try again.";
            }
    );

    this.router.navigate(['/verify-email']);

  }).catch(err => {
    console.error("Registration failed:",err);
    // alert('Error:'+ err.message);
    this.popupTitle = 'Error';
      this.popupMessage = 'Registration Failed';
      this.showAlertPopup = true;
  })
  

  // this.productService.registerUser(formData) // Send FormData
  //   .subscribe(
  //     response => {
  //       //alert('User added successfully!');
  //       this.resetForm();
  //       this.showAddUserPopup = false;
  //       this.userAdded=true;
  //       this.popupTitle="Success"
  //       this.popupMessage="User added successfully!";
  //     },
  //     error => {
  //       console.error('Error adding user:', error);
  //       //alert('Error adding user. Please try again.');
  //       this.userAdded=true;
  //       this.popupTitle="Error"
  //       this.popupMessage="Error adding user. Please try again.";
  //     }
  //   );
}
 
removeSelectedFile(): void {
  this.addUser.imageFile = null;
  // Optionally reset the file input to allow selecting the same file again
  if (this.addUser.imageFile) {
    this.addUser.imageFile=null;
  }
}
 
onFileChange(event: any): void {
  // if (event.target.files && event.target.files.length > 0) {
  //   this.addUser.uploadPhoto = event.target.files[0];
  // }
 
  const file = event.target.files[0];
  if (file) {
    this.addUser.imageFile = file;
  }
  else {
    this.addUser.imageFile = null;
  }
}
closeAddUserPopup(): void {
  this.showAddUserPopup = false;
  this.resetForm(); // Reset the form when closing
  // Optionally emit an event to notify the parent component that the popup was closed
}
 
 
closeUserAddedPopup(){
  this.userAdded=false;
}
 
removePhoto() {
 
  const photoInput = document.getElementById('imageFile') as HTMLInputElement;
 
  if (photoInput) {
 
      photoInput.value = '';
 
  }
 
}
 
resetForm(): void {
  this.addUser = {
    firstName: '',
    lastName: '',
    nickName: '',
    dob: '',
    contactNo: '',
    addressL1: '',
    addressL2: '',
    email: '',
    postalCode: '',
    imageFile: null as File | null,
    isAdmin: false
  }
 
}

closeAlertPopup(){
  this.showAlertPopup = false;
}
 
}
 