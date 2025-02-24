'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { Item } from '@/interfaces/item'
import { TipoQueso } from '@/interfaces/tipoQueso'
import { Formula } from '@/interfaces/formula'
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { Button } from "@/components/ui/button"
import { DetalleFormula } from '@/interfaces/detalleFormula'

interface DetalleRequest {
  insumo_id: number,
  cantidad: number
}

export default function FormulaForm({ codigo }: { codigo: string }) {
  const router = useRouter()
  const [formula, setFormula] = useState<Formula>({
    codigo: '',
    cantidad_leche: 0,
    queso: {
      id_tipo_queso: 0,
      item: {
        id: 0,
        nombre: '',
        unidad_medida: 'g',
      },
      dias_maduracion: 0
    },
    detalles: [{
      id_detalle: 0,
      insumo: {
        id: 0,
        nombre: '',
        unidad_medida: 'g'
      },
      cantidad: 0
    },]
  })
  const [tiposQueso, setTiposQueso] = useState<TipoQueso[]>([])
  const [tipoQueso, setTipoQueso] = useState<TipoQueso>({
    id_tipo_queso: 0,
    item: {
      id: 0,
      nombre: '',
      unidad_medida: 'g',
    },
    dias_maduracion: 0
  });
  const [insumos, setInsumos] = useState<Item[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const isNew = codigo === 'nuevo'


  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
        router.push('/')
      }
    }
  }, [router])

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [tiposQuesoRes, insumosRes] = await Promise.all([
          fetch('http://localhost:8082/productos'),
          fetch('http://localhost:8082/insumos')
        ])

        if (!tiposQuesoRes.ok || !insumosRes.ok) {
          throw new Error('Error al cargar los datos')
        }

        const tiposQuesoData = await tiposQuesoRes.json()
        const insumosData = await insumosRes.json()

        setTiposQueso(tiposQuesoData)
        setInsumos(insumosData)
        
        
        if (!isNew) {
          const formulaRes = await fetch(`http://localhost:8082/formulas/${codigo}`)
          if (!formulaRes.ok) {
            const errorData = await formulaRes.json()
            throw new Error(errorData.message || 'Error al cargar la fórmula')
          }
          const formulaData = await formulaRes.json()
          console.log(formulaData);
          
          setTipoQueso(formulaData.queso)
          setFormula({
            ...formulaData,
            detalles: formulaData.detalles.map((detalle: DetalleFormula) => ({
              insumo: {
                id: detalle.insumo.id,
                nombre: detalle.insumo.nombre,
                unidad_medida: detalle.insumo.unidad_medida
              },
              cantidad: detalle.cantidad
            })) || []
          })
        }
      } catch (error) {
        setError(error instanceof Error ? error.message : 'Error al cargar los datos. Por favor, intente de nuevo más tarde.')
      } finally {
        setIsLoading(false)
      }
    }

    fetchData()
    
  }, [isNew, codigo])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)

    try {
      const detallesToSend: DetalleRequest[] = []
      formula.detalles.forEach(detalle => {
        detallesToSend.push({
          insumo_id: detalle.insumo.id,
          cantidad: detalle.cantidad
        })
      })

      const formulaToSend = {
        codigo: formula.codigo,
        cantidad_leche: formula.cantidad_leche,
        id_tipo_queso: formula.queso.id_tipo_queso,
        detalles: detallesToSend,
      }
      
      
      const url = isNew ? 'http://localhost:8082/formulas' : `http://localhost:8082/formulas/${codigo}`
      const method = isNew ? 'POST' : 'PUT'

      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formulaToSend),
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al guardar la fórmula')
      }

      router.push('/formulas')
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al guardar la fórmula. Por favor, intente de nuevo más tarde.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleTipoQuesoChange = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    const id = parseInt(e.target.value)
    if (id === 0) {
      setFormula(prev => ({ 
        ...prev, 
        queso: { id_tipo_queso: id, item: { id: 0, nombre: '', unidad_medida: 'kg' }, dias_maduracion: 0 },
        codigo: '' 
      }))
    } else {
      const selectedTipoQueso = tiposQueso.find(t => t.id_tipo_queso === id)
      if (selectedTipoQueso) {
        setFormula(prev => ({ 
          ...prev, 
          queso: selectedTipoQueso 
        }))
        if (isNew) {
          try {
            const response = await fetch(`http://localhost:8082/formulas/listarBy/${id}`)
            if (!response.ok) {
              const errorData = await response.json()
              throw new Error(errorData.message || 'Error al obtener las fórmulas del tipo de queso')
            }
            const formulas: Formula[] = await response.json()
            const codigoBase = `Q${selectedTipoQueso.item.nombre.charAt(0).toUpperCase()}`
            const numeroFormula = (formulas.length + 1).toString().padStart(3, '0')
            setFormula(prev => ({ 
              ...prev, 
              codigo: `${codigoBase}${numeroFormula}` 
            }))
          } catch (error) {
            setError(error instanceof Error ? error.message : 'Error al generar el código de la fórmula')
          }
        }
      }
    }
  }

  const addDetalle = () => {
    setFormula(prev => ({
      ...prev,
      detalles: [
        ...prev.detalles, 
        { 
          insumo: {
            id: 0,
            nombre: '',
            unidad_medida: 'g'
          },
          cantidad: 0 
        }
      ]
    }))
  }

  const removeDetalle = (index: number) => {
    setFormula(prev => ({
      ...prev,
      detalles: prev.detalles.filter((_, i) => i !== index)
    }))
  }

  const updateDetalle = (index: number, field: 'insumo' | 'cantidad', value: number | Item) => {
    setFormula(prev => ({
      ...prev,
      detalles: prev.detalles.map((detalle, i) => 
        i === index 
          ? { 
              ...detalle, 
              [field]: field === 'insumo' 
                ? { ...detalle.insumo, id: (value as Item).id } 
                : value 
            } 
          : detalle
      )
    }))
  }

  const getAvailableInsumos = (currentIndex: number) => {
    const usedInsumoIds = formula.detalles
      .filter((_, index) => index !== currentIndex)
      .map(detalle => detalle.insumo.id)
    return insumos.filter(insumo => !usedInsumoIds.includes(insumo.id))
  }

  const isFormValid = () => {
    return (
      formula.codigo !== '' &&
      formula.cantidad_leche > 0 &&
      formula.queso.id_tipo_queso !== 0 &&
      formula.detalles.length > 0 &&
      formula.detalles.every(detalle => detalle.insumo.id !== 0 && detalle.cantidad > 0)
    )
  }

  if (isLoading) {
    return <div className="text-center py-4">Cargando...</div>
  }

  return (
    <div className="bg-white shadow-md rounded-lg p-6 max-w-2xl mx-auto" style={{marginTop: '50px'}}>
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">{isNew ? 'Nueva Fórmula' : 'Editar Fórmula'}</h1>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>
            {error} 
            <Button onClick={() => setError(null)} variant="secondary">
              Ok
            </Button>
          </AlertDescription>
        </Alert>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label htmlFor="codigo" className="block text-sm font-medium text-gray-700 mb-1">
            Código:
          </label>
          <input
            type="text"
            id="codigo"
            value={formula.codigo}
            readOnly
            className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-100"
          />
        </div>
        <div>
          <label htmlFor="cantidad_leche" className="block text-sm font-medium text-gray-700 mb-1">
            Cantidad de Leche:
          </label>
          <input
            type="number"
            id="cantidad_leche"
            value={formula.cantidad_leche}
            onChange={(e) => setFormula({ ...formula, cantidad_leche: parseFloat(e.target.value) })}
            required
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <div>
          <label htmlFor="id_tipo_queso" className="block text-sm font-medium text-gray-700 mb-1">
            Tipo de Queso:
          </label>
          {isNew ? (
            <select
              id="id_tipo_queso"
              value={formula.queso.id_tipo_queso}
              onChange={handleTipoQuesoChange}
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="0">Seleccione un tipo de queso</option>
              {tiposQueso.map((tipo) => (
                <option key={tipo.id_tipo_queso} value={tipo.id_tipo_queso}>
                  {tipo.item.nombre}
                </option>
              ))}
            </select>
          ) : (
            <input
              type="text"
              value={tipoQueso.item.nombre}
              placeholder={tipoQueso.item.nombre}
              readOnly
              className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-100"
            />
          )}
        </div>
        <div>
          <h2 className="text-lg font-semibold mb-2">Detalles de la Fórmula</h2>
          {formula.detalles.map((detalle, index) => (
            <div key={index} className="flex space-x-2 mb-2 items-end">
              <div className="flex-grow">
                <label htmlFor={`insumo_${index}`} className="block text-sm font-medium text-gray-700 mb-1">
                  Insumo:
                </label>
                <select
                  id={`insumo_${index}`}
                  value={detalle.insumo.id}
                  onChange={(e) => {
                    const selectedInsumo = insumos.find(insumo => insumo.id === parseInt(e.target.value));
                    if (selectedInsumo) {
                      updateDetalle(index, 'insumo', selectedInsumo);
                    }
                  }}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="0">Seleccione un insumo</option>
                  {getAvailableInsumos(index).map((insumo) => (
                    <option key={insumo.id} value={insumo.id}>
                      {insumo.nombre} ({insumo.unidad_medida})
                    </option>
                  ))}
                </select>
              </div>
              <div className="w-24">
                <label htmlFor={`cantidad_${index}`} className="block text-sm font-medium text-gray-700 mb-1">
                  Cantidad:
                </label>
                <input
                  type="number"
                  id={`cantidad_${index}`}
                  value={detalle.cantidad}
                  onChange={(e) => updateDetalle(index, 'cantidad', parseFloat(e.target.value))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <button
                type="button"
                onClick={() => removeDetalle(index)}
                className="bg-red-500 hover:bg-red-600 text-white px-3 py-2 rounded-md transition-colors"
              >
                Eliminar
              </button>
            </div>
          ))}
          <button
            type="button"
            onClick={addDetalle}
            className="mt-2 bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-md transition-colors"
          >
            Agregar Detalle
          </button>
        </div>
        <div className="flex justify-between items-center">
          <button
            type="button"
            onClick={() => router.push('/formulas')}
            className="bg-yellow-500 hover:bg-yellow-600 text-black px-4 py-2 rounded-md transition-colors"
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={isLoading || !isFormValid()}
            className="bg-green-500 hover:bg-green-600 text-white px-6 py-2 rounded-md transition-colors font-semibold disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isLoading ? 'Guardando...' : 'Guardar'}
          </button>
        </div>
      </form>
    </div>
  )
}