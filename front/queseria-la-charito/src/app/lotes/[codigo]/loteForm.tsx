'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import Lote from '@/interfaces/lote'
import { Textarea } from '@/components/ui/textarea'


export default function LoteForm({ codigo }: { codigo: string }) {
  const router = useRouter()
  const [lote, setLote] = useState<Lote | null>(null)
  const [unidades, setUnidades] = useState<number>(0)
  const [motivos, setMotivos] = useState<string>('')
  const [fecha] = useState<Date>(new Date());
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [usuario, setUsuario] = useState<string>('')


  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Operario') {
        router.push('/')
      }

      setUsuario(JSON.parse(usuarioLogeado).username)
    }

    const fetchLote = async () => {
      try {
        const response = await fetch(`http://localhost:8082/lotes/${codigo}`)
        if (!response.ok) {
          const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar el lote')
        }
        const data = await response.json()
        setLote(data)
        setUnidades(data.unidades)
      } catch (error) {
        setError(error instanceof Error ? error.message : 'Error al cargar el lote. Por favor, intente de nuevo más tarde.')
      } finally {
        setIsLoading(false)
      }
    }

    fetchLote()
  }, [router, codigo])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)

    try {
      const response = await fetch(`http://localhost:8082/lotes/${codigo}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ fecha, unidades, motivos, usuario }),
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al actualizar el lote')
      }

      router.push('/lotes')
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al actualizar el lote. Por favor, intente de nuevo más tarde.')
    } finally {
      setIsLoading(false)
    }
  }

  if (isLoading) {
    return <div className="text-center py-4">Cargando...</div>
  }

  if (!lote) {
    return <div className="text-center py-4 text-red-500">Lote no encontrado</div>
  }

  return (
    <div className="container mx-auto px-4 py-8 flex flex-col items-center">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Edición de lote</h1>
      
      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <form onSubmit={handleSubmit} className="space-y-6 w-full max-w-md bg-white shadow-md rounded-lg p-8 border border-gray-200">
        <div>
          <label htmlFor="codigo" className="block text-sm font-medium text-gray-700 mb-1">
            Código:
          </label>
          <Input id="codigo" value={lote.codigo} readOnly />
        </div>
        <div>
          <label htmlFor="item" className="block text-sm font-medium text-gray-700 mb-1">
            Item:
          </label>
          <Input id="item" value={lote.item.nombre} readOnly />
        </div>
        <div>
          <label htmlFor="estado" className="block text-sm font-medium text-gray-700 mb-1">
            Estado:
          </label>
          <Input id="estado" value={lote.estado} readOnly />
        </div>
        <div>
          <label htmlFor="unidades" className="block text-sm font-medium text-gray-700 mb-1">
            Unidades restantes:
          </label>
          <Input
            type="number"
            id="unidades"
            value={unidades}
            onChange={(e) => setUnidades(Math.max(0, parseInt(e.target.value)))}
            min="0"
            required
          />
        </div>
        <div>
          <label htmlFor="motivos" className="block text-sm font-medium text-gray-700 mb-1">
            Motivo:
          </label>
          <Textarea
            id="motivos"
            value={motivos}
            onChange={(e) => setMotivos(e.target.value)}
            required
          />
        </div>
        <div className="flex justify-between items-center">
          <Button type="button" variant="outline"  className="bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors inline-block font-semibold" onClick={() => router.push('/lotes')}>
            Cancelar
          </Button>
          <Button type="submit" disabled={isLoading}  className="bg-green-500 hover:bg-green-600 text-white rounded-md transition-colors inline-block font-semibold">
            {isLoading ? 'Guardando...' : 'Guardar'}
          </Button>
        </div>
      </form>
    </div>
  )
}