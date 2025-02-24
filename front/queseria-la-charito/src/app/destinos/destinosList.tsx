'use client'

import { useState, useEffect } from 'react'
import Link from 'next/link'
import { Button } from "@/components/ui/button"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from 'lucide-react'
import Destino from '@/interfaces/destino'
import { useRouter } from 'next/navigation'
import { DialogFooter, DialogHeader, Dialog, DialogContent, DialogDescription, DialogTitle } from '@/components/ui/dialog'

export default function DestinosList() {
  const [destinos, setDestinos] = useState<Destino[]>([])
  const [error, setError] = useState<string | null>(null)
  const router = useRouter();
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [destinoToDelete, setDestinoToDelete] = useState<number | null>(null)

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
        router.push('/')
      } else {
        fetchDestinos()
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

  const handleDeleteDestino = async () => {
    if (destinoToDelete == null) return;
    
    try {
      const response = await fetch(`http://localhost:8082/destinos/${destinoToDelete}`, {
        method: 'DELETE',
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al eliminar los destinos')
      }
      fetchDestinos()
      setIsDeleteDialogOpen(false)
      setDestinoToDelete(null)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al eliminar el destino. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Lista de Destinos</h1>
      
      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <div className="mt-8 text-center mb-8">
        <Link
          href="/destinos/nuevo"
          className="bg-green-500 hover:bg-green-600 text-white px-6 py-3 rounded-md transition-colors inline-block font-semibold"
        >
          Agregar Nuevo Destino
        </Link>
      </div>

      <ul className="space-y-4">
        {destinos.map((destino) => (
          <li key={destino.id} className="bg-white p-4 rounded-lg shadow">
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
              <span className="mb-2 sm:mb-0">{destino.calle}, {destino.numero} - {destino.barrio}</span>
              <div className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2">
                <Link href={`/destinos/editar?id=${destino.id}`} className="w-full sm:w-auto">
                  <Button type="button" className='bg-yellow-500 hover:bg-yellow-600 text-black w-full sm:w-auto' variant="secondary">
                    Editar
                  </Button>
                </Link>
                <Button 
                  variant="destructive" 
                  className='bg-red-500 hover:bg-red-600 text-white transition-colors font-semibold w-full sm:w-auto' 
                  onClick={() => {
                    setDestinoToDelete(destino.id)
                    setIsDeleteDialogOpen(true)
                  }}
                >
                  Eliminar
                </Button>
              </div>
            </div>
          </li>
        ))}
      </ul>

      <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirmar eliminación</DialogTitle>
            <DialogDescription>
              ¿Está seguro de que desea eliminar este destino? Esta acción no se puede deshacer.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="sm:justify-end">
            <Button 
              variant="outline" 
              onClick={() => setIsDeleteDialogOpen(false)} 
              className="bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors font-semibold w-full sm:w-auto mb-2 sm:mb-0"
            >
              Cancelar
            </Button>
            <Button 
              variant="destructive" 
              onClick={handleDeleteDestino} 
              className="bg-red-500 hover:bg-red-600 text-white rounded-md transition-colors font-semibold w-full sm:w-auto"
            >
              Eliminar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}