'use client'

import { useState, useEffect } from 'react'
import Link from 'next/link'
import { Item } from '../../interfaces/item'
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { Button } from "@/components/ui/button"
import { useRouter } from 'next/navigation'
import { DialogFooter, DialogHeader, Dialog, DialogContent, DialogDescription, DialogTitle } from '@/components/ui/dialog'

export default function InsumosList() {
  const [insumos, setInsumos] = useState<Item[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const router = useRouter();

  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [insumoToDelete, setInsumoToDelete] = useState<number | null>(null)

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
          if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
            router.push('/')
          }
    }

    fetchInsumos()
  }, [router])

  const fetchInsumos = async () => {
    setIsLoading(true)
    setError(null)
    try {
      const response = await fetch('http://localhost:8082/insumos')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los insumos')
      }
      const data = await response.json()
      setInsumos(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar el insumo. Por favor, intente de nuevo más tarde.')
    } finally {
      setIsLoading(false)
    }
  }

  const eliminarInsumo = async () => {
    if(insumoToDelete == null) return;
    try {
      const response = await fetch(`http://localhost:8082/insumos/${insumoToDelete}`, {
        method: 'DELETE',
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al eliminar los insumos')
      }
      await fetchInsumos()
      setInsumoToDelete(null)
      setIsDeleteDialogOpen(false)
    } catch (error ) {
      setError(error instanceof Error ? error.message : 'Error al eliminar el insumo. Por favor, intente de nuevo más tarde.')
    }
    
  }

  if (isLoading) {
    return <div className="text-center py-4">Cargando insumos...</div>
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Lista de Insumos</h1>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>
            {error} 
            <Button onClick={() => setError(null)} variant="secondary">
              Ok
            </Button>
          </AlertDescription>
        </Alert>
      )}

      <div className="mt-8 text-center mb-8">
        <Link
          href="/insumos/nuevo"
          className="bg-green-500 hover:bg-green-600 text-white px-6 py-3 rounded-md transition-colors inline-block font-semibold"
        >
          Agregar Nuevo Insumo
        </Link>
      </div>

      {insumos.length === 0 ? (
        <p className="text-gray-500 text-center py-4">No hay insumos registrados.</p>
      ) : (
        <ul className="space-y-4">
          {insumos.map(insumo => (
            <li key={insumo.id} className="bg-gray-50 rounded-lg p-4 flex justify-between items-center shadow-sm hover:shadow-md transition-shadow">
              <div>
                <span className="font-semibold text-lg text-gray-800">{insumo.nombre}</span>
                <span className="ml-2 text-sm text-gray-500">({insumo.unidad_medida})</span>
              </div>
              <div className="space-x-2">
                <Link
                  href={`/insumos/${insumo.id}`}
                  className="bg-yellow-500 hover:bg-yellow-600 text-black px-4 py-2 rounded-md transition-colors"
                >
                  Editar
                </Link>
                <button
                  onClick={() => {
                    setInsumoToDelete(insumo.id)
                    setIsDeleteDialogOpen(true)
                  }}
                  className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-md transition-colors"
                >
                  Eliminar
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}

    <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirmar eliminación</DialogTitle>
            <DialogDescription>
              ¿Está seguro de que desea eliminar este insumo? Esta acción no se puede deshacer.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)} className="bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors inline-block font-semibold">Cancelar</Button>
            <Button variant="destructive" onClick={eliminarInsumo} className="bg-red-500 hover:bg-red-600 text-white rounded-md transition-colors inline-block font-semibold">Eliminar</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
      
    </div>
  )
}