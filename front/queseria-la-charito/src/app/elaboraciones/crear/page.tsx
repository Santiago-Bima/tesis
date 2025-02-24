'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import { TipoQueso } from '@/interfaces/tipoQueso'
import { Formula } from '@/interfaces/formula'
import formatDate from '@/utils/formatDate'

export default function CrearElaboracionPage() {
  const router = useRouter()
  const [productos, setProductos] = useState<TipoQueso[]>([])
  const [formulas, setFormulas] = useState<Formula[]>([])
  const [error, setError] = useState<string | null>(null)
  const [newElaboracion, setNewElaboracion] = useState({
    fecha: formatDate(new Date().toLocaleDateString()),
    cantidadLeche: 0,
    idFormula: '',
    tiempoSalado: 0,
    usuario: ''
  })
  const [selectedProducto, setSelectedProducto] = useState<number | null>(null)

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Operario') {
        router.push('/')
      } else {
        setNewElaboracion({...newElaboracion, usuario: JSON.parse(usuarioLogeado).username})
        fetchProductos()
      }
    }

  }, [router])

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

  const fetchFormulas = async (productoId: number) => {
    try {
      const response = await fetch(`http://localhost:8082/formulas/listarBy/${productoId}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar las fórmulas')
      }
      const data = await response.json()
      setFormulas(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar las fórmulas. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleProductoChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const itemId = parseInt(event.target.value)
    setSelectedProducto(itemId)
    setNewElaboracion({ ...newElaboracion, idFormula: '' })
    fetchFormulas(itemId)
  }

  const handleFormulaChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const itemId = event.target.value
    setNewElaboracion({...newElaboracion, idFormula: itemId})
  }

  const handleCreateElaboracion = async (e: React.FormEvent) => {
    e.preventDefault()

    const rawValue = new Date().toLocaleDateString();
    const [day, month, year] = rawValue.split("/");
    
    newElaboracion.fecha = `${year}-${month}-${day}`;
    try {
      const response = await fetch('http://localhost:8082/elaboraciones', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newElaboracion),
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al crear la elaboración')
      }
      router.push('/elaboraciones')
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al crear la elaboración. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="container mx-auto px-4 py-8 flex flex-col items-center">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Crear Nueva Elaboración</h1>
      
      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <form onSubmit={handleCreateElaboracion} className="space-y-6 w-full max-w-md bg-white shadow-md rounded-lg p-8 border border-gray-200">
        <div className="space-y-4">
          <div>
            <Label htmlFor="cantidadLeche">Cantidad de Leche</Label>
            <Input
              id="cantidadLeche"
              type="number"
              value={newElaboracion.cantidadLeche}
              onChange={(e) => setNewElaboracion({...newElaboracion, cantidadLeche: parseFloat(e.target.value)})}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50"
              required
            />
          </div>
          <div>
            <Label htmlFor="producto">Producto</Label>
            <select
              value={selectedProducto?.toString() || ''}
              onChange={handleProductoChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
              required
            >
              <option value="">Seleccione un producto</option>
              {productos.map((producto) => (
                <option key={producto.id_tipo_queso} value={producto.id_tipo_queso}>
                  {producto.item.nombre}
                </option>
              ))}
            </select>
          </div>
          <div>
            <Label htmlFor="idFormula">Fórmula</Label>
            <select
              value={newElaboracion.idFormula}
              onChange={handleFormulaChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
              required
              disabled={!selectedProducto}
            >
              <option value="">Seleccione una fórmula</option>
              {formulas.map((formula) => (
                <option key={formula.codigo} value={formula.codigo}>
                  {formula.codigo}
                </option>
              ))}
            </select>
          </div>
          <div>
            <Label htmlFor="tiempoSalado">Tiempo de Salado</Label>
            <Input
              id="tiempoSalado"
              type="number"
              value={newElaboracion.tiempoSalado}
              onChange={(e) => setNewElaboracion({...newElaboracion, tiempoSalado: parseFloat(e.target.value)})}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50"
              required
            />
          </div>
        </div>
        <div className="flex justify-end space-x-2 pt-4">
          <Button type="button" className='bg-yellow-500 hover:bg-yellow-600 text-black' variant="secondary" onClick={() => router.push('/elaboraciones')}>
            Cancelar
          </Button>
          <Button type="submit" className='bg-green-500 hover:bg-green-600 text-white transition-colors inline-block font-semibold'>Crear Elaboración</Button>
        </div>
      </form>
    </div>
  )
}