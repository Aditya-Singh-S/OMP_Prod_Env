import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { UserService } from '../services/user.service';
import { map, take} from 'rxjs/operators';

export const adminGuard: CanActivateFn = (route, state) => {

  const userService = inject(UserService);
  const router = inject(Router);

  return userService.isAdmin$.pipe(
    take(1),
    map(isAdmin => {
      if(!isAdmin){
        router.navigate(['/home']);
        return false;
      }
      return true;
    })
  );
};
