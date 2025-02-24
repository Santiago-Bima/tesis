'use client'

import { useEffect, useState } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import Script from 'next/script'
import { Button } from "@/components/ui/button"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import { enviroment } from '../../../../enviroment'

declare global {
  interface Window {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    MercadoPago: any;
  }
}

interface PurchaseData {
  total: number;
  details: Array<{
    idProveedor: string;
    cantidad: number;
    subtotal: number;
  }>;
}

export default function PagoForm() {
  const [error, setError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [purchaseData, setPurchaseData] = useState<PurchaseData | null>(null)
  const router = useRouter()
  const searchParams = useSearchParams()


  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
        router.push('/')
      } else {
        const data = searchParams.get('data')
        if (data) {
          setPurchaseData(JSON.parse(decodeURIComponent(data)))
        }
      }
    }
  }, [router, searchParams])

  useEffect(() => {
    if (typeof window.MercadoPago !== 'undefined' && purchaseData) {
      initializeBrick()
    }
  }, [])

  const sendComprobante = async (data: unknown) => {
    try {
      const url = 'http://localhost:8082/comprobantes'
      const method = 'POST'

      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al guardar el comprobante de pago')
      }

      return await response.json();
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al guardar el comprobante. Por favor, intente de nuevo más tarde.')
    }
  }

  const initializeBrick = async () => {
    if (!purchaseData) return

    try {
      const mp = new window.MercadoPago(enviroment.MERCADO_PAGO_API_KEY)
      const bricksBuilder = mp.bricks()

      await bricksBuilder.create('cardPayment', 'cardPaymentBrick_container', {
        initialization: {
          amount: purchaseData.total, 
        },
        customization: {
          visual: {
            style: {
              theme: 'default'
            }
          }
        },
        callbacks: {
          onReady: () => {
            setIsLoading(false)
          },
          // eslint-disable-next-line @typescript-eslint/no-unused-vars, @typescript-eslint/no-explicit-any
          onSubmit: async (cardFormData: any) => {
            setIsLoading(true)
            try {
              // Here you would typically send the cardFormData to your backend
              // Your backend would then create a payment using Mercado Pago's API
              // For this example, we'll simulate a successful payment
              await new Promise(resolve => setTimeout(resolve, 1000))

              const result = await sendComprobante(purchaseData)
              
              const paymentResult = {
                success: true,
                fecha: result.fecha,
                total: result.total,
                detalles: result.detalles
              }
              

              router.push('/compras/confirmacion?result=' + encodeURIComponent(JSON.stringify(paymentResult)))
            } catch (error) {
              setError('Error al procesar el pago. Por favor, intente de nuevo más tarde.')
              setIsLoading(false)
            }
          },
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          onError: (error: any) => {
            setError(`Error: ${error.message}`)
            setIsLoading(false)
          }
        }
      })
    } catch (error) {
      setError('Error al inicializar el formulario de pago. Por favor, intente de nuevo más tarde.')
      setIsLoading(false)
    }
  }

  if (!purchaseData) {
    return <div>Error: No se encontraron datos de compra</div>
  }
  
  return (
    <div className="container mx-auto px-4 py-8">
      <Script
        src="https://sdk.mercadopago.com/js/v2"
        onLoad={() => initializeBrick()}
      />

      <div className='flex flex-row justify-between align-center'>
        <h1 className="text-3xl font-bold mb-6 text-indigo-700">Pago de Insumos</h1>
        <Button onClick={() => router.push('/compras')} className="bg-yellow-500 hover:bg-yellow-600 text-black hidden sm:block">
          Volver a la selección de insumos
        </Button>
      </div>

      <Button onClick={() => router.push('/compras')} className="bg-yellow-500 hover:bg-yellow-600 text-black block sm:hidden">
          Volver a la selección de insumos
      </Button>
      
      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {isLoading && <p>Cargando formulario de pago...</p>}

      <div id="cardPaymentBrick_container"></div>
    </div>
  )
}