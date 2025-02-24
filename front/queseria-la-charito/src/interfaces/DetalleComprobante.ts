import Lote from "./lote"
import Proveedor from "./proveedor"

export default interface DetalleComprobante {
  id: number
  lote: Lote
  proveedor: Proveedor
  cantidad: number
  subtotal: number
}