'use client'

import { useState, useEffect } from 'react'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import ModificacionLote from '@/interfaces/ModificacionesLote'
import { useRouter } from 'next/navigation'
import { useModificacionesNuevas } from '@/hooks/useModificacionesNuevas'
import { useNavbar } from '@/contexts/NavbarContext'
import { NumberFormatter } from '@/utils/numberFormatter'
import formatArgDate from '@/utils/formatArgDate'

export default function ModificacionesLotesPage() {
  const [modificaciones, setModificaciones] = useState<ModificacionLote[]>([])
  const [error, setError] = useState<string | null>(null)
  const router = useRouter()

  const { checkNewModification } = useModificacionesNuevas()
  const { reloadNavbar } = useNavbar()

  const formatter = new NumberFormatter('es-ES');

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
        router.push('/')
      }
    }

    fetchModificaciones()
  }, [router])

  const fetchModificaciones = async () => {
    try {
      const response = await fetch('http://localhost:8082/lotes/modificaciones')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar las modificaciones de lotes')
      }
      const data = await response.json()
      setModificaciones(data)
      checkNewModification()
      reloadNavbar()
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar las modificaciones de lotes. Por favor, intente de nuevo más tarde.')
    }
  }

  const ModificacionCard = ({ modificacion }: { modificacion: ModificacionLote }) => (
    <Card className="mb-4">
      <CardHeader>
        <CardTitle className="text-lg">Modificación de Lote</CardTitle>
      </CardHeader>
      <CardContent>
        <p><strong>Fecha:</strong> {formatArgDate(modificacion.fecha)}</p>
        <p><strong>Responsable:</strong> {modificacion.usuario.username}</p>
        <p><strong>Lote:</strong> {modificacion.lote.codigo}</p>
        <p><strong>Motivo:</strong> {modificacion.motivo}</p>
        <p><strong>Cantidad Previa:</strong> {formatter.formatNumber(modificacion.cantidadPrevia)}</p>
        <p><strong>Cantidad Posterior:</strong> {formatter.formatNumber(modificacion.cantidadPosterior)}</p>
        <Card className="mt-4">
          <CardHeader>
            <CardTitle className="text-md">Detalles del Lote</CardTitle>
          </CardHeader>
          <CardContent>
            <p><strong>Código:</strong> {modificacion.lote.codigo}</p>
            <p><strong>Item:</strong> {modificacion.lote.item.nombre}</p>
            <p><strong>Unidad de Medida:</strong> {modificacion.lote.item.unidad_medida}</p>
            <p><strong>Estado:</strong> {modificacion.lote.estado}</p>
            <p><strong>Unidades Actuales:</strong> {formatter.formatNumber(modificacion.lote.unidades)}</p>
          </CardContent>
        </Card>
      </CardContent>
    </Card>
  )

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Modificaciones de Lotes</h1>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {modificaciones.length > 0 ? (
        <>
          {/* Mobile view */}
          <div className="md:hidden">
            {modificaciones.map((modificacion) => (
              <ModificacionCard key={modificacion.id} modificacion={modificacion} />
            ))}
          </div>

          {/* Desktop view */}
          <div className="hidden md:block overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Fecha</TableHead>
                  <TableHead>Responsable</TableHead>
                  <TableHead>Lote</TableHead>
                  <TableHead>Motivo</TableHead>
                  <TableHead>Cantidad Previa</TableHead>
                  <TableHead>Cantidad Posterior</TableHead>
                  <TableHead>Detalles</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {modificaciones.map((modificacion) => (
                  <TableRow key={modificacion.id}>
                    <TableCell>{formatArgDate(modificacion.fecha)}</TableCell>
                    <TableCell>{modificacion.usuario.username}</TableCell>
                    <TableCell>{modificacion.lote.codigo}</TableCell>
                    <TableCell>{modificacion.motivo}</TableCell>
                    <TableCell>{formatter.formatNumber(modificacion.cantidadPrevia)}</TableCell>
                    <TableCell>{formatter.formatNumber(modificacion.cantidadPosterior)}</TableCell>
                    <TableCell>
                      <Card>
                        <CardHeader>
                          <CardTitle>Detalles del Lote</CardTitle>
                        </CardHeader>
                        <CardContent>
                          <p><strong>Código:</strong> {modificacion.lote.codigo}</p>
                          <p><strong>Item:</strong> {modificacion.lote.item.nombre}</p>
                          <p><strong>Unidad de Medida:</strong> {modificacion.lote.item.unidad_medida}</p>
                          <p><strong>Estado:</strong> {modificacion.lote.estado}</p>
                          <p><strong>Unidades Actuales:</strong> {formatter.formatNumber(modificacion.lote.unidades)}</p>
                        </CardContent>
                      </Card>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        </>
      ) : (
        <p className="text-center text-gray-500 mt-6">No se encontraron modificaciones de lotes.</p>
      )}
    </div>
  )
}