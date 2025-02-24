'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import { TipoQueso } from '@/interfaces/tipoQueso'
import Destino from '@/interfaces/destino'
import Vehiculo from '@/interfaces/vehiculo'


interface User {
  username: string
  rol: string
  isDispatching: boolean
}

export default function CrearDespachoPage() {
  const router = useRouter()
  const [destinos, setDestinos] = useState<Destino[]>([])
  const [vehiculos, setVehiculos] = useState<Vehiculo[]>([])
  const [productos, setProductos] = useState<TipoQueso[]>([])
  const [error, setError] = useState<string | null>(null)

  const [fecha, setFecha] = useState<string>('')
  const [idDestino, setIdDestino] = useState<number | null>(null)
  const [idVehiculo, setIdVehiculo] = useState<number | null>(null)
  const [queso, setQueso] = useState<string>('')
  const [usuario, setUsuario] = useState<string>('')
  const [totalEnteros, setTotalEnteros] = useState<number>(0)
  const [totalMedios, setTotalMedios] = useState<number>(0)
  const [totalCuartos, setTotalCuartos] = useState<number>(0)
  const [usuarios, setUsuarios] = useState<User[]>([])



  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
        router.push('/')
      } else {
        fetchDestinos()
        fetchVehiculos()
        fetchProductos()
        fetchUsuarios()
      }
    }
  }, [router])


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
      setError(error instanceof Error ? error.message : 'Error al cargar los destinos. Por favor, intente de nuevo más tarde.')
    }
  }

  const fetchUsuarios= async () => {
    try {
      const response = await fetch('http://localhost:8082/autenticacion/usuarios')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los usuarios')
      }
      const data = await response.json()
      setUsuarios(data.filter((user: User) => user.rol === "Operario"))
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los usuarios. Por favor, intente de nuevo más tarde.')
    }
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

  const fetchProductos = async () => {
    try {
      const response = await fetch('http://localhost:8082/productos')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los productos')
      }
      const data = await response.json()
      setProductos(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los productos. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleQuesoChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const itemId = event.target.value
    setQueso(itemId)
  }

  const handleDestinoChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const itemId = parseInt(event.target.value)
    setIdDestino(itemId)
  }

  const handleVehiculoChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const itemId = parseInt(event.target.value)
    setIdVehiculo(itemId)
  }

  const handleResponsableChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const responsable = event.target.value
    setUsuario(responsable)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!fecha || !idDestino || !idVehiculo || !queso) {
      setError('Por favor, complete todos los campos obligatorios.')
      return
    }

    try {
      const response = await fetch('http://localhost:8082/despachos', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          fecha,
          idDestino,
          idVehiculo,
          queso,
          totalEnteros,
          totalMedios,
          totalCuartos,
          usuario,
        }),
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al crear el despacho')
      }

      router.push('/despachos')
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al crear el despacho. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="container mx-auto px-4 py-8 flex flex-col items-center">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Crear Nuevo Despacho</h1>
      
      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <form onSubmit={handleSubmit} className="space-y-6 w-full max-w-md bg-white shadow-md rounded-lg p-8 border border-gray-200">
        <div>
          <label htmlFor="fecha" className="block text-sm font-medium text-gray-700">Fecha</label>
          <Input
            id="fecha"
            type="date"
            value={fecha}
            onChange={(e) => setFecha(e.target.value)}
            required
          />
        </div>

        <div>
          <label htmlFor="responsable" className="block text-sm font-medium text-gray-700">Responsable</label>
          <select
            id="responsable"
            value={usuario}
            onChange={handleResponsableChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            required
          >
            <option value="">Seleccione un responsable</option>
            {usuarios.map((usuario) => (
              !usuario.isDispatching && (
                <option key={usuario.username} value={usuario.username}>
                  {usuario.username}
                </option>
              )
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="destino" className="block text-sm font-medium text-gray-700">Destino</label>
          <select
            id="destino"
            value={idDestino?.toString() || ''}
            onChange={handleDestinoChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            required
          >
            <option value="">Seleccione un destino</option>
            {destinos.map((destino) => (
              <option key={destino.id} value={destino.id.toString()}>
                {destino.calle}, {destino.numero} - {destino.barrio}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="vehiculo" className="block text-sm font-medium text-gray-700">Vehículo</label>
          <select
            id="vehiculo"
            value={idVehiculo?.toString() || ''}
            onChange={handleVehiculoChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            required
          >
            <option value="">Seleccione un vehículo</option>
            {vehiculos.map((vehiculo) => (
              vehiculo.disponible && (
                <option key={vehiculo.id} value={vehiculo.id.toString()}>
                  {vehiculo.matricula}
                </option>
              )
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="queso" className="block text-sm font-medium text-gray-700">Queso</label>
          <select
            id="queso"
            value={queso}
            onChange={handleQuesoChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            required
          >
            <option value="">Seleccione un queso</option>
            {productos.map((producto) => (
              <option key={producto.item.id} value={producto.item.nombre}>
                {producto.item.nombre}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="totalEnteros" className="block text-sm font-medium text-gray-700">Total Enteros</label>
          <Input
            id="totalEnteros"
            type="number"
            value={totalEnteros}
            onChange={(e) => setTotalEnteros(parseFloat(e.target.value))}
            min="0"
          />
        </div>

        {queso === 'Cremoso' && (
          <>
          
            <div>
              <label htmlFor="totalMedios" className="block text-sm font-medium text-gray-700">Total Medios</label>
              <Input
                id="totalMedios"
                type="number"
                value={totalMedios}
                onChange={(e) => setTotalMedios(parseFloat(e.target.value))}
                min="0"
              />
            </div>

            <div>
              <label htmlFor="totalCuartos" className="block text-sm font-medium text-gray-700">Total Cuartos</label>
              <Input
                id="totalCuartos"
                type="number"
                value={totalCuartos}
                onChange={(e) => setTotalCuartos(parseFloat(e.target.value))}
                min="0"
              />
            </div>
          </>
        )}


        <div className="flex justify-end space-x-2">
          <Button type="button" className='bg-yellow-500 hover:bg-yellow-600 text-black' variant="secondary" onClick={() => router.push('/despachos')}>
            Cancelar
          </Button>
          <Button type="submit" className='bg-green-500 hover:bg-green-600 text-white transition-colors inline-block font-semibold'>
            Crear Despacho
          </Button>
        </div>
      </form>
    </div>
  )
}