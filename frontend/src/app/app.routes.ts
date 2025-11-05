import type { Routes } from "@angular/router"
import { BeneficioListComponent } from "./components/beneficio-list/beneficio-list.component"

export const routes: Routes = [
  { path: "", redirectTo: "/beneficios", pathMatch: "full" },
  { path: "beneficios", component: BeneficioListComponent },
]
