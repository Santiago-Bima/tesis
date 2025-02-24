import { DetalleFormula } from "./detalleFormula"
import { TipoQueso } from "./tipoQueso"

export interface Formula {
  codigo: string
  cantidad_leche: number
  queso: TipoQueso
  detalles: DetalleFormula[]
}