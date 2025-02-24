'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"

export default function ControlCalidadPage({ params }: { params: { id: string } }) {
  const router = useRouter()
  const [control, setControl] = useState({
    fecha: '',
    pruebaSabor: '',
    pruebaConcistencia: '',
    pruebaAroma: '',
    observacion: ''
  })
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Operario') {
        router.push('/')
      }
    }

  }, [router])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const response = await fetch(`http://localhost:8082/elaboraciones/controles/${params.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(control),
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al guardar el control de calidad')
      }
      router.push('/elaboraciones')
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al guardar el control de calidad. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="container mx-auto px-4 py-8 flex flex-col items-center">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Control de Calidad</h1>
      
      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <form onSubmit={handleSubmit} className="space-y-6 w-full max-w-md bg-white shadow-md rounded-lg p-8 border border-gray-200">
        <h4 className="text-2xl font-bold mb-6 text-indigo-700 text-center">Elaboración {params.id}</h4>
        <div>
          <Label htmlFor="fecha">Fecha</Label>
          <Input
            id="fecha"
            type="date"
            value={control.fecha}
            onChange={(e) => setControl({...control, fecha: e.target.value})}
            required
          />
        </div>
        <div>
          <Label htmlFor="pruebaSabor">Prueba de Sabor</Label>
          <Input
            id="pruebaSabor"
            value={control.pruebaSabor}
            onChange={(e) => setControl({...control, pruebaSabor: e.target.value})}
            required
          />
        </div>
        <div>
          <Label htmlFor="pruebaConcistencia">Prueba de Consistencia</Label>
          <Input
            id="pruebaConcistencia"
            value={control.pruebaConcistencia}
            onChange={(e) => setControl({...control, pruebaConcistencia: e.target.value})}
            required
          />
        </div>
        <div>
          <Label htmlFor="pruebaAroma">Prueba de Aroma</Label>
          <Input
            id="pruebaAroma"
            value={control.pruebaAroma}
            onChange={(e) => setControl({...control, pruebaAroma: e.target.value})}
            required
          />
        </div>
        <div>
          <Label htmlFor="observacion">Observación</Label>
          <Textarea
            id="observacion"
            value={control.observacion}
            onChange={(e) => setControl({...control, observacion: e.target.value})}
          />
        </div>
        <div className="flex justify-end space-x-2">
          <Button type="button" className='bg-yellow-500 hover:bg-yellow-600 text-black' variant="secondary" onClick={() => router.push('/elaboraciones')}>
            Cancelar
          </Button>
          <Button type="submit" className='bg-green-500 hover:bg-green-600 text-white transition-colors inline-block font-semibold'>Guardar</Button>
        </div>
      </form>
    </div>
  )
}