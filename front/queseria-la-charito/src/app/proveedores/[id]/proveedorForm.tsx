'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import { Item } from '@/interfaces/item'


export default function ProveedorForm({ id }: { id: string }) {
  const [proveedor, setProveedor] = useState({
    nombre: '',
    email: '',
    alias: '',
    cuit: '',
    banco: '',
    tipoCuenta: '',
    telefono: 0,
    idInsumo: 0,
    cantidadMedida: 0,
    unidadMedida: '',
    costo: 0
  })
  const [insumos, setInsumos] = useState<Item[]>([])
  const [error, setError] = useState<string | null>(null)
  const [isBancoRequired, setIsBancoRequired] = useState(false)
  const [isTipoCuentaRequired, setIsTipoCuentaRequired] = useState(false)
  const router = useRouter()
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

    fetchInsumos()

    fetchInsumos()
    if (!isNew) {
      fetchProveedor(id)
    }
  }, [isNew, id, router])

  useEffect(() => {
    setIsBancoRequired(!!proveedor.tipoCuenta)
    setIsTipoCuentaRequired(!!proveedor.banco)
  }, [proveedor.banco, proveedor.tipoCuenta])

  const fetchInsumos = async () => {
    try {
      const response = await fetch('http://localhost:8082/insumos')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los insumos')
      }
      const data = await response.json()
      setInsumos(data.map((insumo: Item) => ({
        ...insumo,
        id: insumo.id.toString()
      })))
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los insumos. Por favor, intente de nuevo más tarde.')
    }
  }

  const fetchProveedor = async (providerId: string) => {
    try {
      const response = await fetch(`http://localhost:8082/proveedores/${providerId}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar el proveedor')
      }
      const data = await response.json()
      setProveedor({
        ...data,
        idInsumo: data.insumo.id.toString()
      })
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar el proveedor. Por favor, intente de nuevo más tarde.')
    }
  }

  const getUnidadMedidaOptions = () => {
    const insumo: Item | undefined = insumos.find(item => { return item.id == proveedor.idInsumo; })
    
    if (insumo === undefined) return [''];

    const baseUnit: string = insumo.unidad_medida;

    if (baseUnit === 'g' || baseUnit === 'kg') return ['g', 'kg']
    if (baseUnit === 'ml' || baseUnit === 'l') return ['ml', 'l']
    return [baseUnit]
  }

  const handleInsumoChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const itemId = parseInt(event.target.value)
    setProveedor(prev => ({
      ...prev,
      idInsumo: itemId
    }))
  }

  const handleTipoCuentaChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const tipoCuenta = event.target.value
    setProveedor(prev => ({
      ...prev,
      tipoCuenta: tipoCuenta
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if ((proveedor.banco && !proveedor.tipoCuenta) || (!proveedor.banco && proveedor.tipoCuenta)) {
      setError('Si se proporciona el banco, el tipo de cuenta es obligatorio y viceversa.')
      return
    }
    try {
      const url = !isNew ? `http://localhost:8082/proveedores/${id}` : 'http://localhost:8082/proveedores'
      const method = !isNew ? 'PUT' : 'POST'
      
      
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          ...proveedor,
          telefono: Number(proveedor.telefono),
          costo: Number(proveedor.costo),
          idInsumo: proveedor.idInsumo ? Number(proveedor.idInsumo) : undefined
        }),
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al guardar los vehículos')
      }
      router.push('/proveedores')
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al guardar el proveedor. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setProveedor(prev => ({
      ...prev,
      [name]: name === 'telefono' || name === 'costo' ? Number(value) : value
    }))

    if (name === 'idInsumo') {
      const selectedInsumo = insumos.find(insumo => insumo.id === parseInt(value))
      if (selectedInsumo) {
        setProveedor(prev => ({
          ...prev,
          idInsumo: parseInt(value),
          unidadMedida: selectedInsumo.unidad_medida
        }))
      }
    }
  }

  return (
    <>
      <div className="container mx-auto px-4 py-8 flex flex-col items-center">
        <h1 className="text-3xl font-bold mb-6 text-indigo-700">Formulario de Proveedores</h1>

        {error && (
          <Alert variant="destructive" className="mb-6">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        <form onSubmit={handleSubmit} className="w-full max-w-3xl bg-white shadow-md rounded-lg p-8 border border-gray-200">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label htmlFor="nombre" className="block text-sm font-medium text-gray-700">Nombre</label>
              <Input
                type="text"
                id="nombre"
                name="nombre"
                value={proveedor.nombre}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700">Email</label>
              <Input
                type="email"
                id="email"
                name="email"
                value={proveedor.email}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label htmlFor="alias" className="block text-sm font-medium text-gray-700">Alias/CBU</label>
              <Input
                type="text"
                id="alias"
                name="alias"
                value={proveedor.alias}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label htmlFor="cuit" className="block text-sm font-medium text-gray-700">CUIT/CUIL</label>
              <Input
                type="text"
                id="cuit"
                name="cuit"
                value={proveedor.cuit}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label htmlFor="banco" className="block text-sm font-medium text-gray-700">
                Banco {isBancoRequired && <span className="text-red-500">*</span>}
              </label>
              <Input
                type="text"
                id="banco"
                name="banco"
                value={proveedor.banco}
                onChange={handleChange}
                required={isBancoRequired}
              />
            </div>
            <div>
              <label htmlFor="tipoCuenta" className="block text-sm font-medium text-gray-700">Tipo de Cuenta</label>
              <select
                id="tipoCuenta"
                name="tipoCuenta"
                value={proveedor.tipoCuenta}
                onChange={handleTipoCuentaChange}
                required={isTipoCuentaRequired}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                <option value="" disabled>Seleccione un tipo de cuenta</option>
                <option value="CuentaCorriente">Cuenta Corriente</option>
                <option value="CajaDeAhorro">Caja de Ahorro</option>
              </select>
            </div>
            <div>
              <label htmlFor="telefono" className="block text-sm font-medium text-gray-700">Teléfono</label>
              <Input
                type="number"
                id="telefono"
                name="telefono"
                value={proveedor.telefono || ''}
                onChange={handleChange}
              />
            </div>
            <div>
              <label htmlFor="idInsumo" className="block text-sm font-medium text-gray-700">Insumo</label>
              <select
                id="idInsumo"
                name="idInsumo"
                value={proveedor.idInsumo || ''}
                onChange={handleInsumoChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                required
              >
                <option value="" disabled>Seleccione un insumo</option>
                {insumos.map((insumo) => (
                  <option key={insumo.id} value={insumo.id}>
                    {insumo.nombre}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label htmlFor="unidadMedida" className="block text-sm font-medium text-gray-700 mb-1">
                Unidad de Medida:
              </label>
              <select
                id="unidadMedida"
                value={proveedor.unidadMedida}
                onChange={e => setProveedor({ ...proveedor, unidadMedida: e.target.value as 'ml' | 'l' | 'g' | 'kg' })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                <option value="" disabled>Seleccione una unidad de medida</option>
                {getUnidadMedidaOptions().map((unidad) => (
                  <option key={unidad} value={unidad}>
                    {unidad}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label htmlFor="cantidadMedida" className="block text-sm font-medium text-gray-700">Cantidad</label>
              <Input
                type="number"
                id="cantidadMedida"
                name="cantidadMedida"
                value={proveedor.cantidadMedida || ''}
                onChange={handleChange}
                step="0.01"
                required
              />
            </div>
            <div>
              <label htmlFor="costo" className="block text-sm font-medium text-gray-700">Costo</label>
              <Input
                type="number"
                id="costo"
                name="costo"
                value={proveedor.costo || ''}
                onChange={handleChange}
                step="0.01"
                required
              />
            </div>
          </div>
          <div className="flex justify-end space-x-2 mt-6">
            <Button type="button" className='bg-yellow-500 hover:bg-yellow-600 text-black' variant="secondary" onClick={() => router.push('/proveedores')}>
              Cancelar
            </Button>
            <Button type="submit" className='bg-green-500 hover:bg-green-600 text-white transition-colors inline-block font-semibold'>
              {!isNew ? 'Guardar Cambios' : 'Crear Proveedor'}
            </Button>
          </div>
        </form>

      </div>
    </>
  )
}