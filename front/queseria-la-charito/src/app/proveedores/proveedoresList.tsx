'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import Proveedor from '@/interfaces/proveedor'
import { Item } from '@/interfaces/item'
import { NumberFormatter } from '@/utils/numberFormatter'
import { DialogFooter, DialogHeader, Dialog, DialogContent, DialogDescription, DialogTitle } from '@/components/ui/dialog'

export default function ProveedoresList() {
  const [insumos, setInsumos] = useState<Item[]>([])
  const [selectedInsumo, setSelectedInsumo] = useState<number | null>(null)
  const [proveedores, setProveedores] = useState<Proveedor[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const router = useRouter()
  const formatter = new NumberFormatter('es-ES');
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [proveedorToDelete, setProveedorToDelete] = useState<number | null>(null)

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


  useEffect(() => {
    if (selectedInsumo) {
      fetchProveedores(selectedInsumo)
    }
  }, [selectedInsumo])

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
      setError(error instanceof Error ? error.message : 'Error al cargar los insumos. Por favor, intente de nuevo más tarde.')
    } finally {
      setIsLoading(false)
    }
  }

  const fetchProveedores = async (insumoId: number) => {
    setIsLoading(true)
    setError(null)
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
    } finally {
      setIsLoading(false)
    }
  }

  const handleDeleteProveedor = async () => {
    if (proveedorToDelete == null) return;
    try {
      const response = await fetch(`http://localhost:8082/proveedores/${proveedorToDelete}`, {
        method: 'DELETE',
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al eliminar el proveedor')
      }
      setProveedores(proveedores.filter(proveedor => proveedor.id !== proveedorToDelete))
      setIsDeleteDialogOpen(false)
      setProveedorToDelete(null)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al eliminar el proveedor. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleInsumoChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const itemId = parseInt(event.target.value)
    setSelectedInsumo(itemId)
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Lista de Proveedores</h1>
      
      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <div className="flex flex-row w-full gap-4 justify-between mb-9">
        <div className="flex flex-row w-3/4 gap-4">
            <select
              value={selectedInsumo?.toString() || ''}
              onChange={handleInsumoChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="" disabled>Seleccione un insumo</option>
              {insumos.map((insumo) => (
                <option key={insumo.id} value={insumo.id.toString()}>
                  {insumo.nombre}
                </option>
              ))}
            </select>
        </div>
        <div className="flex items-center">
          <Button onClick={() => router.push('/proveedores/nuevo')} className="bg-green-500 hover:bg-green-600 text-white rounded-md transition-colors inline-block font-semibold">
            Crear Nuevo Proveedor
          </Button>
        </div>
      </div>

        
      { isLoading ? (
        <div className="text-center py-4">Cargando...</div>
      ) : proveedores.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {proveedores.map((proveedor) => (
            <Card key={proveedor.id}>
              <CardHeader>
                <CardTitle>{proveedor.nombre}</CardTitle>
              </CardHeader>
              <CardContent>
                {proveedor.email && <p><strong>Email:</strong> {proveedor.email}</p>}
                {proveedor.alias && <p><strong>Alias/CBU:</strong> {proveedor.alias}</p>}
                {proveedor.cuit && <p><strong>CUIT/CUIL:</strong> {proveedor.cuit}</p>}
                {proveedor.banco && <p><strong>Banco:</strong> {proveedor.banco}</p>}
                {proveedor.tipoCuenta && <p><strong>Tipo de Cuenta:</strong> {proveedor.tipoCuenta}</p>}
                {proveedor.telefono !== 0 && <p><strong>Teléfono:</strong> {proveedor.telefono}</p>}
                <p><strong>Insumo:</strong> {proveedor.insumo.nombre}</p>
                <p><strong>Unidad de medida:</strong> {proveedor.unidadMedida.toUpperCase()}</p>
                <p><strong>Cantidad:</strong> {formatter.formatNumber(proveedor.cantidadMedida,true)}</p>
                {proveedor.costo !== 0 && <p><strong>Costo:</strong> ${formatter.formatCurrency(proveedor.costo)}</p>}
                <div className="flex justify-end space-x-2 mt-4">
                  <Button variant="outline" className="bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors inline-block font-semibold" onClick={() => router.push(`/proveedores/${proveedor.id}`)}>
                    Editar
                  </Button>
                  <Button variant="destructive" className="bg-red-500 hover:bg-red-600 text-white rounded-md transition-colors inline-block font-semibold" onClick={() => {
                      setProveedorToDelete(proveedor.id)
                      setIsDeleteDialogOpen(true)
                  }}>
                    Eliminar
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <div className="text-center py-4 text-gray-500">No hay lotes disponibles para el item y estado seleccionados.</div>
      )}

      <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirmar eliminación</DialogTitle>
            <DialogDescription>
              ¿Está seguro de que desea eliminar este proveedor? Esta acción no se puede deshacer.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)} className="bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors inline-block font-semibold">Cancelar</Button>
            <Button variant="destructive" onClick={handleDeleteProveedor} className="bg-red-500 hover:bg-red-600 text-white rounded-md transition-colors inline-block font-semibold">Eliminar</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
      
    </div>
  )
}