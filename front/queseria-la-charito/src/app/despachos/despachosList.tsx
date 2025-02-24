'use client'

import { useState, useEffect } from 'react'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Alert, AlertDescription } from "@/components/ui/alert"
import Link from 'next/link'
import { AlertCircle } from 'lucide-react'
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import Destino from '@/interfaces/destino'
import Vehiculo from '@/interfaces/vehiculo'
import Despacho from '@/interfaces/despacho'
import { useRouter } from 'next/navigation'
import { NumberFormatter } from '@/utils/numberFormatter'
import formatArgDate from '@/utils/formatArgDate'

export default function Component() {
  const [destinos, setDestinos] = useState<Destino[]>([])
  const [vehiculos, setVehiculos] = useState<Vehiculo[]>([])
  const [selectedDestino, setSelectedDestino] = useState<number | null>(null)
  const [despachos, setDespachos] = useState<Despacho[]>([])
  const [fecha, setFecha] = useState<string>('')
  const [error, setError] = useState<string | null>(null)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [despachoToDelete, setDespachoToDelete] = useState<number | null>(null)
  const [hasSearched, setHasSearched] = useState(false)
  const [despachoToEdit, setDespachoToEdit] = useState<Despacho | null>(null)
  const [editedVehiculo, setEditedVehiculo] = useState<number | null>(null)
  const [editedDestino, setEditedDestino] = useState<number | null>(null)
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)

  const formatter = new NumberFormatter('es-ES')
  const router = useRouter()

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado")
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
        router.push('/')
      } else {
        fetchDestinos()
        fetchVehiculos()
      }
    }
  }, [router])

  useEffect(() => {
    fetchDespachos()
  }, [selectedDestino, fecha])

  const handleDestinoChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const itemId = parseInt(event.target.value)
    setSelectedDestino(itemId)
  }

  const fetchVehiculos = async () => {
    try {
      const response = await fetch('http://localhost:8082/vehiculos')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los vehículos')
      }
      const data = await response.json()
      setVehiculos(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los vehículos. Por favor, intente de nuevo más tarde.')
    }
  }

  const fetchDestinos = async () => {
    try {
      const response = await fetch('http://localhost:8082/destinos')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los destinos')
      }
      const data = await response.json()
      setDestinos(data)
    } catch (error) {
      setError(error instanceof Error ? error.message :  'Error al cargar los destinos. Por favor, intente de nuevo más tarde.')
    }
  }

  const fetchDespachos = async () => {
    if (!selectedDestino) {
      return
    }

    try {
      let url = `http://localhost:8082/despachos?destinoId=${selectedDestino}`
      if (fecha) {
        url += `&fecha=${fecha}`
      }

      const response = await fetch(url)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los despachos')
      }
      const data = await response.json()
      setDespachos(data)
      setError(null)
      setHasSearched(true)
    } catch (error) {
      setError(error instanceof Error ? error.message :  'Error al cargar los despachos. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleEditDestinoChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const itemId = parseInt(event.target.value)
    setEditedDestino(itemId)
  }

  const handleEditVehiculoChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const itemId = parseInt(event.target.value)
    setEditedVehiculo(itemId)
  }

  const handleEditDespacho = async () => {
    if (despachoToEdit === null || editedVehiculo === null || editedDestino === null) return

    try {
      const response = await fetch(`http://localhost:8082/despachos/${despachoToEdit.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          idVehiculo: editedVehiculo,
          idDestino: editedDestino,
        }),
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al editar el despacho')
      }
      
      fetchDespachos()
      setIsEditDialogOpen(false)
      setDespachoToEdit(null)
      setEditedVehiculo(null)
      setEditedDestino(null)
    } catch (error) {
      setError(error instanceof Error ? error.message :  'Error al editar el despacho. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleDeleteDespacho = async () => {
    if (despachoToDelete === null) return

    try {
      const response = await fetch(`http://localhost:8082/despachos/${despachoToDelete}`, {
        method: 'DELETE',
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al eliminar el despacho')
      }
      setDespachos(despachos.filter(despacho => despacho.id !== despachoToDelete))
      setIsDeleteDialogOpen(false)
      setDespachoToDelete(null)
    } catch (error) {
      setError(error instanceof Error ? error.message :  'Error al eliminar el despacho. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Lista de Despachos</h1>
      
      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <div className="flex flex-col md:flex-row w-full gap-4 justify-between mb-6">
        <div className="flex flex-col md:flex-row w-full md:w-3/4 gap-4">
          <select
            value={selectedDestino?.toString() || ''}
            onChange={handleDestinoChange}
            className="w-full md:w-1/2 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="" disabled>Seleccione un destino</option>
            {destinos.map((destino) => (
              <option key={destino.id} value={destino.id.toString()}>
                {destino.calle}, {destino.numero} - {destino.barrio}
              </option>
            ))}
          </select>
          <Input
            type="date"
            value={fecha}
            onChange={(e) => setFecha(e.target.value)}
            placeholder="Fecha (opcional)"
            className="w-full md:w-1/2 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        <div className="flex items-center justify-center md:justify-end mt-4 md:mt-0">
          <Link href="/despachos/crear">
            <Button className="bg-green-500 hover:bg-green-600 text-white px-6 py-3 rounded-md transition-colors font-semibold w-full md:w-auto">
              Agregar Nuevo Despacho
            </Button>
          </Link>
        </div>
      </div>

      {hasSearched && (
        despachos.length > 0 ? (
          <Accordion type="single" collapsible className="mt-6">
            {despachos.map((despacho) => (
              <AccordionItem key={despacho.id} value={despacho.id.toString()}>
                <AccordionTrigger>
                  <div className="flex flex-col md:flex-row justify-between w-full text-sm md:text-base">
                    <span className="mb-1 md:mb-0">Fecha: {formatArgDate(despacho.fecha)}</span>
                    <span className="mb-1 md:mb-0">Responsable: {despacho.usuario.username}</span>
                    <span className="mb-1 md:mb-0">Estado: {despacho.estado.replace(/([A-Z])/g, ' $1').trim()}</span>
                    <span className="mb-1 md:mb-0">Queso: {despacho.queso}</span>
                    <span className="mb-1 md:mb-0">Cantidad Total: {formatter.formatNumber(despacho.cantidadTotal)}</span>
                    <span className="mb-1 md:mb-0">Destino: {despacho.destino.calle}, {despacho.destino.numero} - {despacho.destino.barrio}</span>
                    <span>Vehículo: {despacho.vehiculo.matricula}</span>
                  </div> 
                </AccordionTrigger>
                <AccordionContent>
                  <div className="space-y-2">
                    <h3 className="font-semibold">Detalles del despacho:</h3>
                    <ul className="space-y-1">
                      {despacho.detalles.map((detalle) => (
                        <li key={detalle.id} className="text-sm">
                          Lote: {detalle.lote.codigo}
                          {detalle.cantidadEnteros !== null && `, Enteros: ${formatter.formatNumber(detalle.cantidadEnteros)}`}
                          {detalle.cantidadMedios !== null && `, Medios: ${formatter.formatNumber(detalle.cantidadMedios)}`}
                          {detalle.cantidadCuartos !== null && `, Cuartos: ${formatter.formatNumber(detalle.cantidadCuartos)}`}
                        </li>
                      ))}
                    </ul>
                    <DistanceInfo destino={despacho.destino} />
                    <div className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2 mt-4">
                      {despacho.estado === 'PorEntregar' && (
                        <Button 
                          variant="outline" 
                          onClick={() => {
                            setDespachoToEdit(despacho)
                            setEditedVehiculo(despacho.vehiculo.id)
                            setEditedDestino(despacho.destino.id)
                            setIsEditDialogOpen(true)
                          }}
                          className="bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors font-semibold w-full sm:w-auto"
                        >
                          Editar Despacho
                        </Button>
                      )}
                      {despacho.estado !== 'Entregando' && (
                        <Button 
                          variant="destructive" 
                          onClick={() => {
                            setDespachoToDelete(despacho.id)
                            setIsDeleteDialogOpen(true)
                          }}
                          className="bg-red-500 hover:bg-red-600 text-white rounded-md transition-colors font-semibold w-full sm:w-auto"
                        >
                          Eliminar Despacho
                        </Button>
                      )}
                    </div>
                  </div>
                </AccordionContent>
              </AccordionItem>
            ))}
          </Accordion>
        ) : (
          <div className="text-center py-4 text-gray-500">No hay despachos disponibles para el destino y fecha seleccionados.</div>
        )
      )}

      <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirmar eliminación</DialogTitle>
            <DialogDescription>
              ¿Está seguro de que desea eliminar este despacho? Esta acción no se puede deshacer.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2">
            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)} className="bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors font-semibold w-full sm:w-auto">Cancelar</Button>
            <Button variant="destructive" onClick={handleDeleteDespacho} className="bg-red-500 hover:bg-red-600 text-white rounded-md transition-colors font-semibold w-full sm:w-auto">Eliminar</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Editar Despacho</DialogTitle>
            <DialogDescription>
              Modifique el vehículo o el destino del despacho.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="flex flex-col items-center mx-4 sm:mx-10">
              <label htmlFor="vehiculo" className="text-right mb-2">
                Vehículo
              </label>
              <select
                id="vehiculo"
                value={editedVehiculo?.toString() || ''}
                onChange={handleEditVehiculoChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                {vehiculos.map((vehiculo) => (
                  <option key={vehiculo.id} value={vehiculo.id.toString()}>
                    {vehiculo.matricula}
                  </option>
                ))}
              </select>
            </div>
            <div className="flex flex-col items-center mx-4 sm:mx-10">
              <label htmlFor="destino" className="text-right mb-2">
                Destino
              </label>
              <select
                id="destino"
                value={editedDestino?.toString() || ''}
                onChange={handleEditDestinoChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                {destinos.map((destino) => (
                  <option key={destino.id} value={destino.id.toString()}>
                    {destino.calle}, {destino.numero} - {destino.barrio}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <DialogFooter className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2">
            <Button variant="outline" onClick={() => setIsEditDialogOpen(false)} className="bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors font-semibold w-full sm:w-auto">Cancelar</Button>
            <Button onClick={handleEditDespacho} className="bg-green-500 hover:bg-green-600 text-white rounded-md transition-colors font-semibold w-full sm:w-auto">Guardar Cambios</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

function DistanceInfo({ destino }: { destino: Destino }) {
  const [distanceInfo, setDistanceInfo] = useState<{ distance: string, duration: string } | null>(null)

  useEffect(() => {
    const fetchDistanceInfo = async () => {
      const info = await calculateDistance(destino)
      setDistanceInfo(info)
    }
    fetchDistanceInfo()
  }, [destino])

  return (
    <div className="text-sm">
      <h3 className="font-semibold">Información de viaje:</h3>
      {distanceInfo ? (
        <p>Distancia: {distanceInfo.distance}, Tiempo estimado: {distanceInfo.duration}</p>
      ) : (
        <p>Calculando información de viaje...</p>
      )}
    </div>
  )
}

async function calculateDistance(destino: Destino) {
  try {
    const response = await fetch('/api/calculateDistance', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ destino }),
    })

    if (!response.ok) {
      throw new Error('Error en la solicitud al servidor')
    }

    const data = await response.json()
    return data
  } catch (error) {
    console.error('Error al calcular la distancia:', error)
    return { distance: 'N/A', duration: 'N/A' }
  }
}