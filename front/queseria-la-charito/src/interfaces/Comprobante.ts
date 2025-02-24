import DetalleComprobante from "./DetalleComprobante"

export default interface ComprobanteResult {
  id: number
  fecha: string
  total: number,
  detalles: DetalleComprobante[]
}