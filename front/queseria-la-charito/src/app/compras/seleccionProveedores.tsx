'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import { Item } from '@/interfaces/item'
import Proveedor from '@/interfaces/proveedor'
import { NumberFormatter } from '@/utils/numberFormatter'

interface SelectedProveedor extends Proveedor {
  selectedQuantity: number
  subtotal: number
}

export default function SeleccionProveedoresPage() {
  const [insumos, setInsumos] = useState<Item[]>([])
  const [selectedInsumo, setSelectedInsumo] = useState<string>('')
  const [proveedores, setProveedores] = useState<Proveedor[]>([])
  const [selectedProveedores, setSelectedProveedores] = useState<SelectedProveedor[]>([])
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
        fetchInsumos()
      }
    }
  }, [router])

  useEffect(() => {
    if (selectedInsumo) {
      fetchProveedores(selectedInsumo)
    }
  }, [selectedInsumo])

  const fetchInsumos = async () => {
    try {
      const response = await fetch('http://localhost:8082/insumos')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los insumos')
      }
      const data = await response.json()
      setInsumos(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los insumos. Por favor, intente de nuevo más tarde.')
    }
  }

  const fetchProveedores = async (insumoId: string) => {
    try {
      const response = await fetch(`http://localhost:8082/proveedores/insumos/${insumoId}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los proveedores')
      }
      const data = await response.json()
      setProveedores(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los proveedores. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleSelectProveedor = (proveedor: Proveedor) => {
    const existingProveedor = selectedProveedores.find(p => p.id === proveedor.id)
    if (existingProveedor) {
      setError('Este proveedor ya ha sido seleccionado.')
      return
    }
    setSelectedProveedores([...selectedProveedores, {
      ...proveedor,
      selectedQuantity: proveedor.cantidadMedida,
      subtotal: proveedor.cantidadMedida * (proveedor.costo/proveedor.cantidadMedida)
    }])
  }

  const handleUpdateQuantity = (id: number, newQuantity: number) => {
    setSelectedProveedores(prevProveedores => 
      prevProveedores.map(p => 
        p.id === id 
          ? { ...p, selectedQuantity: newQuantity, subtotal: newQuantity * (p.costo/p.cantidadMedida) }
          : p
      )
    )
  }

  const handleRemoveProveedor = (id: number) => {
    setSelectedProveedores(prevProveedores => prevProveedores.filter(p => p.id !== id))
  }

  const handleProceedToPayment = () => {
    const purchaseData = {
      fecha: new Date().toISOString(),
      total: selectedProveedores.reduce((sum, p) => sum + p.subtotal, 0),
      listDetalles: selectedProveedores.map(p => ({
        idProveedor: p.id,
        cantidad: p.selectedQuantity,
        subtotal: p.subtotal
      }))
    }
    
    router.push(`/compras/pago?data=${encodeURIComponent(JSON.stringify(purchaseData))}`)
  }

  const totalAmount = selectedProveedores.reduce((sum, p) => sum + p.subtotal, 0)

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Compra de Insumos</h1>
      
      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <h2 className="text-xl font-semibold mb-4">Seleccionar Insumo y Proveedor</h2>
          <select
            className="w-full p-2 border border-gray-300 rounded-md mb-4"
            value={selectedInsumo}
            onChange={(e) => setSelectedInsumo(e.target.value)}
          >
            <option value="" disabled>Seleccione un insumo</option>
            {insumos.map((insumo) => (
              <option key={insumo.id} value={insumo.id.toString()}>
                {insumo.nombre}
              </option>
            ))}
          </select>


          {proveedores.map((proveedor) => (
            <Card key={proveedor.id} className="mb-4">
              <CardHeader>
                <CardTitle>{proveedor.nombre}</CardTitle>
              </CardHeader>
              <CardContent>
                <p>{formatter.formatNumber(proveedor.cantidadMedida, true)} {proveedor.unidadMedida.toUpperCase()}</p>
                <p>Costo: ${formatter.formatCurrency(proveedor.costo)}</p>
                <Button onClick={() => handleSelectProveedor(proveedor)} className="mt-2 bg-blue-500 hover:bg-blue-600">
                  Seleccionar
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>

        <div>
          <h2 className="text-xl font-semibold mb-4">Proveedores Seleccionados</h2>
          {selectedProveedores.map((proveedor) => (
            <Card key={proveedor.id} className="mb-4">
              <CardHeader>
                <CardTitle>{proveedor.nombre}</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex items-center space-x-2 mb-2">
                  <Input
                    type="number"
                    value={proveedor.selectedQuantity}
                    onChange={(e) => handleUpdateQuantity(proveedor.id, parseFloat(e.target.value))}
                    min={proveedor.cantidadMedida}
                    step={proveedor.cantidadMedida}
                  />
                  <span>{proveedor.unidadMedida.toUpperCase()}</span>
                </div>
                <p>Insumo: {proveedor.insumo.nombre}</p>
                <p>Subtotal: ${formatter.formatCurrency(proveedor.subtotal)}</p>
                <Button variant="destructive" onClick={() => handleRemoveProveedor(proveedor.id)} className="mt-2">
                  Eliminar
                </Button>
              </CardContent>
            </Card>
          ))}

          <div className="mt-6">
            <h3 className="text-lg font-semibold">Total: ${formatter.formatCurrency(totalAmount)}</h3>
            <Button onClick={handleProceedToPayment} className="mt-4 bg-green-500 hover:bg-green-600" disabled={selectedProveedores.length === 0}>
              Proceder al Pago
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}