import { Formula } from "./formula"
import DetalleCorte from "./detallecorte"
import Control from "./control"
import Lote from "./lote"

export default interface Elaboracion {
  id: string
  fecha: string
  cantidadLeche: number
  lote: Lote
  formula: Formula
  tiempoSalado: number | null
  fechaEntradaMaduracion: string | null
  fechaSalidaMaduracion: string | null
  fechaEmbolsado: string | null
  fechaPintado: string | null
  detalleCorte: DetalleCorte
  control: Control | null
}