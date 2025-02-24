'use client'

import { useNavbar } from '@/contexts/NavbarContext';
import Link from 'next/link'
import { useEffect, useState } from 'react';
import { Bell, Menu } from 'lucide-react'
import { Button } from "@/components/ui/button"
import { useDespachoNuevo } from '@/hooks/useDespachoNuevo';
import { useControlStockNuevo } from '@/hooks/useControlStockNuevo';
import { useModificacionesNuevas } from '@/hooks/useModificacionesNuevas';
import { useFAQ } from '@/contexts/FAQContext';

import {
  Sheet,
  SheetContent,
  SheetTrigger,
} from "@/components/ui/sheet"

export default function Navbar() {
  const { reload, reloadNavbar } = useNavbar();
  const { reloadFAQ } = useFAQ();
  
  const [user, setUser] = useState(null);
  const [rol, setRol] = useState('');
  
  const { despachoNuevo } = useDespachoNuevo()
  const { nuevos, checkNewControl } = useControlStockNuevo()
  const { nuevasModificaciones, checkNewModification} = useModificacionesNuevas()

  const [isOpen, setIsOpen] = useState(false)


  useEffect(() => {
    handleUser()
  }, [])

  useEffect(() => {
    checkNewModification()
    checkNewControl()
    
    handleUser()
  }, [reload, nuevasModificaciones, nuevos])

  const handleEnterModifications = () => {
    reloadNavbar()
  }

  
  const handleUser = () => {
    const usuarioLogeado = window.sessionStorage.getItem("usuarioLogeado");
    
    if(usuarioLogeado) {
      const user = JSON.parse(usuarioLogeado)
      setUser(user)
      setRol(user.rol)

    }
  }

  const NavLink = ({ href, onClick, children }: { href: string; onClick?: () => void; children: React.ReactNode }) => (
    <Link 
      href={href} 
      className="hover:text-indigo-200 transition-colors font-semibold block py-2"
      onClick={() => {
        setIsOpen(false)
        onClick && onClick()
      }}
    >
      {children}
    </Link>
  )

  const NavContent = () => (
    <>
      <NavLink href="/">Home</NavLink>
      
      {user === null && <NavLink href="/login">Log In</NavLink>}

      {rol === "Operario" && (
        <>
          <NavLink href="/elaboraciones">Elaboraciones</NavLink>
          <NavLink href="/lotes">Lotes</NavLink>
          <NavLink href="/controlesStock/nuevo">Control de stock</NavLink>
          <NavLink href="/despachos/miDespacho">
            <span className="flex items-center">
              Despacho
              {despachoNuevo && <Bell className="ml-1 h-4 w-4 text-yellow-300 animate-pulse" />}
            </span>
          </NavLink>
        </>
      )}

      {rol === "Gerente" && (
        <>
          <NavLink href="/usuarios">Usuarios</NavLink>
          <NavLink href="/elaboraciones/informe">Informe de elaboraciones</NavLink>
          <NavLink href="/despachos/informe">Informe de despachos</NavLink>
          <NavLink href="/compras/informe">Informe de compra</NavLink>
        </>
      )}

      {rol === "Subgerente" && (
        <>
          <NavLink href="/controlesStock" onClick={handleEnterModifications}>
            <span className="flex items-center">
              Control de stock
              {nuevos && <Bell className="ml-1 h-4 w-4 text-yellow-300 animate-pulse" />}
            </span>
          </NavLink>
          <NavLink href="/despachos">Despachos</NavLink>
          <NavLink href="/modificaciones" onClick={handleEnterModifications}>
            <span className="flex items-center">
              Modificaciones de lotes
              {nuevasModificaciones && <Bell className="ml-1 h-4 w-4 text-yellow-300 animate-pulse" />}
            </span>
          </NavLink>
          <NavLink href="/compras">Compra de insumos</NavLink>
          <NavLink href="/compras/listar">Comprobantes</NavLink>
          <NavLink href="/formulas">Fórmulas</NavLink>
          <NavLink href="/insumos">Insumos</NavLink>
          <NavLink href="/vehiculos">Vehículos</NavLink>
          <NavLink href="/destinos">Destinos</NavLink>
          <NavLink href="/proveedores">Proveedores</NavLink>
        </>
      )}
      
      {user !== null && (
        <NavLink 
          href="/login" 
          onClick={() => {
            setRol("")
            setUser(null)
            window.sessionStorage.removeItem("usuarioLogeado")
            reloadFAQ()
          }}
        >
          Salir
        </NavLink>
      )}
    </>
  )

  return (
    <nav className="bg-indigo-600 text-white p-4 shadow-md">
      <div className="container mx-auto">
        <div className="flex justify-between items-left">
          {/* Desktop menu */}
          <ul className="hidden xl:flex space-x-6">
            <NavContent />
          </ul>

          {/* Mobile menu */}
          <Sheet open={isOpen} onOpenChange={setIsOpen}>
            <SheetTrigger asChild>
              <Button variant="ghost" className="p-0 xl:hidden">
                <Menu className="h-6 w-6" />
                <span className="sr-only">Toggle menu</span>
              </Button>
            </SheetTrigger>
            <SheetContent side="left" className="w-[300px] sm:w-[400px] bg-indigo-600">
              <nav className="flex flex-col gap-4">
                <NavContent />
              </nav>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </nav>
  )
}
