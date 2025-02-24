'use client'

import { useState, useEffect, useMemo } from 'react'
import Link from 'next/link'
import { Button } from "@/components/ui/button"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from 'lucide-react'
import { Item } from '@/interfaces/item'
import Lote from '@/interfaces/lote'
import { useRouter } from 'next/navigation'
import { NumberFormatter } from '@/utils/numberFormatter'
import { DialogFooter, DialogHeader, Dialog, DialogContent, DialogDescription, DialogTitle } from '@/components/ui/dialog'

export default function LotesList() {
  const [items, setItems] = useState<Item[]>([])
  const [estados, setEstados] = useState<string[]>([])
  const [lotes, setLotes] = useState<Lote[]>([])
  const [selectedItemId, setSelectedItemId] = useState<number | null>(null)
  const [selectedEstado, setSelectedEstado] = useState<string>('')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const router = useRouter()
  const formatter = new NumberFormatter('es-ES');
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [loteToDelete, setLoteToDelete] = useState<string | null>(null)

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Operario') {
        router.push('/')
      }
    }

    fetchItems()
    fetchEstados()
  }, [router])

  useEffect(() => {
    if (selectedItemId && selectedEstado) {
      fetchLotes(selectedItemId, selectedEstado)
    }
  }, [selectedItemId, selectedEstado])

  const fetchItems = async () => {
    setIsLoading(true)
    setError(null)
    try {
      const [insumosRes, productosRes] = await Promise.all([
        fetch('http://localhost:8082/insumos'),
        fetch('http://localhost:8082/productos')
      ])
      if (!insumosRes.ok || !productosRes.ok) {
        throw new Error('Error al cargar los items')
      }
      const insumosData = await insumosRes.json()
      const productosData = await productosRes.json()
      
      const combinedItems: Item[] = [
        ...insumosData.map((insumo: any) => ({ id: insumo.id, nombre: insumo.nombre })),
        ...productosData.map((producto: any) => ({ id: producto.item.id, nombre: producto.item.nombre }))
      ]
      setItems(combinedItems)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los items. Por favor, intente de nuevo más tarde.')
    } finally {
      setIsLoading(false)
    }
  }

  const filteredEstados = useMemo(() => {
    if (!selectedItemId) return [];
    const selectedItem = items.find(item => item.id === selectedItemId);
    if (!selectedItem) return [];
  
    const specialItems = ['Pategras', 'Cremoso', 'Barra'];
    if (specialItems.includes(selectedItem.nombre)) {
      return estados.filter(estado => ['Elaborando', 'Terminado', 'Despachado'].includes(estado));
    } else {
      return estados.filter(estado => ['SinStock', 'Disponible'].includes(estado));
    }
  }, [selectedItemId, items, estados]);

  const fetchEstados = async () => {
    try {
      const response = await fetch('http://localhost:8082/estados')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los estados')
      }
      const data = await response.json()
      setEstados(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los estados. Por favor, intente de nuevo más tarde.')
    }
  }

  const fetchLotes = async (itemId: number, estado: string) => {
    setIsLoading(true)
    setError(null)
    try {
      const response = await fetch(`http://localhost:8082/lotes/${itemId}/${estado}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los lotes')
      }
      const data = await response.json()
      setLotes(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los lotes. Por favor, intente de nuevo más tarde.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleItemChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const itemId = parseInt(event.target.value)
    setSelectedItemId(itemId)
  }

  const handleEstadoChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedEstado(event.target.value)
  }

  const eliminarLote = async () => {
    if (loteToDelete == null) return;
    try {
      const response = await fetch(`http://localhost:8082/lotes/${loteToDelete}`, {
        method: 'DELETE',
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al eliminar el lote')
      }
      if (selectedItemId && selectedEstado) {
        fetchLotes(selectedItemId, selectedEstado)
        setIsDeleteDialogOpen(false)
        setLoteToDelete(null)
      }
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al eliminar el lote. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Lista de Lotes</h1>
      
      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6">
        <div>
          <label htmlFor="item" className="block text-sm font-medium text-gray-700 mb-1">
            Seleccionar Item:
          </label>
          <select
            id="item"
            value={selectedItemId?.toString() || ''}
            onChange={handleItemChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="" disabled>Seleccione un item</option>
            {items.map((item) => (
              <option key={item.id} value={item.id.toString()}>
                {item.nombre}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label htmlFor="estado" className="block text-sm font-medium text-gray-700 mb-1">
            Seleccionar Estado:
          </label>
          <select
            id="estado"
            value={selectedEstado}
            onChange={handleEstadoChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="" disabled>Seleccione un estado</option>
            {filteredEstados.map((estado) => (
              <option key={estado} value={estado}>
                {estado.replace(/([A-Z])/g, ' $1').trim()}
              </option>
            ))}
          </select>
        </div>
      </div>

      {isLoading ? (
        <div className="text-center py-4">Cargando...</div>
      ) : lotes.length > 0 ? (
        <ul className="space-y-4">
          {lotes.map((lote) => (
            <li key={lote.codigo} className="bg-gray-50 rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow">
              <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center">
                <div className="mb-2 sm:mb-0">
                  <span className="font-semibold text-lg text-gray-800">{lote.codigo}</span>
                  <span className="block sm:inline sm:ml-2 text-sm text-gray-500">
                    ({lote.item.nombre} - {formatter.formatNumber(lote.unidades, true)} {lote.item.unidad_medida})
                  </span>
                </div>
                <div className="flex flex-col sm:flex-row items-start sm:items-center space-y-2 sm:space-y-0 sm:space-x-2 w-full sm:w-auto">
                  <Link href={`/lotes/${lote.codigo}`} className="w-full sm:w-auto">
                    <Button variant="outline" className="w-full sm:w-auto bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors inline-block">Editar</Button>
                  </Link>
                  <Button variant="destructive" className="w-full sm:w-auto bg-red-500 hover:bg-red-600 text-black rounded-md transition-colors inline-block" onClick={() => {
                    setIsDeleteDialogOpen(true)
                    setLoteToDelete(lote.codigo)
                  }}>
                    Eliminar
                  </Button>
                </div>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <div className="text-center py-4 text-gray-500">No hay lotes disponibles para el item y estado seleccionados.</div>
      )}

      <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Confirmar eliminación</DialogTitle>
            <DialogDescription>
              ¿Está seguro de que desea eliminar este lote? Esta acción no se puede deshacer.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)} className="w-full sm:w-auto bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors inline-block font-semibold">Cancelar</Button>
            <Button variant="destructive" onClick={eliminarLote} className="w-full sm:w-auto bg-red-500 hover:bg-red-600 text-white rounded-md transition-colors inline-block">Eliminar</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}