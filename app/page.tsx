"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Plus, Pencil, Trash2, ArrowLeftRight, TrendingUp, DollarSign, Package, Activity } from "lucide-react"
import { useToast } from "@/hooks/use-toast"

interface Beneficio {
  id: number
  nome: string
  descricao: string
  valor: number
  ativo: boolean
  version: number
}

export default function BeneficiosPage() {
  const [beneficios, setBeneficios] = useState<Beneficio[]>([
    {
      id: 1,
      nome: "Vale Alimentação",
      descricao: "Benefício mensal para alimentação",
      valor: 500.0,
      ativo: true,
      version: 1,
    },
    {
      id: 2,
      nome: "Vale Transporte",
      descricao: "Auxílio para transporte diário",
      valor: 200.0,
      ativo: true,
      version: 1,
    },
    { id: 3, nome: "Plano de Saúde", descricao: "Cobertura médica completa", valor: 350.0, ativo: true, version: 1 },
    { id: 4, nome: "Vale Refeição", descricao: "Benefício para refeições", valor: 400.0, ativo: false, version: 1 },
  ])

  const [isCreateOpen, setIsCreateOpen] = useState(false)
  const [isEditOpen, setIsEditOpen] = useState(false)
  const [isTransferOpen, setIsTransferOpen] = useState(false)
  const [isDeleteOpen, setIsDeleteOpen] = useState(false)
  const [selectedBeneficio, setSelectedBeneficio] = useState<Beneficio | null>(null)
  const [formData, setFormData] = useState({ nome: "", descricao: "", valor: "", ativo: true })
  const [transferData, setTransferData] = useState({ origemId: "", destinoId: "", valor: "" })
  const { toast } = useToast()

  const stats = [
    {
      title: "Total de Benefícios",
      value: beneficios.length.toString(),
      icon: Package,
      trend: "+2 este mês",
      color: "text-blue-500",
    },
    {
      title: "Benefícios Ativos",
      value: beneficios.filter((b) => b.ativo).length.toString(),
      icon: Activity,
      trend: "75% do total",
      color: "text-green-500",
    },
    {
      title: "Valor Total",
      value: `R$ ${beneficios.reduce((sum, b) => sum + b.valor, 0).toFixed(2)}`,
      icon: DollarSign,
      trend: "+12% vs mês anterior",
      color: "text-purple-500",
    },
    {
      title: "Média por Benefício",
      value: `R$ ${(beneficios.reduce((sum, b) => sum + b.valor, 0) / beneficios.length).toFixed(2)}`,
      icon: TrendingUp,
      trend: "Estável",
      color: "text-orange-500",
    },
  ]

  const handleCreate = () => {
    const newBeneficio: Beneficio = {
      id: Math.max(...beneficios.map((b) => b.id), 0) + 1,
      nome: formData.nome,
      descricao: formData.descricao,
      valor: Number.parseFloat(formData.valor),
      ativo: formData.ativo,
      version: 1,
    }
    setBeneficios([...beneficios, newBeneficio])
    setIsCreateOpen(false)
    setFormData({ nome: "", descricao: "", valor: "", ativo: true })
    toast({ title: "Sucesso", description: "Benefício criado com sucesso!" })
  }

  const handleEdit = () => {
    if (!selectedBeneficio) return
    setBeneficios(
      beneficios.map((b) =>
        b.id === selectedBeneficio.id
          ? {
              ...b,
              nome: formData.nome,
              descricao: formData.descricao,
              valor: Number.parseFloat(formData.valor),
              ativo: formData.ativo,
              version: b.version + 1,
            }
          : b,
      ),
    )
    setIsEditOpen(false)
    setSelectedBeneficio(null)
    setFormData({ nome: "", descricao: "", valor: "", ativo: true })
    toast({ title: "Sucesso", description: "Benefício atualizado com sucesso!" })
  }

  const handleDelete = () => {
    if (!selectedBeneficio) return
    setBeneficios(beneficios.filter((b) => b.id !== selectedBeneficio.id))
    setIsDeleteOpen(false)
    setSelectedBeneficio(null)
    toast({ title: "Sucesso", description: "Benefício excluído com sucesso!" })
  }

  const handleTransfer = () => {
    const origem = beneficios.find((b) => b.id === Number.parseInt(transferData.origemId))
    const destino = beneficios.find((b) => b.id === Number.parseInt(transferData.destinoId))
    const valor = Number.parseFloat(transferData.valor)

    if (!origem || !destino) {
      toast({ title: "Erro", description: "Benefícios de origem ou destino inválidos", variant: "destructive" })
      return
    }

    if (origem.valor < valor) {
      toast({ title: "Erro", description: "Saldo insuficiente no benefício de origem", variant: "destructive" })
      return
    }

    setBeneficios(
      beneficios.map((b) => {
        if (b.id === origem.id) return { ...b, valor: b.valor - valor, version: b.version + 1 }
        if (b.id === destino.id) return { ...b, valor: b.valor + valor, version: b.version + 1 }
        return b
      }),
    )

    setIsTransferOpen(false)
    setTransferData({ origemId: "", destinoId: "", valor: "" })
    toast({ title: "Sucesso", description: `R$ ${valor.toFixed(2)} transferido com sucesso!` })
  }

  const openEdit = (beneficio: Beneficio) => {
    setSelectedBeneficio(beneficio)
    setFormData({
      nome: beneficio.nome,
      descricao: beneficio.descricao,
      valor: beneficio.valor.toString(),
      ativo: beneficio.ativo,
    })
    setIsEditOpen(true)
  }

  const openDelete = (beneficio: Beneficio) => {
    setSelectedBeneficio(beneficio)
    setIsDeleteOpen(true)
  }

  return (
    <div className="min-h-screen bg-background dark">
      {/* Header */}
      <header className="border-b border-border bg-card/50 backdrop-blur-sm sticky top-0 z-50">
        <div className="container mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-semibold text-foreground">Sistema de Benefícios</h1>
              <p className="text-sm text-muted-foreground mt-1">Gerencie benefícios corporativos</p>
            </div>
            <Button onClick={() => setIsCreateOpen(true)} className="gap-2">
              <Plus className="h-4 w-4" />
              Novo Benefício
            </Button>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-6 py-8">
        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          {stats.map((stat, index) => (
            <Card key={index} className="p-6 bg-card border-border hover:border-primary/50 transition-colors">
              <div className="flex items-start justify-between">
                <div className="space-y-2">
                  <p className="text-sm text-muted-foreground">{stat.title}</p>
                  <p className="text-2xl font-semibold text-foreground">{stat.value}</p>
                  <p className="text-xs text-muted-foreground">{stat.trend}</p>
                </div>
                <div className={`p-3 rounded-lg bg-secondary/50 ${stat.color}`}>
                  <stat.icon className="h-5 w-5" />
                </div>
              </div>
            </Card>
          ))}
        </div>

        {/* Actions Bar */}
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-lg font-semibold text-foreground">Todos os Benefícios</h2>
            <p className="text-sm text-muted-foreground">Gerencie e transfira valores entre benefícios</p>
          </div>
          <Button onClick={() => setIsTransferOpen(true)} variant="outline" className="gap-2">
            <ArrowLeftRight className="h-4 w-4" />
            Transferir Valores
          </Button>
        </div>

        {/* Table */}
        <Card className="border-border bg-card">
          <Table>
            <TableHeader>
              <TableRow className="border-border hover:bg-transparent">
                <TableHead className="text-muted-foreground">ID</TableHead>
                <TableHead className="text-muted-foreground">Nome</TableHead>
                <TableHead className="text-muted-foreground">Descrição</TableHead>
                <TableHead className="text-muted-foreground text-right">Valor</TableHead>
                <TableHead className="text-muted-foreground">Status</TableHead>
                <TableHead className="text-muted-foreground text-right">Ações</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {beneficios.map((beneficio) => (
                <TableRow key={beneficio.id} className="border-border hover:bg-secondary/50">
                  <TableCell className="font-mono text-sm text-muted-foreground">{beneficio.id}</TableCell>
                  <TableCell className="font-medium text-foreground">{beneficio.nome}</TableCell>
                  <TableCell className="text-muted-foreground max-w-xs truncate">{beneficio.descricao}</TableCell>
                  <TableCell className="text-right font-mono text-foreground">
                    R$ {beneficio.valor.toFixed(2)}
                  </TableCell>
                  <TableCell>
                    <Badge
                      variant={beneficio.ativo ? "default" : "secondary"}
                      className={beneficio.ativo ? "bg-green-500/10 text-green-500 hover:bg-green-500/20" : ""}
                    >
                      {beneficio.ativo ? "Ativo" : "Inativo"}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-right">
                    <div className="flex items-center justify-end gap-2">
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => openEdit(beneficio)}
                        className="h-8 w-8 text-muted-foreground hover:text-foreground"
                      >
                        <Pencil className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => openDelete(beneficio)}
                        className="h-8 w-8 text-muted-foreground hover:text-destructive"
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Card>
      </main>

      {/* Create Dialog */}
      <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <DialogContent className="bg-card border-border">
          <DialogHeader>
            <DialogTitle className="text-foreground">Criar Novo Benefício</DialogTitle>
            <DialogDescription className="text-muted-foreground">Preencha os dados do novo benefício</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="nome" className="text-foreground">
                Nome
              </Label>
              <Input
                id="nome"
                value={formData.nome}
                onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
                placeholder="Ex: Vale Alimentação"
                className="bg-background border-border text-foreground"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="descricao" className="text-foreground">
                Descrição
              </Label>
              <Textarea
                id="descricao"
                value={formData.descricao}
                onChange={(e) => setFormData({ ...formData, descricao: e.target.value })}
                placeholder="Descreva o benefício..."
                className="bg-background border-border text-foreground"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="valor" className="text-foreground">
                Valor (R$)
              </Label>
              <Input
                id="valor"
                type="number"
                step="0.01"
                value={formData.valor}
                onChange={(e) => setFormData({ ...formData, valor: e.target.value })}
                placeholder="0.00"
                className="bg-background border-border text-foreground"
              />
            </div>
            <div className="flex items-center space-x-2">
              <input
                type="checkbox"
                id="ativo"
                checked={formData.ativo}
                onChange={(e) => setFormData({ ...formData, ativo: e.target.checked })}
                className="rounded border-border"
              />
              <Label htmlFor="ativo" className="text-foreground cursor-pointer">
                Benefício ativo
              </Label>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsCreateOpen(false)}>
              Cancelar
            </Button>
            <Button onClick={handleCreate}>Criar Benefício</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Edit Dialog */}
      <Dialog open={isEditOpen} onOpenChange={setIsEditOpen}>
        <DialogContent className="bg-card border-border">
          <DialogHeader>
            <DialogTitle className="text-foreground">Editar Benefício</DialogTitle>
            <DialogDescription className="text-muted-foreground">Atualize os dados do benefício</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="edit-nome" className="text-foreground">
                Nome
              </Label>
              <Input
                id="edit-nome"
                value={formData.nome}
                onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
                className="bg-background border-border text-foreground"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="edit-descricao" className="text-foreground">
                Descrição
              </Label>
              <Textarea
                id="edit-descricao"
                value={formData.descricao}
                onChange={(e) => setFormData({ ...formData, descricao: e.target.value })}
                className="bg-background border-border text-foreground"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="edit-valor" className="text-foreground">
                Valor (R$)
              </Label>
              <Input
                id="edit-valor"
                type="number"
                step="0.01"
                value={formData.valor}
                onChange={(e) => setFormData({ ...formData, valor: e.target.value })}
                className="bg-background border-border text-foreground"
              />
            </div>
            <div className="flex items-center space-x-2">
              <input
                type="checkbox"
                id="edit-ativo"
                checked={formData.ativo}
                onChange={(e) => setFormData({ ...formData, ativo: e.target.checked })}
                className="rounded border-border"
              />
              <Label htmlFor="edit-ativo" className="text-foreground cursor-pointer">
                Benefício ativo
              </Label>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsEditOpen(false)}>
              Cancelar
            </Button>
            <Button onClick={handleEdit}>Salvar Alterações</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Transfer Dialog */}
      <Dialog open={isTransferOpen} onOpenChange={setIsTransferOpen}>
        <DialogContent className="bg-card border-border">
          <DialogHeader>
            <DialogTitle className="text-foreground">Transferir Valores</DialogTitle>
            <DialogDescription className="text-muted-foreground">Transfira valores entre benefícios</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="origem" className="text-foreground">
                Benefício de Origem
              </Label>
              <select
                id="origem"
                value={transferData.origemId}
                onChange={(e) => setTransferData({ ...transferData, origemId: e.target.value })}
                className="w-full rounded-md border border-border bg-background px-3 py-2 text-foreground"
              >
                <option value="">Selecione...</option>
                {beneficios.map((b) => (
                  <option key={b.id} value={b.id}>
                    {b.nome} (R$ {b.valor.toFixed(2)})
                  </option>
                ))}
              </select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="destino" className="text-foreground">
                Benefício de Destino
              </Label>
              <select
                id="destino"
                value={transferData.destinoId}
                onChange={(e) => setTransferData({ ...transferData, destinoId: e.target.value })}
                className="w-full rounded-md border border-border bg-background px-3 py-2 text-foreground"
              >
                <option value="">Selecione...</option>
                {beneficios.map((b) => (
                  <option key={b.id} value={b.id}>
                    {b.nome} (R$ {b.valor.toFixed(2)})
                  </option>
                ))}
              </select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="transfer-valor" className="text-foreground">
                Valor a Transferir (R$)
              </Label>
              <Input
                id="transfer-valor"
                type="number"
                step="0.01"
                value={transferData.valor}
                onChange={(e) => setTransferData({ ...transferData, valor: e.target.value })}
                placeholder="0.00"
                className="bg-background border-border text-foreground"
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsTransferOpen(false)}>
              Cancelar
            </Button>
            <Button onClick={handleTransfer}>Confirmar Transferência</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Dialog */}
      <Dialog open={isDeleteOpen} onOpenChange={setIsDeleteOpen}>
        <DialogContent className="bg-card border-border">
          <DialogHeader>
            <DialogTitle className="text-foreground">Confirmar Exclusão</DialogTitle>
            <DialogDescription className="text-muted-foreground">
              Tem certeza que deseja excluir o benefício "{selectedBeneficio?.nome}"? Esta ação não pode ser desfeita.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDeleteOpen(false)}>
              Cancelar
            </Button>
            <Button variant="destructive" onClick={handleDelete}>
              Excluir
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}
