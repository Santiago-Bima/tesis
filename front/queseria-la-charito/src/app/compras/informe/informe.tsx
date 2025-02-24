'use client'

import { useState, useEffect } from 'react'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from 'lucide-react'
import { useRouter } from 'next/navigation'
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, PieChart, Pie, Cell, ResponsiveContainer } from 'recharts'
import { NumberFormatter } from '@/utils/numberFormatter'

interface Lote {
  codigo: string
  item: {
    id: number
    nombre: string
    unidad_medida: string
  }
  estado: string
  unidades: number
}

interface Proveedor {
  id: number
  nombre: string
  email: string
  alias: string
  cuit: string
  banco: string
  tipoCuenta: string
  telefono: number
  insumo: {
    id: number
    nombre: string
    unidad_medida: string
  }
  unidadMedida: string
  cantidadMedida: number
  costo: number
}

interface Comprobante {
  id: number
  lote: Lote
  proveedor: Proveedor
  cantidad: number
  subtotal: number
}

interface Informe {
  insumo: string
  total: number
  comprobantes: Comprobante[]
}

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8'];

export default function Informes() {
  const [fechaInicio, setFechaInicio] = useState<string>('')
  const [fechaFin, setFechaFin] = useState<string>('')
  const [informes, setInformes] = useState<Informe[]>([])
  const [error, setError] = useState<string | null>(null)
  const router = useRouter();
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

  const fetchInformes = async () => {
    if (!fechaInicio || !fechaFin) {
      setError('Por favor, seleccione ambas fechas.')
      return
    }

    try {
      const response = await fetch(`http://localhost:8082/comprobantes/informes?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los informes')
      }
      const data = await response.json()
      setInformes(data)
      setError(null)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los informes. Por favor, intente de nuevo más tarde.')
    }
  }

  const getChartData = () => {
    return informes.map(informe => ({
      name: informe.insumo,
      value: informe.total
    }))
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

  const totalGastado = informes.reduce((sum, informe) => sum + informe.total, 0);

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-2xl sm:text-3xl font-bold mb-6 text-indigo-700">Informes de Comprobantes</h1>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <Card className="mb-6">
        <CardHeader>
          <CardTitle className="text-lg sm:text-xl">Seleccionar Rango de Fechas</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-4 mb-4">
            <div className="w-full sm:w-1/2">
              <label htmlFor="fechaInicio" className="block text-sm font-medium text-gray-700 mb-1">
                Fecha de inicio
              </label>
              <Input
                type="date"
                id="fechaInicio"
                value={fechaInicio}
                onChange={(e) => setFechaInicio(e.target.value)}
                className="w-full"
              />
            </div>
            <div className="w-full sm:w-1/2">
              <label htmlFor="fechaFin" className="block text-sm font-medium text-gray-700 mb-1">
                Fecha de fin
              </label>
              <Input
                type="date"
                id="fechaFin"
                value={fechaFin}
                onChange={(e) => setFechaFin(e.target.value)}
                className="w-full"
              />
            </div>
          </div>
          <Button onClick={fetchInformes} className="w-full sm:w-auto bg-blue-500 hover:bg-blue-600 text-white">Generar informe</Button>
        </CardContent>
      </Card>

      {informes.length > 0 && (
        <>
          <Card className="mb-6">
            <CardHeader>
              <CardTitle className="text-lg sm:text-xl">Resumen de Gastos</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-xl sm:text-2xl font-bold">Total Gastado: ${formatter.formatCurrency(totalGastado)}</p>
            </CardContent>
          </Card>

          <Card className="mb-6">
            <CardHeader>
              <CardTitle className="text-lg sm:text-xl">Gráfico de Comprobantes por Insumo</CardTitle>
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

          <Card className="mb-6">
            <CardHeader>
              <CardTitle className="text-lg sm:text-xl">Distribución de Comprobantes por Insumo</CardTitle>
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

          <div className="overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Insumo</TableHead>
                  <TableHead>Total</TableHead>
                  <TableHead>Detalles</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {informes.map((informe, index) => (
                  <TableRow key={index}>
                    <TableCell>{informe.insumo}</TableCell>
                    <TableCell>${formatter.formatCurrency(informe.total)}</TableCell>
                    <TableCell>
                      <Accordion type="single" collapsible className="w-full">
                        <AccordionItem value={`item-${index}`}>
                          <AccordionTrigger>Ver comprobantes</AccordionTrigger>
                          <AccordionContent>
                            <div className="overflow-x-auto">
                              <Table>
                                <TableHeader>
                                  <TableRow>
                                    <TableHead>ID</TableHead>
                                    <TableHead>Proveedor</TableHead>
                                    <TableHead>Lote</TableHead>
                                    <TableHead>Cantidad</TableHead>
                                    <TableHead>Subtotal</TableHead>
                                  </TableRow>
                                </TableHeader>
                                <TableBody>
                                  {informe.comprobantes.map((comprobante) => (
                                    <TableRow key={comprobante.id}>
                                      <TableCell>{comprobante.id}</TableCell>
                                      <TableCell>{comprobante.proveedor.nombre}</TableCell>
                                      <TableCell>{comprobante.lote.codigo}</TableCell>
                                      <TableCell>{formatter.formatNumber(comprobante.cantidad)} {comprobante.proveedor.unidadMedida}</TableCell>
                                      <TableCell>${formatter.formatCurrency(comprobante.subtotal)}</TableCell>
                                    </TableRow>
                                  ))}
                                </TableBody>
                              </Table>
                            </div>
                          </AccordionContent>
                        </AccordionItem>
                      </Accordion>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        </>
      )}

      {informes.length === 0 && !error && (
        <p className="text-center text-gray-500 mt-6">No se encontraron informes para el rango de fechas seleccionado.</p>
      )}
    </div>
  )
}