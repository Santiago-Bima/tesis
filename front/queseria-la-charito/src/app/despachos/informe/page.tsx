'use client'

import { useState, useEffect } from 'react'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from 'lucide-react'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { useRouter } from 'next/navigation'
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, PieChart, Pie, Cell, ResponsiveContainer } from 'recharts'
import { NumberFormatter } from '@/utils/numberFormatter'

interface Destino {
  id: number
  calle: string
  numero: number
  barrio: string
}

interface DetalleDespacho {
  destino: Destino
  cantidadPategras: number
  cantidadBarra: number
  cantidadEnterosCremoso: number
  cantidadMediosCremoso: number
  cantidadCuartosCremoso: number
}

interface InformeDespachos {
  cantidadDespachos: number
  totalUnidadesDespachadas: number
  cantidadTotalPategras: number
  cantidadTotalBarra: number
  cantidadTotalEnterosCremoso: number
  cantidadTotalMediosCremoso: number
  cantidadTotalCuartosCremoso: number
  detallesDespacho: DetalleDespacho[]
}

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8'];

export default function DespachosReportPage() {
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [report, setReport] = useState<InformeDespachos | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const router = useRouter()
  const formatter = new NumberFormatter('es-ES');

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Gerente') {
        router.push('/')
      }
    }
  }, [router])

  const fetchDespachos = async () => {
    if (!startDate || !endDate) {
      setError('Por favor, seleccione ambas fechas.')
      return
    }

    setLoading(true)
    setError(null)

    try {
      const response = await fetch(`http://localhost:8082/despachos/informes?fechaInicio=${startDate}&fechaFin=${endDate}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar el informe de despachos')
      }
      const data = await response.json()
      setReport(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar el informe de despachos. Por favor, intente de nuevo más tarde.')
    } finally {
      setLoading(false)
    }
  }

  const getChartData = () => {
    if (!report) return []
    return [
      { name: 'Pategras', value: report.cantidadTotalPategras },
      { name: 'Barra', value: report.cantidadTotalBarra },
      { name: 'Cremoso Entero', value: report.cantidadTotalEnterosCremoso },
      { name: 'Cremoso Medio', value: report.cantidadTotalMediosCremoso },
      { name: 'Cremoso Cuarto', value: report.cantidadTotalCuartosCremoso },
    ]
  }

  const getPieChartData = () => {
    return getChartData().filter(item => item.value > 0)
  }

  const renderCustomizedLabel = ({ cx, cy, midAngle, innerRadius, outerRadius, percent }) => {
    const RADIAN = Math.PI / 180;
    const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
    const x = cx + radius * Math.cos(-midAngle * RADIAN);
    const y = cy + radius * Math.sin(-midAngle * RADIAN);
  
    return (
      <text x={x} y={y} fill="white" textAnchor={x > cx ? 'start' : 'end'} dominantBaseline="central">
        {`${(percent * 100).toFixed(0)}%`}
      </text>
    );
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Informe de Despachos</h1>

      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Seleccionar Rango de Fechas</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-4 mb-4">
            <div className="w-full sm:w-1/2">
              <label htmlFor="startDate" className="block text-sm font-medium text-gray-700 mb-1">
                Fecha de inicio
              </label>
              <Input
                type="date"
                id="startDate"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
              />
            </div>
            <div className="w-full sm:w-1/2">
              <label htmlFor="endDate" className="block text-sm font-medium text-gray-700 mb-1">
                Fecha de fin
              </label>
              <Input
                type="date"
                id="endDate"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
              />
            </div>
          </div>
          <Button onClick={fetchDespachos} disabled={loading} className="w-full sm:w-auto bg-blue-500 hover:bg-blue-600 text-white">
            {loading ? 'Cargando...' : 'Generar Informe'}
          </Button>
        </CardContent>
      </Card>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {report && (
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Resumen de Despachos</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="overflow-x-auto">
                <Table>
                  <TableBody>
                    <TableRow>
                      <TableCell className="font-medium">Cantidad de Despachos</TableCell>
                      <TableCell>{formatter.formatNumber(report.cantidadDespachos)}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">Total Unidades Despachadas</TableCell>
                      <TableCell>{formatter.formatNumber(report.totalUnidadesDespachadas)}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">Total Pategras</TableCell>
                      <TableCell>{formatter.formatNumber(report.cantidadTotalPategras)}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">Total Barra</TableCell>
                      <TableCell>{formatter.formatNumber(report.cantidadTotalBarra)}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">Total Cremoso Entero</TableCell>
                      <TableCell>{formatter.formatNumber(report.cantidadTotalEnterosCremoso)}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">Total Cremoso Medio</TableCell>
                      <TableCell>{formatter.formatNumber(report.cantidadTotalMediosCremoso)}</TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell className="font-medium">Total Cremoso Cuarto</TableCell>
                      <TableCell>{formatter.formatNumber(report.cantidadTotalCuartosCremoso)}</TableCell>
                    </TableRow>
                  </TableBody>
                </Table>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Gráfico de Despachos</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="h-[300px] sm:h-[400px]">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={getChartData()}>
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="value" fill="#8884d8" />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Distribución de Despachos</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="h-[300px] sm:h-[400px]">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={getPieChartData()}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={renderCustomizedLabel}
                      outerRadius="80%"
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {getPieChartData().map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Detalles de Despachos por Destino</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Destino</TableHead>
                      <TableHead>Pategras</TableHead>
                      <TableHead>Barra</TableHead>
                      <TableHead>Cremoso Entero</TableHead>
                      <TableHead>Cremoso Medio</TableHead>
                      <TableHead>Cremoso Cuarto</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {report.detallesDespacho.map((detalle, index) => (
                      <TableRow key={index}>
                        <TableCell>{`${detalle.destino.calle} ${detalle.destino.numero}, ${detalle.destino.barrio}`}</TableCell>
                        <TableCell>{formatter.formatNumber(detalle.cantidadPategras)}</TableCell>
                        <TableCell>{formatter.formatNumber(detalle.cantidadBarra)}</TableCell>
                        <TableCell>{formatter.formatNumber(detalle.cantidadEnterosCremoso)}</TableCell>
                        <TableCell>{formatter.formatNumber(detalle.cantidadMediosCremoso)}</TableCell>
                        <TableCell>{formatter.formatNumber(detalle.cantidadCuartosCremoso)}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {!report && !loading && !error && (
        <p className="text-center text-gray-500 mt-6">No se ha generado ningún informe aún. Por favor, seleccione un rango de fechas y genere el informe.</p>
      )}
    </div>
  )
}