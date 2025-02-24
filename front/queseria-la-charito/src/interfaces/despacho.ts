import Destino from "./destino"
import DetalleDespacho from "./detalleDespacho"
import Vehiculo from "./vehiculo"

export default interface Despacho {
  id: number
  fecha: string 
  queso: string
  cantidadTotal: number
  destino: Destino
  vehiculo: Vehiculo
  detalles: DetalleDespacho[],
  usuario: {username: string}
  estado: string
}