
import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { saveAs } from 'file-saver';
import * as XLSX from 'xlsx';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Observable, tap } from 'rxjs';

interface UserDetail {
  firstName: string;
  lastName: string;
  email: string;
  dateOfBirth: string;
  contactNumber: string;
  addedOn: string;
  updatedOn: string;
  addressLine1: string;
  addressLine2: string;
  postalCode: number;
  active: boolean;
  userRole: string; 
  emailVerification?: boolean; 

}

@Component({
  selector: 'app-admin-user-list-popup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-user-list-popup.component.html',
  styleUrl: './admin-user-list-popup.component.css'
})
export class AdminUserListPopupComponent implements OnInit {
  allUsers: UserDetail[] = [];
  @Output() close = new EventEmitter<void>();
  selectedStatus: string = '';

   // New properties for the popup
   showPopup: boolean = false;
   popupTitle: string = '';
   popupMessage: string = '';

  constructor(private http: HttpClient,private userService : UserService) { }

  ngOnInit(): void {
    this.fetchAllUsers();
  }

  fetchAllUsers() {
    this.http.get<UserDetail[]>('https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/admin/users',{headers : this.userService.authHeaders}).subscribe(
      (data) => {
        this.allUsers = data; 
      },
      (error: HttpErrorResponse) => {
        console.error('Error fetching all users:', error);
      }
    );
  }

  filterUsersByActiveStatus() {
    if (this.selectedStatus === 'active' || this.selectedStatus === 'inactive') 
      {
      const isActiveValue = this.selectedStatus === 'active';
      const params = new HttpParams().set('isActive', isActiveValue.toString());

      this.http.get<UserDetail[]>('https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/admin/users/filter', { headers : this.userService.authHeaders,params }).subscribe(
        (data) => {
          this.allUsers = data; 
        },
        (error: HttpErrorResponse) => {
          console.error('Error filtering users:', error);
        }
      );
    } else {
      this.fetchAllUsers();
    }
  }

  filterUsers() {
    if (this.selectedStatus === 'active') {
        this.getUsersByActiveStatus(true).subscribe(users => {
            this.allUsers = users;
        });
    } else if (this.selectedStatus === 'inactive') {
        this.getUsersByActiveStatus(false).subscribe(users => {
            this.allUsers = users;
        });
    } else if (this.selectedStatus === 'verified') {
        this.getVerifiedUsers().subscribe(users => {
            this.allUsers = users;
        });
    } else if (this.selectedStatus === 'not_verified') {
        this.getNotVerifiedUsers().subscribe(users => {
            this.allUsers = users;
        });
    } else {
        this.fetchAllUsers();
    }
}

getUsersByActiveStatus(isActive: boolean): Observable<UserDetail[]> {
    const params = new HttpParams().set('isActive', isActive.toString());
    return this.http.get<UserDetail[]>(`https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/admin/users/active`, { headers : this.userService.authHeaders,params });
}

getVerifiedUsers(): Observable<UserDetail[]> {
    const params = new HttpParams().set('emailVerification', 'true');
    return this.http.get<UserDetail[]>(`https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/admin/users/verified`, {headers : this.userService.authHeaders,params }).pipe(
      tap(data => console.log('Verified users data:', data)) 
    );
}

getNotVerifiedUsers(): Observable<UserDetail[]> {
    const params = new HttpParams().set('emailVerification', 'false');
    return this.http.get<UserDetail[]>(`https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/admin/users/verified`, {headers : this.userService.authHeaders,params }).pipe(
      tap(data => console.log('Not verified users data:', data)) 
    );
}


  exportToExcel() {
    if (this.allUsers && this.allUsers.length > 0) {
      const formattedUsers = this.allUsers.map(user => ({
        'First Name': user.firstName,
        'Last Name': user.lastName,
        'Email': user.email,
        'Date of Birth': user.dateOfBirth ? new Date(user.dateOfBirth).toLocaleDateString('en-IN') : '',
        'Contact Number': user.contactNumber,
        'Added On': user.addedOn ? new Date(user.addedOn).toLocaleString('en-IN') : '',
        'Updated On': user.updatedOn ? new Date(user.updatedOn).toLocaleString('en-IN') : '',
        'Address Line 1': user.addressLine1,
        'Address Line 2': user.addressLine2,
        'Postal Code': user.postalCode,
        'Active': user.active ? 'Yes' : 'No',
        'Email Verified': user.emailVerification ? 'Yes' : 'No',
        'Role': user.userRole 
      }));

      const worksheet = XLSX.utils.json_to_sheet(formattedUsers);
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, 'User Details');
      const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
      this.saveAsExcelFile(excelBuffer, 'user_details');
    } else {
      //alert('No user data to export.');
      this.popupTitle = 'Error';
      this.popupMessage = 'No user data to export.';
      this.showPopup = true;
    }
  }

  saveAsExcelFile(buffer: any, fileName: string): void {
    const data: Blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8' });
    saveAs(data, fileName + '.xlsx');
  }

  closePopup() {
    this.close.emit();
  }

  closePop(){
    this.showPopup=false;
  }
}