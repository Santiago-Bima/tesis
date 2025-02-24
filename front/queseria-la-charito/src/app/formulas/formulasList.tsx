'use client'

import { useState, useEffect } from 'react'
import Link from 'next/link'
import { ChevronDown, ChevronUp } from 'lucide-react'
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { TipoQueso } from '@/interfaces/tipoQueso'
import { Formula } from '@/interfaces/formula'
import { useRouter } from 'next/navigation'
import { NumberFormatter } from '@/utils/numberFormatter'
import { DialogFooter, DialogHeader, Dialog, DialogContent, DialogDescription, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'

export default function FormulasList() {
  const [productos, setProductos] = useState<TipoQueso[]>([])
  const [formulas, setFormulas] = useState<Formula[]>([])
  const [selectedProductId, setSelectedProductId] = useState<number | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [expandedFormula, setExpandedFormula] = useState<string | null>(null)
  const router = useRouter();
  const formatter = new NumberFormatter('es-ES');
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [formulaToDelete, setFormulaToDelete] = useState<string | null>(null)


  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
          if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
            router.push('/')
          }
    }

    fetchProductos()
  }, [router])

  useEffect(() => {
    if (selectedProductId) {
      fetchFormulas(selectedProductId)
    }
  }, [selectedProductId])

  const fetchProductos = async () => {
    setIsLoading(true)
    setError(null)
    try {
      const response = await fetch('http://localhost:8082/productos')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los productos')
      }
      const data = await response.json()
      setProductos(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los productos. Por favor, intente de nuevo más tarde.')
    } finally {
      setIsLoading(false)
    }
  }

  const fetchFormulas = async (productId: number) => {
    setIsLoading(true)
    setError(null)
    try {
      const response = await fetch(`http://localhost:8082/formulas/listarBy/${productId}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar las fórmulas')
      }
      const data = await response.json()
      setFormulas(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar las fórmulas. Por favor, intente de nuevo más tarde.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleProductChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const productId = parseInt(event.target.value)
    setSelectedProductId(productId)
  }

  const eliminarFormula = async () => {
    if (formulaToDelete == null) return;

    try {
      const response = await fetch(`http://localhost:8082/formulas/${formulaToDelete}`, {
        method: 'DELETE',
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al eliminar la fórmula')
      }
      if (selectedProductId) {
        fetchFormulas(selectedProductId)
        setFormulaToDelete(null)
        setIsDeleteDialogOpen(false)
      }
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al eliminar la fórmula. Por favor, intente de nuevo más tarde.')
    }
  }

  const toggleFormulaExpansion = (codigo: string) => {
    setExpandedFormula(expandedFormula === codigo ? null : codigo)
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Lista de Fórmulas</h1>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>
            {error} 
          </AlertDescription>
        </Alert>
      )}

      <div className="mb-6">
        <label htmlFor="producto" className="block text-sm font-medium text-gray-700 mb-1">
          Seleccionar Producto:
        </label>
        <select
          id="producto"
          onChange={handleProductChange}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option value="">Seleccione un producto</option>
          {productos.map((producto) => (
            <option key={producto.id_tipo_queso} value={producto.id_tipo_queso}>
              {producto.item.nombre}
            </option>
          ))}
        </select>
      </div>

      <div className="mt-8 text-center mb-8">
        <Link
          href="/formulas/nuevo"
          className="bg-green-500 hover:bg-green-600 text-white px-6 py-3 rounded-md transition-colors inline-block font-semibold"
        >
          Agregar Nueva Fórmula
        </Link>
      </div>

      {isLoading ? (
        <div className="text-center py-4">Cargando...</div>
      ) : selectedProductId ? (
        formulas.length > 0 ? (
          <ul className="space-y-4">
            {formulas.map((formula) => (
              <li key={formula.codigo} className="bg-gray-50 rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow">
                <div className="flex justify-between items-center">
                  <div>
                    <span className="font-semibold text-lg text-gray-800">{formula.codigo}</span>
                    <span className="ml-2 text-sm text-gray-500">
                      (Leche: {formatter.formatNumber(formula.cantidad_leche, true)} ml)
                    </span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => toggleFormulaExpansion(formula.codigo)}
                      className="text-indigo-600 hover:text-indigo-800 transition-colors"
                    >
                      {expandedFormula === formula.codigo ? (
                        <ChevronUp className="h-6 w-6" />
                      ) : (
                        <ChevronDown className="h-6 w-6" />
                      )}
                    </button>
                    <Link
                      href={`/formulas/${formula.codigo}`}
                      className="bg-yellow-500 hover:bg-yellow-600 text-black px-4 py-2 rounded-md transition-colors"
                    >
                      Editar
                    </Link>
                    <button
                      onClick={() => {
                        setFormulaToDelete(formula.codigo)
                        setIsDeleteDialogOpen(true)
                    }}
                      className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-md transition-colors"
                    >
                      Eliminar
                    </button>
                  </div>
                </div>
                {expandedFormula === formula.codigo && (
                  <div className="mt-4 pl-4 border-l-2 border-gray-300">
                    <h3 className="font-semibold mb-2">Insumos:</h3>
                    {formula.detalles && formula.detalles.length > 0 ? (
                      <ul className="space-y-2">
                        {formula.detalles.map((detalle) => (
                          <li key={detalle.id_detalle} className="text-sm text-gray-600">
                            {detalle.insumo.nombre}: {formatter.formatNumber(detalle.cantidad, true)} {detalle.insumo.unidad_medida}
                          </li>
                        ))}
                      </ul>
                    ) : (
                      <p className="text-sm text-gray-500">No hay detalles disponibles para esta fórmula.</p>
                    )}
                  </div>
                )}
              </li>
            ))}
          </ul>
        ) : (
          <div className="text-center py-4 text-gray-500">No hay fórmulas disponibles para este producto.</div>
        )
      ) : (
        <div className="text-center py-4 text-gray-500">Seleccione un producto para ver sus fórmulas.</div>
      )}

    <Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirmar eliminación</DialogTitle>
            <DialogDescription>
              ¿Está seguro de que desea eliminar este formula? Esta acción no se puede deshacer.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)} className="bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors inline-block font-semibold">Cancelar</Button>
            <Button variant="destructive" onClick={eliminarFormula} className="bg-red-500 hover:bg-red-600 text-white rounded-md transition-colors inline-block font-semibold">Eliminar</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
      
    </div>
  )
}