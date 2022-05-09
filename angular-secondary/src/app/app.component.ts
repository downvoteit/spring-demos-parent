import {Component} from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <div class="plain-main">
      <div>
        <h1>{{ message }}</h1>
      </div>
      <div class="plain-nav">
        <button routerLink="/browse">Browse</button>
        <button routerLink="/ledger">Ledger</button>
      </div>
      <div>
        <router-outlet></router-outlet>
      </div>
    </div>
  `,
  styles: []
})
export class AppComponent {
  message = `Welcome to Server ${window.location.port} - Secondary / Analytics (OLAP)`
}
