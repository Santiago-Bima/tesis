'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
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
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, PieChart, Pie, Cell, ResponsiveContainer } from 'recharts'
import { NumberFormatter } from '@/utils/numberFormatter'

interface Insumo {
  id: number
  nombre: string
  unidad_medida: string
}

interface InsumoUtilizado {
  insumo: Insumo
  total: number
}

interface ElaborationReport {
  cantidadElaboraciones: number
  cantidadPategras: number
  cantidadBarra: number
  cantidadEnterosCremoso: number
  cantidadMediosCremoso: number
  cantidadCuartosCremoso: number
  insumosUtilizados: InsumoUtilizado[]
  cantidadIncompletas: number
}

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8'];

export default function ElaborationReportsPage() {
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [report, setReport] = useState<ElaborationReport | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const formatter = new NumberFormatter('es-ES');
  const router = useRouter();

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    if(!usuarioLogeado) {
      router.push('/login')
    } else if(JSON.parse(usuarioLogeado).rol !== 'Gerente') {
      router.push('/')
    }
  }, [router])

  const fetchElaborations = async () => {
    if (!startDate || !endDate) {
      setError('Por favor, seleccione ambas fechas.')
      return
    }

    setLoading(true)
    setError(null)

    try {
      const response = await fetch(`http://localhost:8082/elaboraciones/informes?fechaInicio=${startDate}&fechaFin=${endDate}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar el informe de elaboraciones')
      }
      const data = await response.json()
      setReport(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar el informe de elaboraciones. Por favor, intente de nuevo más tarde.')
    } finally {
      setLoading(false)
    }
  }

  const getPieData = () => {
    if (!report) return []
    return [
      { name: 'Pategras', value: report.cantidadPategras },
      { name: 'Barra', value: report.cantidadBarra },
      { name: 'Cremoso Entero', value: report.cantidadEnterosCremoso },
      { name: 'Cremoso Medio', value: report.cantidadMediosCremoso },
      { name: 'Cremoso Cuarto', value: report.cantidadCuartosCremoso },
    ].filter(item => item.value > 0)
  }

  const getBarData = () => {
    if (!report) return []
    const total = report.cantidadElaboraciones
    return [
      { name: 'Pategras', value: report.cantidadPategras },
      { name: 'Barra', value: report.cantidadBarra },
      { name: 'Cremoso Entero', value: report.cantidadEnterosCremoso },
      { name: 'Cremoso Medio', value: report.cantidadMediosCremoso },
      { name: 'Cremoso Cuarto', value: report.cantidadCuartosCremoso },
    ]
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
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Informe de Elaboraciones</h1>

      <Card className="mb-6">
        <CardHeader>
          <CardTitle>Seleccionar Rango de Fechas</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-4 mb-4">
            <div className="flex-1">
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
            <div className="flex-1">
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
          <Button onClick={fetchElaborations} disabled={loading} className="w-full sm:w-auto bg-blue-500 hover:bg-blue-600 text-white">
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
              <CardTitle>Informe de Elaboraciones</CardTitle>
            </CardHeader>
            <CardContent>
              <Table>
                <TableBody>
                  <TableRow>
                    <TableCell className="font-medium">Total de Elaboraciones</TableCell>
                    <TableCell>{formatter.formatNumber(report.cantidadElaboraciones)}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell className="font-medium">Elaboraciones incompletas</TableCell>
                    <TableCell>{formatter.formatNumber(report.cantidadIncompletas)}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell className="font-medium">Pategras</TableCell>
                    <TableCell>{formatter.formatNumber(report.cantidadPategras)}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell className="font-medium">Barra</TableCell>
                    <TableCell>{formatter.formatNumber(report.cantidadBarra)}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell className="font-medium">Cremoso Entero</TableCell>
                    <TableCell>{formatter.formatNumber(report.cantidadEnterosCremoso)}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell className="font-medium">Cremoso Medio</TableCell>
                    <TableCell>{formatter.formatNumber(report.cantidadMediosCremoso)}</TableCell>
                  </TableRow>
                  <TableRow>
                    <TableCell className="font-medium">Cremoso Cuarto</TableCell>
                    <TableCell>{formatter.formatNumber(report.cantidadCuartosCremoso)}</TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </CardContent>
          </Card>

          <div className="grid grid-cols-1 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>Gráfico de Elaboraciones</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="h-[300px] mb-4">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={getBarData()}>
                      <XAxis dataKey="name" />
                      <YAxis />
                      <Tooltip />
                      <Bar dataKey="value" fill="#8884d8" />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
                <div className="overflow-x-auto">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Tipo</TableHead>
                        <TableHead>Cantidad</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {getBarData().map((item) => (
                        <TableRow key={item.name}>
                          <TableCell>{item.name}</TableCell>
                          <TableCell>{formatter.formatNumber(item.value)}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Distribución de Elaboraciones</CardTitle>
              </CardHeader>
              <CardContent className="h-[300px]">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={getPieData()}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={renderCustomizedLabel}
                      outerRadius={80}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {getPieData().map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                    <div className='flex'>
                      <Legend
                        layout="vertical"
                        verticalAlign="middle"
                        align="right"
                        formatter={(value, entry) => {
                          const percentage = ((entry.payload?.value / report.cantidadElaboraciones) * 100).toFixed(2);
                          return `${value}: ${entry.payload?.value} (${percentage}%)`;
                        }}
                      />
                    </div>
                  </PieChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </div>

          <Card>
            <CardHeader>
              <CardTitle>Insumos Utilizados</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Insumo</TableHead>
                      <TableHead>Unidad de Medida</TableHead>
                      <TableHead>Total Utilizado</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {report.insumosUtilizados.map((insumo) => (
                      <TableRow key={insumo.insumo.id}>
                        <TableCell>{insumo.insumo.nombre}</TableCell>
                        <TableCell>{insumo.insumo.unidad_medida}</TableCell>
                        <TableCell>{formatter.formatNumber(insumo.total, true)} {insumo.insumo.unidad_medida}</TableCell>
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