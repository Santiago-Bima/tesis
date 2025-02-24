import { Item } from "./item"

export default interface Lote {
  codigo: string
  item: Item
  estado: string
  unidades: number
}