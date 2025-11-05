import { Component, type OnInit } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import type { BeneficioService } from "../../services/beneficio.service"
import type { Beneficio, TransferRequest } from "../../models/beneficio.model"

@Component({
  selector: "app-beneficio-list",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./beneficio-list.component.html",
  styleUrls: ["./beneficio-list.component.css"],
})
export class BeneficioListComponent implements OnInit {
  beneficios: Beneficio[] = []
  selectedBeneficio: Beneficio | null = null
  isEditing = false
  showTransferModal = false
  errorMessage = ""
  successMessage = ""

  // Form models
  formBeneficio: Beneficio = {
    nome: "",
    descricao: "",
    valor: 0,
    ativo: true,
  }

  transferRequest: TransferRequest = {
    fromId: 0,
    toId: 0,
    amount: 0,
  }

  constructor(private beneficioService: BeneficioService) {}

  ngOnInit(): void {
    this.loadBeneficios()
  }

  loadBeneficios(): void {
    this.beneficioService.getAll().subscribe({
      next: (data) => {
        this.beneficios = data
        this.clearMessages()
      },
      error: (error) => {
        this.errorMessage = "Erro ao carregar benefícios"
        console.error(error)
      },
    })
  }

  onSubmit(): void {
    if (this.isEditing && this.selectedBeneficio?.id) {
      this.beneficioService.update(this.selectedBeneficio.id, this.formBeneficio).subscribe({
        next: () => {
          this.successMessage = "Benefício atualizado com sucesso!"
          this.loadBeneficios()
          this.resetForm()
        },
        error: (error) => {
          this.errorMessage = error.error?.message || "Erro ao atualizar benefício"
        },
      })
    } else {
      this.beneficioService.create(this.formBeneficio).subscribe({
        next: () => {
          this.successMessage = "Benefício criado com sucesso!"
          this.loadBeneficios()
          this.resetForm()
        },
        error: (error) => {
          this.errorMessage = error.error?.message || "Erro ao criar benefício"
        },
      })
    }
  }

  editBeneficio(beneficio: Beneficio): void {
    this.selectedBeneficio = beneficio
    this.formBeneficio = { ...beneficio }
    this.isEditing = true
    this.clearMessages()
  }

  deleteBeneficio(id: number): void {
    if (confirm("Tem certeza que deseja deletar este benefício?")) {
      this.beneficioService.delete(id).subscribe({
        next: () => {
          this.successMessage = "Benefício deletado com sucesso!"
          this.loadBeneficios()
        },
        error: (error) => {
          this.errorMessage = "Erro ao deletar benefício"
          console.error(error)
        },
      })
    }
  }

  openTransferModal(): void {
    this.showTransferModal = true
    this.transferRequest = { fromId: 0, toId: 0, amount: 0 }
    this.clearMessages()
  }

  closeTransferModal(): void {
    this.showTransferModal = false
    this.transferRequest = { fromId: 0, toId: 0, amount: 0 }
  }

  executeTransfer(): void {
    this.beneficioService.transfer(this.transferRequest).subscribe({
      next: () => {
        this.successMessage = "Transferência realizada com sucesso!"
        this.loadBeneficios()
        this.closeTransferModal()
      },
      error: (error) => {
        this.errorMessage = error.error?.message || "Erro ao realizar transferência"
      },
    })
  }

  resetForm(): void {
    this.formBeneficio = {
      nome: "",
      descricao: "",
      valor: 0,
      ativo: true,
    }
    this.selectedBeneficio = null
    this.isEditing = false
  }

  clearMessages(): void {
    this.errorMessage = ""
    this.successMessage = ""
  }
}
