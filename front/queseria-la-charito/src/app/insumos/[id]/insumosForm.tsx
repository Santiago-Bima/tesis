'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { Item } from '../../../interfaces/item'

export default function InsumosForm({ id }: { id: string }) {
  const router = useRouter()
  const [insumo, setInsumo] = useState<Item>({ id: 0, nombre: '', unidad_medida: 'ml' })
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const isNew = id === 'nuevo'

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
          if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
            router.push('/')
          }
    }

    if (!isNew) {
      fetchInsumo()
    }
  }, [router, isNew, id])

  const fetchInsumo = async () => {
    try {
      const response = await fetch(`http://localhost:8082/insumos/${id}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar el insumo')
      }
      const data = await response.json()
      setInsumo(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar el insumo. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)

    try {
      const url = isNew ? 'http://localhost:8082/insumos' : `http://localhost:8082/insumos/${id}`
      const method = isNew ? 'POST' : 'PUT'

      const insumoData = {
        nombre: insumo.nombre,
        unidad_medida: insumo.unidad_medida
      }

      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(insumoData),
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al guardar el insumo')
      }

      router.push('/insumos')
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al guardar el insumo. Por favor, intente de nuevo más tarde.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="bg-white shadow-md rounded-lg p-6 max-w-md mx-auto" style={{marginTop: '50px'}}>
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">{isNew ? 'Nuevo Insumo' : 'Editar Insumo'}</h1>
      {error && <div className="text-red-500 mb-4 p-2 bg-red-100 border border-red-400 rounded">{error}</div>}

      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label htmlFor="nombre" className="block text-sm font-medium text-gray-700 mb-1">
            Nombre:
          </label>
          <input
            type="text"
            id="nombre"
            value={insumo.nombre}
            onChange={e => setInsumo({ ...insumo, nombre: e.target.value })}
            required
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <div>
          <label htmlFor="unidadMedida" className="block text-sm font-medium text-gray-700 mb-1">
            Unidad de Medida:
          </label>
          <select
            id="unidadMedida"
            value={insumo.unidad_medida}
            onChange={e => setInsumo({ ...insumo, unidad_medida: e.target.value as 'ml' | 'l' | 'g' | 'kg' })}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            {['ml', 'l', 'g', 'kg'].map(unidad => (
              <option key={unidad} value={unidad}>
                {unidad}
              </option>
            ))}
          </select>
        </div>
        <div className="flex justify-between items-center">
          <button
            type="button"
            onClick={() => router.push('/insumos')}
            className="bg-yellow-500 hover:bg-yellow-600 text-black px-4 py-2 rounded-md transition-colors"
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={isLoading}
            className="bg-green-500 hover:bg-green-600 text-white px-6 py-2 rounded-md transition-colors font-semibold disabled:opacity-50"
          >
            {isLoading ? 'Guardando...' : 'Guardar'}
          </button>
        </div>
      </form>
    </div>
  )
}