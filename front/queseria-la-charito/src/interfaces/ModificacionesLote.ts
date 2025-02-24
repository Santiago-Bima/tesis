import Lote from "./lote"

export default interface ModificacionLote {
  id: number
  fecha: string
  motivo: string
  cantidadPrevia: number
  cantidadPosterior: number
  lote: Lote
  usuario: {username: string}
  nuevo:boolean
}