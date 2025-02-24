'use client'

import { useState, useEffect } from 'react'
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import formatDate from '@/utils/formatDate'
import ComprobanteResult from '@/interfaces/Comprobante'
import { useRouter } from 'next/navigation'
import { NumberFormatter } from '@/utils/numberFormatter'

export default function ComprobantesList() {
  const [comprobantes, setComprobantes] = useState<ComprobanteResult[]>([])
  const [selectedDate, setSelectedDate] = useState<string | undefined>('')
  const [error, setError] = useState<string | null>(null)

  const router = useRouter();
  const formatter = new NumberFormatter('es-ES');


  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
        router.push('/')
      } else {
        if (selectedDate) {
          fetchComprobantes(formatDate(selectedDate))
        }
      }
    }
  }, [router, selectedDate])

  const handleDateChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    setSelectedDate(e.target.value);
    if (selectedDate === undefined || selectedDate === '') return;
  }

  const fetchComprobantes = async (fecha: string) => {
    const rawValue = fecha;
    const [year, day, month] = rawValue.split("-");
    try {
      const response = await fetch(`http://localhost:8082/comprobantes?fecha=${year}-${month}-${day}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los comprobantes')
      }
      const data = await response.json()
      setComprobantes(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los comprobantes. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Lista de Comprobantes</h1>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <div className="mb-6">
        <Input
          type="date"
          value={selectedDate}
          onChange={value => handleDateChange(value)}
          required
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {comprobantes.map((comprobante) => (
          <Card key={comprobante.id}>
            <CardHeader>
              <CardTitle>Resumen de la Compra</CardTitle>
            </CardHeader>
            <CardContent>
              <p>Total Gastado: ${formatter.formatCurrency(comprobante.total)}</p>
              <h2 className="text-xl font-semibold mt-4 mb-2">Detalles:</h2>
              <ul>
                {comprobante.detalles.map((detail, index) => (
                  <li key={index} className="mb-2">
                    <p>Lote: {detail.lote.codigo}</p>
                    <p>Proveedor: {detail.proveedor.nombre}</p>
                    <p>Cantidad: {formatter.formatNumber(detail.cantidad, true)} {detail.proveedor.unidadMedida.toUpperCase()}</p>
                    <p>Subtotal: ${formatter.formatCurrency(detail.subtotal)}</p>
                  </li>
                ))}
              </ul>
            </CardContent>
          </Card>
        ))}
      </div>

      {comprobantes.length === 0 && (
        <p className="text-center text-gray-500 mt-6">No se encontraron comprobantes para la fecha seleccionada.</p>
      )}
    </div>
  )
}