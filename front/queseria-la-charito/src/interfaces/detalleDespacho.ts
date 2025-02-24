import Lote from "./lote"

export default interface DetalleDespacho {
  id: number
  lote: Lote
  cantidadEnteros: number | null
  cantidadMedios: number | null
  cantidadCuartos: number | null
}