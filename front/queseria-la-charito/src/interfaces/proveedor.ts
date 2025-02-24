import { Item } from "./item"

export default interface Proveedor {
  id: number
  nombre: string
  email: string
  alias: string
  cuit: string
  banco: string
  tipoCuenta: string
  telefono: number
  insumo: Item
  unidadMedida: string
  cantidadMedida: number
  costo: number
}