'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
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
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"
import { useControlStockNuevo } from '@/hooks/useControlStockNuevo'
import { useNavbar } from '@/contexts/NavbarContext'
import { NumberFormatter } from '@/utils/numberFormatter'
import formatArgDate from '@/utils/formatArgDate'

interface CantidadInsumo {
  insumo: string
  cantidad: number
}

interface ControlStock {
  id: number
  fecha: string
  cantidadEnterosEsperada: number
  cantidadEnterosObtenida: number
  CantidadMediosEsperada: number
  cantidadMediosObtenida: number
  cantidadCuartosEsperada: number
  cantidadCuartosObtenida: number
  cantidadesInsumosObtenidos: CantidadInsumo[]
  cantidadesInsumosEsperados: CantidadInsumo[]
  observaciones: string
  usuario: { username: string }
  nuevo: boolean
}

export default function ControlesStockPage() {
  const [controles, setControles] = useState<ControlStock[]>([])
  const [error, setError] = useState<string | null>(null)
  const router = useRouter()

  const { checkNewControl } = useControlStockNuevo();
  const { reloadNavbar } = useNavbar();

  const formatter = new NumberFormatter('es-ES');


  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Subgerente') {
        router.push('/')
      } else {
        fetchControles()
      }
    }
  }, [router])

  const fetchControles = async () => {
    try {
      const response = await fetch('http://localhost:8082/controles')
      if (!response.ok) {
        throw new Error('Error al cargar los controles de stock')
      }
      const data = await response.json()
      setControles(data)
      checkNewControl()
      reloadNavbar()
    } catch (error) {
      setError('Error al cargar los controles de stock. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-indigo-700">Controles de Stock</h1>
      </div>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {controles.length > 0 ? (
       <Table>
       <TableHeader className="hidden lg:table-header-group">
         <TableRow>
           <TableHead>Fecha</TableHead>
           <TableHead>Responsable</TableHead>
           <TableHead>Enteros (E/O)</TableHead>
           <TableHead>Medios (E/O)</TableHead>
           <TableHead>Cuartos (E/O)</TableHead>
           <TableHead>Detalles</TableHead>
         </TableRow>
       </TableHeader>
       <TableBody>
         {controles.map((control) => (
           <TableRow 
             key={control.id} 
             className="flex flex-col lg:table-row border-b lg:border-0">
             <TableCell className="flex lg:table-cell justify-between py-2">
               <span className="lg:hidden font-semibold">Fecha:</span>
               {formatArgDate(control.fecha)}
             </TableCell>
             <TableCell className="flex lg:table-cell justify-between py-2">
               <span className="lg:hidden font-semibold">Responsable:</span>
               {control.usuario.username}
             </TableCell>
             <TableCell className="flex lg:table-cell justify-between py-2">
               <span className="lg:hidden font-semibold">Enteros (E/O):</span>
               {formatter.formatNumber(control.cantidadEnterosEsperada, true)} / {formatter.formatNumber(control.cantidadEnterosObtenida, true)}
             </TableCell>
             <TableCell className="flex lg:table-cell justify-between py-2">
               <span className="lg:hidden font-semibold">Medios (E/O):</span>
               {formatter.formatNumber(control.CantidadMediosEsperada, true)} / {formatter.formatNumber(control.cantidadMediosObtenida, true)}
             </TableCell>
             <TableCell className="flex lg:table-cell justify-between py-2">
               <span className="lg:hidden font-semibold">Cuartos (E/O):</span>
               {formatter.formatNumber(control.cantidadCuartosEsperada, true)} / {formatter.formatNumber(control.cantidadCuartosObtenida, true)}
             </TableCell>
             <TableCell className="flex flex-col lg:table-cell lg:text-center lg:align-middle py-2">
               <Accordion type="single" collapsible>
                 <AccordionItem value={`item-${control.id}`}>
                   <AccordionTrigger>Ver detalles</AccordionTrigger>
                   <AccordionContent>
                     <Card>
                       <CardHeader>
                         <CardTitle>Insumos</CardTitle>
                       </CardHeader>
                       <CardContent>
                         <Table>
                           <TableHeader>
                             <TableRow>
                               <TableHead>Insumo</TableHead>
                               <TableHead>Esperado</TableHead>
                               <TableHead>Obtenido</TableHead>
                             </TableRow>
                           </TableHeader>
                           <TableBody>
                             {control.cantidadesInsumosEsperados.map((insumoEsperado, index) => {
                               const insumoObtenido = control.cantidadesInsumosObtenidos.find(
                                 (i) => i.insumo === insumoEsperado.insumo
                               )
                               return (
                                 <TableRow key={index}>
                                   <TableCell>{insumoEsperado.insumo}</TableCell>
                                   <TableCell>{formatter.formatNumber(insumoEsperado.cantidad, true)}</TableCell>
                                   <TableCell>{formatter.formatNumber(insumoObtenido?.cantidad, true) || 0}</TableCell>
                                 </TableRow>
                               )
                             })}
                           </TableBody>
                         </Table>
                       </CardContent>
                     </Card>
                     <Card className="mt-4">
                       <CardHeader>
                         <CardTitle>Observaciones</CardTitle>
                       </CardHeader>
                       <CardContent>
                         <p>{control.observaciones || 'Sin observaciones'}</p>
                       </CardContent>
                     </Card>
                   </AccordionContent>
                 </AccordionItem>
               </Accordion>
             </TableCell>
           </TableRow>
         ))}
       </TableBody>
     </Table>
     
      ) : (
        <p className="text-center text-gray-500 mt-6">No se encontraron controles de stock.</p>
      )}
    </div>
  )
}