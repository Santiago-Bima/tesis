'use client'

import { useState, useEffect } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import Destino from '@/interfaces/destino'

export default function DestinoForm() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const id = searchParams.get('id')
  const [destino, setDestino] = useState<Destino>({
    id: 0,
    calle: '',
    numero: 0,
    barrio: ''
  })
  const [error, setError] = useState<string | null>(null)


  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
        router.push('/')
      } else {
        if (id) {
          fetchDestino()
        }
      }
    }
  }, [router, id])

  const fetchDestino = async () => {
    try {
      const response = await fetch(`http://localhost:8082/destinos/${id}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar el destinos')
      }
      const data = await response.json()
      setDestino(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar el destino. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const url = id ? `http://localhost:8082/destinos/${id}` : 'http://localhost:8082/destinos'
      const method = id ? 'PUT' : 'POST'
      
      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          calle: destino.calle,
          numero: destino.numero,
          barrio: destino.barrio
        }),
      })
      
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al guardar el destinos')
      }
      
      router.push('/destinos')
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al guardar el destino. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="container mx-auto px-4 py-8 flex flex-col items-center">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">
        {id ? 'Editar Destino' : 'Crear Nuevo Destino'}
      </h1>
      
      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <form onSubmit={handleSubmit} className="space-y-6 w-full max-w-md bg-white shadow-md rounded-lg p-8 border border-gray-200">
        <div>
          <Label htmlFor="calle">Calle</Label>
          <Input
            id="calle"
            value={destino.calle}
            onChange={(e) => setDestino({...destino, calle: e.target.value})}
            required
          />
        </div>
        <div>
          <Label htmlFor="numero">Número</Label>
          <Input
            id="numero"
            type="number"
            value={destino.numero}
            onChange={(e) => setDestino({...destino, numero: parseFloat(e.target.value)})}
            required
          />
        </div>
        <div>
          <Label htmlFor="barrio">Barrio</Label>
          <Input
            id="barrio"
            value={destino.barrio}
            onChange={(e) => setDestino({...destino, barrio: e.target.value})}
            required
          />
        </div>
        <div className="flex justify-end space-x-2">
          <Button type="button" className='bg-yellow-500 hover:bg-yellow-600 text-black' variant="secondary" onClick={() => router.push('/destinos')}>
            Cancelar
          </Button>
          <Button type="submit" className='bg-green-500 hover:bg-green-600 text-white transition-colors inline-block font-semibold'>
            {id ? 'Guardar Cambios' : 'Crear Destino'}
          </Button>
        </div>
      </form>
    </div>
  )
}