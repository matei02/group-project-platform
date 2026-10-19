// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable } from 'rxjs';
// import { User } from './user.model';
// import { environment }  from '../../../environments/environment'

// @Injectable({
//   providedIn: 'root'
// })
// export class ApiService {

//   constructor(private http: HttpClient) { }

//   fetchData(): Observable<User> {
//     return this.http.get<User>(`${environment.apiUrl}/api/users/1`);
//   }

//   increaseVirtualMoney(userId: number): Observable<any> {
//     return this.http.post(`${environment.apiUrl}/api/users/${userId}/increase/virtual-money`, {});
//   }
// }
