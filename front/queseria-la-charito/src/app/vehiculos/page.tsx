'use client'

import { useState, useEffect } from 'react'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from 'lucide-react'
import Vehiculo from '@/interfaces/vehiculo'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogDescription,
  DialogFooter
} from "@/components/ui/dialog"
import { useRouter } from 'next/navigation'

export default function VehiculosPage() {
  const [vehiculos, setVehiculos] = useState<Vehiculo[]>([])
  const [selectedVehiculo, setSelectedVehiculo] = useState<Vehiculo | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [newMatricula, setNewMatricula] = useState('')
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [vehiculoToDelete, setVehiculoToDelete] = useState<number | null>(null)
  
  const router = useRouter();

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
        router.push('/')
      } else {
        fetchVehiculos()
      }
    }
  }, [router])

  const fetchVehiculos = async () => {
    setIsLoading(true)
    setError(null)
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
    } finally {
      setIsLoading(false)
    }
  }

  const eliminarVehiculo = async () => {
    if(vehiculoToDelete == null) return;
    try {
      const response = await fetch(`http://localhost:8082/vehiculos/${vehiculoToDelete}`, {
        method: 'DELETE',
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al eliminar el vehículos')
      }
      fetchVehiculos()
      setIsDeleteDialogOpen(false)
      setVehiculoToDelete(null)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al eliminar el vehículo. Por favor, intente de nuevo más tarde.')
    }
  }

  const toggleDisponibilidad = async (vehiculo: Vehiculo) => {
    try {
      const response = await fetch(`http://localhost:8082/vehiculos/${vehiculo.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ id: vehiculo.id }),
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al actualizar la disponibilidad del vehículos')
      }
      fetchVehiculos()
      setSelectedVehiculo(null)
      setIsModalOpen(false)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al actualizar la disponibilidad del vehículo. Por favor, intente de nuevo más tarde.')
    }
  }

  const crearVehiculo = async () => {
    if (!newMatricula.trim()) {
      setError('La matrícula no puede estar vacía')
      return
    }
    try {
      const response = await fetch(`http://localhost:8082/vehiculos?matricula=${newMatricula}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        }
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al crear el vehículos')
      }
      setNewMatricula('')
      fetchVehiculos()
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al crear el vehículo. Por favor, intente de nuevo más tarde.')
    }
  }

  const openModal = async (id: number) => {
    try {
      const response = await fetch(`http://localhost:8082/vehiculos/${id}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar la información el vehículos')
      }
      const data = await response.json()
      setSelectedVehiculo(data)
      setIsModalOpen(true)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar la información del vehículo. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Lista de Vehículos</h1>
      
      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <div className="mb-6 flex flex-col sm:flex-row items-center space-y-2 sm:space-y-0 sm:space-x-2">
        <Input
          type="text"
          placeholder="Nueva matrícula"
          value={newMatricula}
          onChange={(e) => setNewMatricula(e.target.value)}
          className="w-full sm:w-auto"
        />
        <Button onClick={crearVehiculo} className='bg-green-500 hover:bg-green-600 text-white transition-colors font-semibold w-full sm:w-auto'>Crear Vehículo</Button>
      </div>

      {isLoading ? (
        <div className="text-center py-4">Cargando...</div>
      ) : vehiculos.length > 0 ? (
        <ul className="space-y-4">
          {vehiculos.map((vehiculo) => (
            <li key={vehiculo.id} className="bg-gray-50 rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow flex flex-col sm:flex-row justify-between items-start sm:items-center space-y-2 sm:space-y-0">
              <div className="flex flex-col sm:flex-row items-start sm:items-center space-y-2 sm:space-y-0 sm:space-x-2">
                <span className="font-semibold text-lg text-gray-800">{vehiculo.matricula}</span>
                <span className={`px-2 py-1 rounded-full text-sm ${vehiculo.disponible ? 'bg-green-200 text-green-800' : 'bg-red-200 text-red-800'}`}>
                  {vehiculo.disponible ? 'Disponible' : 'No disponible'}
                </span>
              </div>
              <div className="flex flex-col sm:flex-row items-start sm:items-center space-y-2 sm:space-y-0 sm:space-x-2 w-full sm:w-auto">
                <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
                  <DialogTrigger asChild>
                    <Button variant="outline" onClick={() => openModal(vehiculo.id)} className='bg-blue-500 hover:bg-blue-600 text-white w-full sm:w-auto'>Ver Detalles</Button>
                  </DialogTrigger>
                  <DialogContent>
                    <DialogHeader>
                      <DialogTitle>Detalles del Vehículo</DialogTitle>
                    </DialogHeader>
                    {selectedVehiculo && (
                      <div className="mt-4">
                        <p><strong>ID:</strong> {selectedVehiculo.id}</p>
                        <p><strong>Matrícula:</strong> {selectedVehiculo.matricula}</p>
                        <p><strong>Estado:</strong> {selectedVehiculo.disponible ? 'Disponible' : 'No disponible'}</p>
                        <Button 
                          onClick={() => toggleDisponibilidad(selectedVehiculo)}
                          className="mt-4 bg-yellow-500 hover:bg-yellow-600 text-black w-full sm:w-auto"
                        >
                          Cambiar Disponibilidad
                        </Button>
                      </div>
                    )}
                  </DialogContent>
                </Dialog>
                <Button variant="destructive" onClick={() => {
                      setVehiculoToDelete(vehiculo.id)
                      setIsDeleteDialogOpen(true)
                  }} className='bg-red-500 hover:bg-red-600 text-white transition-colors font-semibold w-full sm:w-auto'>Eliminar</Button>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <div className="text-center py-4 text-gray-500">No hay vehículos disponibles.</div>
      )}

      <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirmar eliminación</DialogTitle>
            <DialogDescription>
              ¿Está seguro de que desea eliminar este vehículo? Esta acción no se puede deshacer.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2">
            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)} className="bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors font-semibold w-full sm:w-auto">Cancelar</Button>
            <Button variant="destructive" onClick={eliminarVehiculo} className="bg-red-500 hover:bg-red-600 text-white rounded-md transition-colors font-semibold w-full sm:w-auto">Eliminar</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}