'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { useDespachoNuevo } from '@/hooks/useDespachoNuevo'
import { NumberFormatter } from '@/utils/numberFormatter'
import formatArgDate from '@/utils/formatArgDate'

interface Item {
  id: number
  nombre: string
  unidad_medida: string
}

interface Lote {
  codigo: string
  item: Item
  estado: string
  unidades: number
}

interface DetalleDespacho {
  id: number
  lote: Lote
  cantidadEnteros: number
  cantidadMedios: number
  cantidadCuartos: number
}

interface Destino {
  id: number
  calle: string
  numero: number
  barrio: string
}

interface Vehiculo {
  id: number
  matricula: string
  disponible: boolean
}

interface Usuario {
  username: string
}

interface Despacho {
  id: number
  fecha: string
  queso: string
  cantidadTotal: number
  destino: Destino
  vehiculo: Vehiculo
  usuario: Usuario
  estado: string
  detalles: DetalleDespacho[]
}

export default function MiDespachoPage() {
  const [despacho, setDespacho] = useState<Despacho | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  const router = useRouter()
  const [username, setUsername] = useState<string>('');
  const { checkNewDispatch } = useDespachoNuevo()
  const formatter = new NumberFormatter('es-ES');

  
  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");

    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Operario') {
        router.push('/')
      } else {
        fetchDespacho(JSON.parse(usuarioLogeado).username)
        setUsername(JSON.parse(usuarioLogeado).username)
      }
    }
  }, [router])

  const fetchDespacho = async (username: string) => {
    try {
      const response = await fetch(`http://localhost:8082/despachos/mi-despacho/${username}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar el despacho')
      }
      const data = await response.json()
      setDespacho(data.length === 0 ? null : data[0])
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar el despacho. Por favor, intente de nuevo más tarde.')
    } finally {
      setLoading(false)
    }
  }

  const handleChangeEstado = async () => {
    if (!despacho) return

    try {
      const response = await fetch(`http://localhost:8082/despachos/cambiar-estado/${despacho.id}`, {
        method: 'PUT',
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cambiar el estado del despacho')
      }
      // Refetch the despacho to get the updated state
      await fetchDespacho(username)
      checkNewDispatch()
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cambiar el estado del despacho. Por favor, intente de nuevo más tarde.')
    }
  }

  if (loading) {
    return <div className="text-center mt-8">Cargando...</div>
  }

  if (error) {
    return (
      <Alert variant="destructive" className="mt-8">
        <AlertCircle className="h-4 w-4" />
        <AlertDescription>{error}</AlertDescription>
      </Alert>
    )
  }

  if (!despacho) {
    return <div className="text-center mt-8">No se encontró ningún despacho asignado.</div>
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Mi Despacho</h1>

      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Información General</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableBody>
              <TableRow>
                <TableCell className="font-medium">ID</TableCell>
                <TableCell>{despacho.id}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell className="font-medium">Fecha</TableCell>
                <TableCell>{formatArgDate(despacho.fecha)}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell className="font-medium">Queso</TableCell>
                <TableCell>{despacho.queso}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell className="font-medium">Cantidad Total</TableCell>
                <TableCell>{formatter.formatNumber(despacho.cantidadTotal)}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell className="font-medium">Destino</TableCell>
                <TableCell>{`${despacho.destino.calle} ${despacho.destino.numero}, ${despacho.destino.barrio}`}</TableCell>
              </TableRow>
              <TableRow>
                <TableCell className="font-medium">Vehículo</TableCell>
                <TableCell>{despacho.vehiculo.matricula}</TableCell>
              </TableRow>
              <TableRow>
                <DistanceInfo destino={despacho.destino} />
              </TableRow>
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Detalles del Despacho</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Lote</TableHead>
                <TableHead>Item</TableHead>
                <TableHead>Enteros</TableHead>
                <TableHead>Medios</TableHead>
                <TableHead>Cuartos</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {despacho.detalles.map((detalle) => (
                <TableRow key={detalle.id}>
                  <TableCell>{detalle.lote.codigo}</TableCell>
                  <TableCell>{detalle.lote.item.nombre}</TableCell>
                  <TableCell>{formatter.formatNumber(detalle.cantidadEnteros)}</TableCell>
                  <TableCell>{formatter.formatNumber(detalle.cantidadMedios)}</TableCell>
                  <TableCell>{formatter.formatNumber(detalle.cantidadCuartos)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <Button onClick={handleChangeEstado} className='bg-green-500 hover:bg-green-600 text-white transition-colors w-full block'>
        {despacho.estado === 'PorEntregar' ? 'Iniciar Despacho' : 'Terminar Despacho'}
      </Button>
    </div>
  )
}

function DistanceInfo({ destino }: { destino: Destino }) {
  const [distanceInfo, setDistanceInfo] = useState<{ distance: string, duration: string } | null>(null)

  useEffect(() => {
    const fetchDistanceInfo = async () => {
      const info = await calculateDistance(destino)
      setDistanceInfo(info)
    }
    fetchDistanceInfo()
  }, [destino])

  return (
    <div>
      <h3 className="font-semibold">Información de viaje:</h3>
      {distanceInfo ? (
        <p>Distancia: {distanceInfo.distance}, Tiempo estimado: {distanceInfo.duration}</p>
      ) : (
        <p>Calculando información de viaje...</p>
      )}
    </div>
  )
}

async function calculateDistance(destino: Destino) {
  try {
    const response = await fetch('/api/calculateDistance', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ destino }),
    })

    if (!response.ok) {
      throw new Error('Error en la solicitud al servidor')
    }

    const data = await response.json()
    return data
  } catch (error) {
    console.error('Error al calcular la distancia:', error)
    return { distance: 'N/A', duration: 'N/A' }
  }
}