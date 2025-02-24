'use client'

import { useState, useEffect } from 'react'
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from 'lucide-react'
import { Textarea } from "@/components/ui/textarea"
import { useRouter } from 'next/navigation'
import { NumberFormatter } from '@/utils/numberFormatter'

interface LoteControlStock {
  codigo: string
  item: string
  unidades: number
  corte: string
}

interface CantidadInsumo {
  insumo: string
  cantidad: number
}

interface ValoresEsperados {
  cantidadEnterosEsperada: number
  CantidadMediosEsperada: number
  cantidadCuartosEsperada: number
  cantidadesInsumos: CantidadInsumo[]
}

interface ControlData {
  fecha: string
  cantidadEnterosObtenida: number
  cantidadMediosObtenida: number
  cantidadCuartosObtenida: number
  cantidadesInsumos: CantidadInsumo[]
  observaciones: string,
  usuario: string
}

export default function ControlStockNuevo() {
  const [items, setItems] = useState<string[]>([])
  const [selectedItem, setSelectedItem] = useState<string>('')
  const [lotes, setLotes] = useState<LoteControlStock[]>([])
  const [valoresEsperados, setValoresEsperados] = useState<ValoresEsperados | null>(null)
  const [controlData, setControlData] = useState<ControlData>({
    fecha: new Date().toISOString().split('T')[0],
    cantidadEnterosObtenida: 0,
    cantidadMediosObtenida: 0,
    cantidadCuartosObtenida: 0,
    cantidadesInsumos: [],
    observaciones: '',
    usuario: ''
  })
  const [error, setError] = useState<string | null>(null)
  const formatter = new NumberFormatter('es-ES');

  const router = useRouter();

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Operario') {
        router.push('/')
      } else {
        fetchItems()
        fetchValoresEsperados()
        setControlData({...controlData, usuario: JSON.parse(usuarioLogeado).username})
      }
    }
  }, [router])

  useEffect(() => {
    if (selectedItem) {
      fetchLotes(selectedItem)
    }
  }, [selectedItem])

  const fetchItems = async () => {
    try {
      const response = await fetch('http://localhost:8082/items')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los items')
      }
      const data = await response.json()
      setItems(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los items. Por favor, intente de nuevo más tarde.')
    }
  }

  const fetchLotes = async (item: string) => {
    try {
      const response = await fetch(`http://localhost:8082/lotes/controlStock/${item}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los lotes')
      }
      const data = await response.json()
      setLotes(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los lotes. Por favor, intente de nuevo más tarde.')
    }
  }

  const fetchValoresEsperados = async () => {
    try {
      const response = await fetch('http://localhost:8082/controles/valoresEsperados')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los valores esperados')
      }
      const data = await response.json()

      if (data.cantidadesInsumos == null) data.cantidadesInsumos = [];
      
      setValoresEsperados(data)
      setControlData(prevData => ({
        ...prevData,
        cantidadesInsumos: data.cantidadesInsumos.map((insumo: CantidadInsumo) => ({
          ...insumo,
          cantidad: 0
        }))
      }))
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los valores esperados. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
  
    setControlData(prevData => ({
      ...prevData,
      [name]: name === 'observaciones' ? value : parseFloat(value)
    }))
  }

  const handleInsumoChange = (insumo: string, cantidad: number) => {
    
    setControlData(prevData => ({
      ...prevData,
      cantidadesInsumos: prevData.cantidadesInsumos.map(item =>
        item.insumo === insumo ? { ...item, cantidad } : item
      )
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const response = await fetch('http://localhost:8082/controles', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(controlData),
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al enviar el control')
      }
      // Reset form or redirect as needed
      router.push('/')
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al enviar el control. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleItemChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedItem(event.target.value)
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Control de Stock</h1>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="space-y-6">
          <h2 className="text-xl font-semibold mb-4">Lista de Lotes</h2>
          <select
            value={selectedItem}
            onChange={handleItemChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">Seleccione un item</option>
            {items.map((item, index) => (
              <option key={index} value={item}>{item}</option>
            ))}
          </select>

          {lotes.length > 0 ? (
            <ul className="space-y-4">
              {lotes.map((lote, index) => (
                <li key={index}>
                  <Card>
                    <CardContent className="p-4">
                      <p><strong>Código:</strong> {lote.codigo}</p>
                      <p><strong>Item:</strong> {lote.item}</p>
                      <p><strong>Unidades:</strong> {formatter.formatNumber(lote.unidades, true)}</p>
                      {lote.corte != null && (<p><strong>Corte:</strong> {lote.corte}</p>)}
                    </CardContent>
                  </Card>
                </li>
              ))}
            </ul>
          ) : (
            <p className="mt-4 text-gray-500">No hay lotes para mostrar</p>
          )}
        </div>

        <div className="space-y-6">
          <h2 className="text-xl font-semibold mb-4">Formulario de Control</h2>
          <form onSubmit={handleSubmit} className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Valores Esperados vs. Obtenidos</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {[
                    { label: 'Cantidad Enteros', expected: valoresEsperados?.cantidadEnterosEsperada, name: 'cantidadEnterosObtenida', value:controlData.cantidadEnterosObtenida },
                    { label: 'Cantidad Medios', expected: valoresEsperados?.CantidadMediosEsperada, name: 'cantidadMediosObtenida', value:controlData.cantidadMediosObtenida },
                    { label: 'Cantidad Cuartos', expected: valoresEsperados?.cantidadCuartosEsperada, name: 'cantidadCuartosObtenida', value:controlData.cantidadCuartosObtenida },
                  ].map((item, index) => (
                    <div key={index} className="flex flex-col sm:flex-row justify-between items-start sm:items-center space-y-2 sm:space-y-0">
                      <label className="text-sm font-medium">{item.label}:</label>
                      <div className="flex items-center space-x-2">
                        <span className="text-sm">{formatter.formatNumber(item.expected)}</span>
                        <Input
                        type="number"
                        name={item.name}
                        value={item.value}
                        onChange={handleInputChange}
                        className="w-24"
                      />
                      </div>
                    </div>
                  ))}
                  {valoresEsperados?.cantidadesInsumos.map((insumo, index) => (
                    <div key={index} className="flex flex-col sm:flex-row justify-between items-start sm:items-center space-y-2 sm:space-y-0">
                      <label className="text-sm font-medium">{insumo.insumo}:</label>
                      <div className="flex items-center space-x-2">
                        <span className="text-sm">{formatter.formatNumber(insumo.cantidad, true)}</span>
                        <Input
                          type="number"
                          value={controlData.cantidadesInsumos.find(i => i.insumo === insumo.insumo)?.cantidad || ''}
                          onChange={(e) => handleInsumoChange(insumo.insumo, parseFloat(e.target.value))}
                          className="w-24"
                        />
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
            <div className="space-y-2">
              <label htmlFor="observaciones" className="block text-sm font-medium text-gray-700">
                Observaciones
              </label>
              <Textarea
                id="observaciones"
                name="observaciones"
                value={controlData.observaciones}
                onChange={handleInputChange}
                rows={3}
                className="w-full"
              />
            </div>
            <div className='flex justify-center'>
              <Button type="submit" className='w-full sm:w-auto bg-green-500 hover:bg-green-600 text-white transition-colors inline-block font-semibold'>
                Enviar Control
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}