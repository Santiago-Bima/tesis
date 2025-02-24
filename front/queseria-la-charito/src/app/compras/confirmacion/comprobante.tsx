'use client'

import { useEffect, useState } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import ComprobanteResult from '@/interfaces/Comprobante'
import { NumberFormatter } from '@/utils/numberFormatter'
import formatArgDate from '@/utils/formatArgDate'

export default function Comprobante() {
  const [paymentResult, setPaymentResult] = useState<ComprobanteResult | null>(null)
  const searchParams = useSearchParams()

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
        const result = searchParams.get('result')
        if (result) {
          setPaymentResult(JSON.parse(decodeURIComponent(result)))
        }
      }
    }
  }, [router, searchParams])

  if (!paymentResult) {
    return <div>Cargando resultado del pago...</div>
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Confirmación de Pago</h1>

      <Card>
        <CardHeader>
          <CardTitle>Resumen de la Compra</CardTitle>
        </CardHeader>
        <CardContent>
          <p>Fecha de Pago: {formatArgDate(paymentResult.fecha)}</p>
          <p>Total Gastado: ${formatter.formatCurrency(paymentResult?.total)}</p>
          <h2 className="text-xl font-semibold mt-4 mb-2">Detalles:</h2>
          <ul>
            {paymentResult.detalles.map((detail, index) => (
              <li key={index} className="mb-2">
                <p>Proveedor: {detail.proveedor.nombre}</p>
                <p>Cantidad: {formatter.formatNumber(detail.cantidad, true)} {detail.proveedor.unidadMedida.toUpperCase()}</p>
                <p>Subtotal: ${formatter.formatCurrency(detail.subtotal)}</p>
              </li>
            ))}
          </ul>
        </CardContent>
      </Card>
    </div>
  )
}