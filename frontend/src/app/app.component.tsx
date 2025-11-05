import { Component } from "@angular/core"
import { RouterOutlet } from "@angular/router"

@Component({
  selector: "app-root",
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="app-container">
      <header class="app-header">
        <h1>Sistema de Gerenciamento de Benefícios</h1>
      </header>
      <main class="app-main">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [
    `
    .app-container {
      min-height: 100vh;
      background: #f5f5f5;
    }
    .app-header {
      background: #1976d2;
      color: white;
      padding: 1.5rem 2rem;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .app-header h1 {
      margin: 0;
      font-size: 1.5rem;
    }
    .app-main {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }
  `,
  ],
})
export class AppComponent {
  title = "Benefícios"
}
