'use client'

import { useState, useEffect } from 'react'
import { Button } from "@/components/ui/button"
import { HelpCircle } from 'lucide-react'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"
import { useFAQ } from '@/contexts/FAQContext'

interface FAQItem {
  question: string
  answer: string
}

const faqsByRole: Record<string, FAQItem[]> = {
  Operario: [
    {
      question: "¿Cómo registro una nueva elaboración?",
      answer: "Para registrar una nueva elaboración, ve a la sección 'Elaboraciones' y haz clic en 'Nueva Elaboración'. Completa el formulario con los detalles requeridos y guarda los cambios. Si quieres, puedes listar las elaboraciones ya creadas por su producto y de ser necesario entre fechas."
    },
    {
      question: "¿Qué hago si hay una discrepancia en el inventario?",
      answer: "Si encuentras una discrepancia en el inventario, registra inmediatamente la diferencia en el sistema de control de stock, modifica los valores buscando el lote en la pestaña 'Lotes' y notifica a tu supervisor."
    },
    {
      question: "¿Cómo funciona la pestaña de despachos?",
      answer: "En la pestaña se va a desplegar la información de un despacho que se le ha sido asignado, en caso de no tener alguno se le mostrará un mensaje indicandolo, en caso contrario verá una campanita en la pestaña. Al momento de iniciar el viaje debe tocar el botón inferior y una vez terminado deberá realizar la misma acción para que el despacho se cierre y tanto usted como su vehículo utilizado quede disponible para un nuevo despacho."
    }
  ],
  Subgerente: [
    {
      question: "¿Cómo dejo disponible un vehículo si hay un error con el despacho?",
      answer: "Ingresa a la pestaña de 'vehículos', selecciona 'Ver Detalles' en el vehículo deseado y toca el botón 'Cambiar Disponibilidad'"
    },
    {
      question: "No carga el formulario de pago",
      answer: "Si el formulario de pago para los insumos no termina de cargar, intenta recargando la página, en caso de que siga sin cargar comuníquese con soporte o intente dentro de un rato, el servicio de Mercado Libre puede estar caido."
    },
    {
      question: "¿Cómo asigno tareas a los operarios?",
      answer: "Ve a la sección 'Despachos', selecciona 'Agregar Nuevo Despacho', ingresa los datos requeridos, elige a uno de los operarios disponibles de la lista y su vehículo correspondiente. Tenga en cuenta de que si un operario o un vehículo no se muestra en la lista, es porque alguno está con un despacho asignado sin terminar."
    }
  ],
  Gerente: [
    {
      question: "Nombre de usuario inexistente está ocupado",
      answer: "El error del nombre de usuario ocupado que surge aún sin mostrarse un usuario con el mismo nombre, se debe a que previamente estaba ocupado el nombre y no se puede desocupar debido a que sigue ligado a distintos procesos como las elaboraciones, controles de stock, despachos o modificaciones de lotes."
    },
    {
      question: "¿Cómo cambio el rol de un usuario?",
      answer: "El rol solo puede ser rasignado creando un nuevo usuario con otro nombre y rol, ya que el previo permanecerá ocupado, y comunicarlo con el usuario correspondiente. Se recomienda eliminar el usuario anterior en caso de que el mismo ya no cumpla con las mismas funciones previas."
    },
    {
      question: "No hay datos de un insumo en los informes de compras",
      answer: "Esto se debe a que no se realizó ninguna compra del insumo durante el periodo de tiempo seleccionado."
    }
  ]
}

export function FAQButton() {
  const [isOpen, setIsOpen] = useState(false)
  const [userRole, setUserRole] = useState<string | null>(null)
  const { reload } = useFAQ()

  useEffect(() => {
    const updateUserRole = () => {
      const user = JSON.parse(sessionStorage.getItem('usuarioLogeado') || '{}')
      setUserRole(user.rol || null)
    }

    updateUserRole()
    window.addEventListener('storage', updateUserRole)

    return () => {
      window.removeEventListener('storage', updateUserRole)
    }
  }, [])

  useEffect(() => {
    const user = JSON.parse(sessionStorage.getItem('usuarioLogeado') || '{}')
    setUserRole(user.rol || null)
  }, [reload])

  if (!userRole) return null

  const faqs = faqsByRole[userRole] || []

  return (
    <>
      <Button
        className="fixed bottom-4 right-4 rounded-full w-12 h-12 shadow-lg z-50 md:w-14 md:h-14"
        onClick={() => setIsOpen(true)}
      >
        <HelpCircle className="w-6 h-6 md:w-8 md:h-8" />
        <span className="sr-only">Preguntas Frecuentes</span>
      </Button>
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent className="sm:max-w-[425px] max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle className="text-left text-xl md:text-2xl">Preguntas Frecuentes</DialogTitle>
          </DialogHeader>
          <Accordion type="single" collapsible className="w-full">
            {faqs.map((faq, index) => (
              <AccordionItem key={index} value={`item-${index}`}>
                <AccordionTrigger className="text-left text-sm md:text-base">{faq.question}</AccordionTrigger>
                <AccordionContent className="text-sm md:text-base">
                  {faq.answer}
                </AccordionContent>
              </AccordionItem>
            ))}
          </Accordion>
        </DialogContent>
      </Dialog>
    </>
  )
}