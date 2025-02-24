'use client'

import { useState, useEffect } from 'react'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { AlertCircle } from 'lucide-react'
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion"
import Link from 'next/link'
import { TipoQueso } from '@/interfaces/tipoQueso'
import Elaboracion from '@/interfaces/elaboracion'
import DetalleCorte from '@/interfaces/detallecorte'
import { useRouter } from 'next/navigation'
import formatArgDate from '@/utils/formatArgDate'
import { NumberFormatter } from '@/utils/numberFormatter'
import { DialogFooter, DialogHeader, Dialog, DialogContent, DialogDescription, DialogTitle } from '@/components/ui/dialog'

export default function ElaboracionesList() {
  const [elaboraciones, setElaboraciones] = useState<Elaboracion[]>([])
  const [quesos, setQuesos] = useState<TipoQueso[]>([])
  const [selectedQueso, setSelectedQueso] = useState<number | null>(null)
  const [fechaInicio, setFechaInicio] = useState<string>('')
  const [fechaFin, setFechaFin] = useState<string>('')
  const [error, setError] = useState<string | null>(null)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [selectedElaboracion, setSelectedElaboracion] = useState<Elaboracion | null>(null)
  const [fechaEntrada, setFechaEntrada] = useState<string>('')
  const [fechaSalida, setFechaSalida] = useState<string>('')
  const [fechaPintadoEmbolsado, setFechaPintadoEmbolsado] = useState<string>('')
  const [isCorteModalOpen, setIsCorteModalOpen] = useState(false)
  const [username, setUsername] = useState<string>('')
  const [newCorte, setNewCorte] = useState<DetalleCorte>({
    id: 0,
    cantidad: 0,
    peso: 0,
    corte: ''
  })
  const [tiposCorte, setTiposCorte] = useState<string[]>([])
  const router = useRouter();
  const formatter = new NumberFormatter('es-ES');
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [elaboracionToDelete, setElaboracionToDelete] = useState<string | null>(null)

  useEffect(() => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(!usuarioLogeado) {
      router.push('/login')
    } else {
      if(JSON.parse(usuarioLogeado).rol !== 'Operario') {
        router.push('/')
      } else {
        setUsername(JSON.parse(usuarioLogeado).username)
        fetchQuesos()
        fetchTiposCorte()
      }
    }

  }, [router])

  useEffect(() => {
    fetchElaboraciones()
  }, [selectedQueso, fechaInicio, fechaFin])

  const fetchTiposCorte = async () => {
    try {
      const response = await fetch('http://localhost:8082/cortes')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los tipos de corte')
      }
      const data = await response.json()
      setTiposCorte(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los tipos de corte. Por favor, intente de nuevo más tarde.')
    }
  }

  const fetchQuesos = async () => {
    try {
      const response = await fetch('http://localhost:8082/productos')
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar los quesos')
      }
      const data = await response.json()
      setQuesos(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar los quesos. Por favor, intente de nuevo más tarde.')
    }
  }

  const fetchElaboraciones = async () => {
    if (!selectedQueso) {
      return
    }
    
    setError(null)

    let url = `http://localhost:8082/elaboraciones/mis-elaboraciones/${username}?productId=${selectedQueso}`
    if (fechaInicio && fechaFin) {
      url += `&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`
    }

    try {
      const response = await fetch(url)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al cargar las elaboraciones')
      }
      const data = await response.json()
      setElaboraciones(data)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al cargar las elaboraciones. Por favor, intente de nuevo más tarde.')
    }
  }

  const calcularCantidadInsumo = (cantidadLeche: number, cantidadLecheFormula: number, cantidadInsumo: number) => {
    return (cantidadLeche / cantidadLecheFormula) * cantidadInsumo
  }

  const handleDeleteElaboracion = async () => {
    if (elaboracionToDelete == null) return;

    try {
      const response = await fetch(`http://localhost:8082/elaboraciones/${elaboracionToDelete}`, {
        method: 'DELETE',
      })
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al eliminar la elaboración')
      }
      fetchElaboraciones()
      setIsDeleteDialogOpen(false)
      setElaboracionToDelete(null)
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al eliminar la elaboración. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleProductoChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const itemId = parseInt(event.target.value)
    setSelectedQueso(itemId)
  }

  const handleOpenModal = (elaboracion: Elaboracion) => {
    setSelectedElaboracion(elaboracion)
    setFechaEntrada('')
    setFechaSalida('')
    setFechaPintadoEmbolsado('')
    setIsModalOpen(true)
  }

  const handleSubmitModal = async () => {
    if (!selectedElaboracion) return

    try {
      let url = ''
      let body = {}
      let method = 'PUT'

      if (!selectedElaboracion.fechaEntradaMaduracion) {
        url = `http://localhost:8082/elaboraciones/maduracion/${selectedElaboracion.id}`
        body = { fechaEntrada, fechaSalida }
      } else if (selectedElaboracion.formula.queso.item.nombre.toLowerCase() === 'pategras') {
        url = `http://localhost:8082/elaboraciones/pintado/${selectedElaboracion.id}?fechaPintado=${fechaPintadoEmbolsado}`
        method = 'PUT'
      } else {
        url = `http://localhost:8082/elaboraciones/embolsado/${selectedElaboracion.id}?fechaEmbolsado=${fechaPintadoEmbolsado}`
        method = 'PUT'
      }

      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body),
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al actualizar la elaboración')
      }

      setIsModalOpen(false)
      fetchElaboraciones()
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al actualizar la elaboración. Por favor, intente de nuevo más tarde.')
    }
  }

  const handleOpenCorteModal = (elaboracion: Elaboracion) => {
    setSelectedElaboracion(elaboracion)
    setNewCorte({
      id: 0,
      cantidad: 0,
      peso: 0,
      corte: ''
    })
    setIsCorteModalOpen(true)
  }

  const handleSubmitCorte = async () => {
    if (!selectedElaboracion) return

    try {
      const response = await fetch(`http://localhost:8082/elaboraciones/cortes/${selectedElaboracion.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          cantidad: newCorte.cantidad,
          peso: newCorte.peso,
          corte: newCorte.corte
        }),
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Error al agregar el corte')
      }

      setIsCorteModalOpen(false)
      fetchElaboraciones()
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Error al agregar el corte. Por favor, intente de nuevo más tarde.')
    }
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6 text-indigo-700">Lista de Elaboraciones</h1>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <div className="flex flex-col md:flex-row w-full gap-4 justify-between mb-6">
        <div className="flex flex-col md:flex-row w-full md:w-3/4 gap-4">
          <select
            value={selectedQueso?.toString() || ''}
            onChange={handleProductoChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="" disabled>Seleccione un queso</option>
            {quesos.map((queso) => (
              <option key={queso.id_tipo_queso} value={queso.id_tipo_queso}>
                {queso.item.nombre}
              </option>
            ))}
          </select>
          <Input
            type="date"
            value={fechaInicio}
            onChange={(e) => setFechaInicio(e.target.value)}
            placeholder="Fecha de inicio (YYYY-MM-DD)"
            className="w-full md:w-auto"
          />
          <Input
            type="date"
            value={fechaFin}
            onChange={(e) => setFechaFin(e.target.value)}
            placeholder="Fecha de fin (YYYY-MM-DD)"
            className="w-full md:w-auto"
          />
        </div>

        <div className="flex items-center justify-center md:justify-end mt-4 md:mt-0">
          <Link href="/elaboraciones/crear">
            <Button className="w-full md:w-auto bg-green-500 hover:bg-green-600 text-white rounded-md transition-colors inline-block font-semibold">
              Agregar Nueva Elaboración
            </Button>
          </Link>
        </div>
      </div>

      {elaboraciones.length > 0 ? (
        <Accordion type="single" collapsible className="mt-6">
          {elaboraciones.map((elaboracion) => (
            <AccordionItem key={elaboracion.id} value={elaboracion.id}>
              <AccordionTrigger>
                <div className="flex flex-col md:flex-row justify-between w-full text-sm md:text-base">
                  <span className="mb-2 md:mb-0">ID: {elaboracion.id}</span>
                  <span className="mb-2 md:mb-0">Fecha: {formatArgDate(elaboracion.fecha)}</span>
                  <span className="mb-2 md:mb-0">Leche: {formatter.formatNumber(elaboracion.cantidadLeche, true)} L</span>
                  <span>Fórmula: {elaboracion.formula.codigo}</span>
                </div>
              </AccordionTrigger>
              <AccordionContent>
                <div className="space-y-2 text-sm md:text-base">
                  <h3 className="font-semibold">Insumos utilizados:</h3>
                  <ul>
                    {elaboracion.formula.detalles.map((detalle) => (
                      <li key={detalle.id_detalle}>
                        {detalle.insumo.nombre}: {formatter.formatNumber(parseFloat(calcularCantidadInsumo(elaboracion.cantidadLeche, elaboracion.formula.cantidad_leche, detalle.cantidad).toFixed(2)), true)} {detalle.insumo.unidad_medida}
                      </li>
                    ))}
                  </ul>
                  {elaboracion.tiempoSalado && <p>Tiempo de salado: {formatter.formatNumber(elaboracion.tiempoSalado)} minutos</p>}
                  {elaboracion.detalleCorte ? (
                    <div>
                      {elaboracion.fechaEntradaMaduracion && <p>Fecha de entrada a maduración: {formatArgDate(elaboracion.fechaEntradaMaduracion)}</p>}
                      {elaboracion.fechaSalidaMaduracion && <p>Fecha de salida de maduración: {formatArgDate(elaboracion.fechaSalidaMaduracion)}</p>}
                      {elaboracion.fechaEmbolsado && <p>Fecha de embolsado: {formatArgDate(elaboracion.fechaEmbolsado)}</p>}
                      {elaboracion.fechaPintado && <p>Fecha de pintado: {formatArgDate(elaboracion.fechaPintado)}</p>}
                      <div>
                        <h3 className="font-semibold">Detalle de corte:</h3>
                        <p>Corte: {elaboracion.detalleCorte.corte}, Cantidad: {formatter.formatNumber(elaboracion.detalleCorte.cantidad)}, Peso: {formatter.formatNumber(elaboracion.detalleCorte.peso, true)} kg</p>
                      </div>
                      {elaboracion.control && (
                        <div>
                          <h3 className="font-semibold">Control de calidad:</h3>
                          <p>Fecha: {formatArgDate(elaboracion.control.fecha)}</p>
                          <p>Sabor: {elaboracion.control.pruebaSabor}</p>
                          <p>Consistencia: {elaboracion.control.pruebaConcistencia}</p>
                          <p>Aroma: {elaboracion.control.pruebaAroma}</p>
                          <p>Observación: {elaboracion.control.observacion}</p>
                        </div>
                      )}
                    </div>
                  ) : (
                    <div className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2 mt-4 mb-4">
                      <Button onClick={() => handleOpenCorteModal(elaboracion)} className="w-full sm:w-auto bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors inline-block font-semibold">
                        Agregar Corte
                      </Button>
                      <Button variant="destructive" className="w-full sm:w-auto bg-red-500 hover:bg-red-600 text-black rounded-md transition-colors inline-block font-semibold" onClick={() => {
                        setElaboracionToDelete(elaboracion.id)
                        setIsDeleteDialogOpen(true)
                      }}>
                        Eliminar Elaboración
                      </Button>
                    </div>
                  )}

                  <div className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2 mt-4 mb-4">
                    {elaboracion.detalleCorte && !elaboracion.fechaEntradaMaduracion && (
                      <Button className="w-full sm:w-auto bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors inline-block font-semibold" onClick={() => handleOpenModal(elaboracion)}>Gestionar Maduración</Button>
                    )}
                    {elaboracion.fechaEntradaMaduracion && !elaboracion.fechaPintado && !elaboracion.fechaEmbolsado && (
                      <Button className="w-full sm:w-auto bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors inline-block font-semibold" onClick={() => handleOpenModal(elaboracion)}>
                        {elaboracion.formula.queso.item.nombre.toLowerCase() === 'pategras' ? 'Gestionar Pintado' : 'Gestionar Embolsado'}
                      </Button>
                    )}
                    {(elaboracion.fechaPintado || elaboracion.fechaEmbolsado) && !elaboracion.control && (
                      <Link
                        href={`/elaboraciones/${elaboracion.id}/control-calidad`}
                        className="w-full sm:w-auto"
                      >
                        <Button className="w-full bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors inline-block font-semibold">
                          Agregar Control de Calidad
                        </Button>
                      </Link>
                    )}
                    { elaboracion.detalleCorte != null && 
                      <Button variant="destructive" className="w-full sm:w-auto bg-red-500 hover:bg-red-600 text-white rounded-md transition-colors inline-block font-semibold" onClick={() => {
                        setElaboracionToDelete(elaboracion.id)
                        setIsDeleteDialogOpen(true)
                      }}>
                        Eliminar Elaboración
                      </Button>
                    }
                  </div>
                </div>
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      ) : (
        <div className="text-center py-4 text-gray-500">No hay elaboraciones disponibles.</div>
      )}

      <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>
              {!selectedElaboracion?.fechaEntradaMaduracion
                ? 'Gestionar Maduración'
                : selectedElaboracion.formula.queso.item.nombre.toLowerCase() === 'pategras'
                ? 'Gestionar Pintado'
                : 'Gestionar Embolsado'}
            </DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            {!selectedElaboracion?.fechaEntradaMaduracion ? (
              <div className="grid gap-4">
                <div className="flex flex-col">
                  <label htmlFor="fechaEntrada" className="mb-2">
                    Fecha de Entrada
                  </label>
                  <Input
                    id="fechaEntrada"
                    type="date"
                    value={fechaEntrada}
                    onChange={(e) => setFechaEntrada(e.target.value)}
                  />
                </div>
                <div className="flex flex-col">
                  <label htmlFor="fechaSalida" className="mb-2">
                    Fecha de Salida Prevista
                  </label>
                  <Input
                    id="fechaSalida"
                    type="date"
                    value={fechaSalida}
                    onChange={(e) => setFechaSalida(e.target.value)}
                  />
                </div>
              </div>
            ) : (
              <div className="flex flex-col">
                <label htmlFor="fechaPintadoEmbolsado" className="mb-2">
                  {selectedElaboracion.formula.queso.item.nombre.toLowerCase() === 'pategras' ? 'Fecha de Pintado' : 'Fecha de Embolsado'}
                </label>
                <Input
                  id="fechaPintadoEmbolsado"
                  type="date"
                  value={fechaPintadoEmbolsado}
                  onChange={(e) => setFechaPintadoEmbolsado(e.target.value)}
                />
              </div>
            )}
          </div>
          <DialogFooter>
            <Button onClick={handleSubmitModal} className="w-full sm:w-auto bg-green-500 hover:bg-green-600 text-white rounded-md transition-colors inline-block font-semibold">Guardar</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={isCorteModalOpen} onOpenChange={setIsCorteModalOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Agregar Corte</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <label htmlFor="corte" className="text-sm font-medium">
  Tipo de Corte
</label>
<select
  id="corte"
  value={newCorte.corte}
  onChange={(value) => setNewCorte({...newCorte, corte: value.target.value})}
  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
>
  <option value="">Seleccione un tipo de corte</option>
  {selectedElaboracion && ['pategras', 'barra'].includes(selectedElaboracion.formula.queso.item.nombre.toLowerCase()) ? (
    <option value="Entero">Entero</option>
  ) : (
    tiposCorte.map((tipo) => (
      <option key={tipo} value={tipo}>
        {tipo}
      </option>
    ))
  )}
</select>
</div>
<div className="grid gap-2">
<label htmlFor="cantidad" className="text-sm font-medium">
  Cantidad
</label>
<Input
  id="cantidad"
  type="number"
  value={newCorte.cantidad}
  onChange={(e) => setNewCorte({...newCorte, cantidad: parseFloat(e.target.value)})}
/>
</div>
<div className="grid gap-2">
<label htmlFor="peso" className="text-sm font-medium">
  Peso (kg)
</label>
<Input
  id="peso"
  type="number"
  value={newCorte.peso}
  onChange={(e) => setNewCorte({...newCorte, peso: parseFloat(e.target.value)})}
/>
</div>
</div>
<DialogFooter>
<Button onClick={handleSubmitCorte} className="w-full sm:w-auto bg-green-500 hover:bg-green-600 text-white rounded-md transition-colors inline-block font-semibold">Guardar</Button>
</DialogFooter>
</DialogContent>
</Dialog>

<Dialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
<DialogContent className="sm:max-w-[425px]">
<DialogHeader>
  <DialogTitle>Confirmar eliminación</DialogTitle>
  <DialogDescription>
    ¿Está seguro de que desea eliminar esta elaboración? Esta acción no se puede deshacer.
  </DialogDescription>
</DialogHeader>
<DialogFooter>
  <Button variant="outline" onClick={() => setIsDeleteDialogOpen(false)} className="w-full sm:w-auto bg-yellow-500 hover:bg-yellow-600 text-black rounded-md transition-colors inline-block font-semibold">Cancelar</Button>
  <Button variant="destructive" onClick={handleDeleteElaboracion} className="w-full sm:w-auto bg-red-500 hover:bg-red-600 text-white rounded-md transition-colors inline-block font-semibold">Eliminar</Button>
</DialogFooter>
</DialogContent>
</Dialog>
</div>
)
}